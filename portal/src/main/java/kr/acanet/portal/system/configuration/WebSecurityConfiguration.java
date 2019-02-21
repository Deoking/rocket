package kr.acanet.portal.system.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterInvocation;

import kr.acanet.portal.system.security.UserSecurityService;
import kr.acanet.portal.system.security.handler.AccessDeniedHandlerImpl;
import kr.acanet.portal.system.security.handler.AuthenticationFailureHandlerImpl;
import kr.acanet.portal.system.security.handler.AuthenticationSuccessHandlerImpl;
import kr.acanet.portal.system.security.handler.LogoutSuccessHandlerImpl;
import kr.acanet.portal.system.security.handler.SecurityExpressionHandlerImpl;
import kr.acanet.portal.system.security.provider.CustomAuthenticationProvider;

/**
 * 스프링 시큐리티 세팅
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfiguration.class);

	// 로그인 인증을 담당하는 커스텀 인증프로바이더.
	@Autowired
	CustomAuthenticationProvider customAuthenticationProvider;

	/*
	 * @Autowired private MessageSourceAccessor messageSourceAccessor;
	 */

	/*
	 * @Autowired private DataSource adminDataSource;
	 */

	@Autowired
	private UserSecurityService userService;

	/**
	 * <pre>
	 * description 	:인증과정(로그인)에서 사용할 프로바이더등록
	 * </pre>
	 *
	 * @param authenticationManager
	 * @throws Exception
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder authenticationManager) throws Exception {
		authenticationManager.authenticationProvider(customAuthenticationProvider);
	}

	/**
	 * <pre>
	 * description 	:스프링시큐리티에서 무시할 URL 지정.
	 * (필터단에서 수행되기 떄문에 스프링 설정에서 디폴트서블릿에 선언을 했어도 정의해주어야한다.)
	 * </pre>
	 *
	 * @param web
	 * @throws Exception
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**", "/favicon.ico", "/upload/**");
		// 태그라이브러리에도 동일한 검증메서드를 사용하기위해 새로 구현한 expressionhandler를 등록
		web.expressionHandler(securityWebExpressionHandler());
	}

	/**
	 * <pre>
	 * description 	:URL 접근 설정.
	 * </pre>
	 *
	 * @param httpSecurity
	 * @throws Exception
	 */

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		/*
		 * // 로그인 유지기능 설정 if (Boolean.parseBoolean(messageSourceAccessor.getMessage(
		 * "system.login.rememberme.enabled"))) {
		 * 
		 * String rememberMeType =
		 * messageSourceAccessor.getMessage("system.login.rememberme.type", "WEEK"); int
		 * rememberMePeriod = Integer
		 * .parseInt(messageSourceAccessor.getMessage("system.login.rememberme.period",
		 * "2"));
		 * 
		 * logger.info("enabled the remember-me in the application. ");
		 * logger.info("remember-me info - type : {}, period : {} ", rememberMeType,
		 * rememberMePeriod);
		 * 
		 * if ("HOUR".equals(rememberMeType)) { rememberMePeriod = rememberMePeriod *
		 * 3600; } else if ("DAY".equals(rememberMeType)) { rememberMePeriod =
		 * rememberMePeriod * (3600 * 24); } else if ("WEEK".equals(rememberMeType)) {
		 * rememberMePeriod = rememberMePeriod * (3600 * 24 * 7); } else if
		 * ("MONTH".equals(rememberMeType)) { rememberMePeriod = rememberMePeriod *
		 * (3600 * 24 * 30); } else if ("YEAR".equals(rememberMeType)) {
		 * rememberMePeriod = rememberMePeriod * (3600 * 24 * 30 * 12); }
		 * 
		 * httpSecurity.rememberMe().rememberMeParameter("rememberme").tokenRepository(
		 * persistentTokenRepository()) .tokenValiditySeconds(rememberMePeriod) //
		 * .authenticationSuccessHandler(authenticationSuccessHandler())
		 * .userDetailsService(userService);
		 * 
		 * }
		 */
		httpSecurity
				// 세션관리
				.sessionManagement().and()
				// .maximumSessions(Integer.parseInt(messageSourceAccessor.getMessage("security.session.maximum")))
				// .expiredUrl("/login").and().and()
				// 권한에 맞지 않는 URL에 접근시 접근거부로직을 수행할 핸들러 지정.
				.exceptionHandling().accessDeniedHandler(accessDeniedHandler()).and().authorizeRequests()
				// Role 체크 expression 등록
				// 스프링 시큐리티에서는 기본적으로 권한검증시 prefix로 'ROLE_'를 붙여 검증하기 때문에 해당 기능을 없애기 위해 재구현함.
				// 기존 -> Role코드가 '0000'일떄 -> 스프링시큐리티에서는 'ROLE_0000'으로 검증.
				// 변경 -> Role코드가 '0000'이면 '0000'으로 검증.
				.expressionHandler(securityWebExpressionHandler())

				// 인증없이 접근 가능한 URL
				.antMatchers("/", "/login/form", "/test/*", "/login/**", "/logout", "/forbidden").permitAll()
				.antMatchers("/**/**").access("isAccessible()").anyRequest().authenticated().and()
				// 로그인 페이지 설정 및 로그인 로직 수행 URL, 파라미터 설정. loginProcessingUrl은 보이기만하는 fake URL
				.formLogin().usernameParameter("username").passwordParameter("password")
				// 로그인페이지로 사용할 URL 지정
				.loginPage("/login").permitAll().loginProcessingUrl("/login/process")
				// 인증 성공 핸들러
				.successHandler(authenticationSuccessHandler())
				// 인증 실패 핸들러
				.failureHandler(authenticationFailureHandler()).and()

				// 로그아웃 설정
				.logout().logoutUrl("/logout")
				// 로그아웃 성공 핸들러
				.logoutSuccessHandler(logoutSuccessHandler()).invalidateHttpSession(true).and().csrf().disable();
	}

	/**
	 * <pre>
	 * description 	: 로그인 인증 완료 핸들러
	 * </pre>
	 *
	 * @return
	 */
	@Bean
	protected AuthenticationSuccessHandlerImpl authenticationSuccessHandler() {
		return new AuthenticationSuccessHandlerImpl();
	}

	/**
	 * <pre>
	 * description 	: 로그인 인증 실패 핸들러(권한 체크가 아닌 로그인 시 발생하는 예외 처리 핸들러.)
	 * </pre>
	 *
	 * @return
	 */
	@Bean
	protected AuthenticationFailureHandlerImpl authenticationFailureHandler() {
		return new AuthenticationFailureHandlerImpl();
	}

	/**
	 * <pre>
	 * description 	: 접근권한 제어 핸들러(요청 URL에대해 사용자가 권한이 없을경우 처리)
	 * </pre>
	 *
	 * @return
	 */
	@Bean
	public AccessDeniedHandlerImpl accessDeniedHandler() {
		return new AccessDeniedHandlerImpl();
	}

	/**
	 * <pre>
	 * description 	: 로그아웃 완료 핸들러
	 * </pre>
	 *
	 * @return
	 */
	@Bean
	public LogoutSuccessHandlerImpl logoutSuccessHandler() {
		return new LogoutSuccessHandlerImpl();
	}

	/**
	 * <pre>
	 * description 	: 커스텀 권한체크 핸들러
	 * 	URL 접근제어 세팅 시 권한체크를 스피링 시큐리티방식(ROLE_**)이 아닌 사용자 지정방식으로 재구현.
	 * </pre>
	 *
	 * @return
	 */
	@Bean
	public SecurityExpressionHandler<FilterInvocation> securityWebExpressionHandler() {
		return new SecurityExpressionHandlerImpl();
	}

	/**
	 * <pre>
	 * description 	:패스워드 인코더
	 * </pre>
	 *
	 * @return
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * <pre>
	 * description 	: 로그인유지기능 사용 시 쿠키 및 DB에 기록하여 유지.
	 * </pre>
	 *
	 * @return
	 */

	/*
	 * @Bean public PersistentTokenRepository persistentTokenRepository() {
	 * CustomJdbcTokenRepositoryProvider jdbcToken = new
	 * CustomJdbcTokenRepositoryProvider();
	 * //jdbcToken.setDataSource(adminDataSource); return jdbcToken; }
	 */

}
