package kr.acanet.portal.system.security.handler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;

public class AuthenticationFailureHandlerImpl implements AuthenticationFailureHandler{

private static final Logger logger = LoggerFactory.getLogger(AuthenticationFailureHandlerImpl.class);
	
	/*
	 * @Autowired private MessageSourceAccessor messageSourceAccessor;
	 */
	
	@Override
	@SuppressWarnings("unchecked")
	public void onAuthenticationFailure(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {
		
		logger.info("exception : {}, message : {}", exception.getClass().getSimpleName(), exception.getMessage());
		/*
		 * //에러메시지는 기본값으로 우선 설정 String errorMsg =
		 * messageSourceAccessor.getMessage("message.login.error.default");
		 * 
		 * //사용자가 존재하지 않을 시 if(exception instanceof UsernameNotFoundException) {
		 * errorMsg = messageSourceAccessor.getMessage("message.login.error.username");
		 * response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		 * 
		 * //비밀번호가 틀렸을 시 }else if(exception instanceof BadCredentialsException) {
		 * errorMsg = messageSourceAccessor.getMessage("message.login.error.password");
		 * response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		 * 
		 * //아이디가 만료되었을 시 }else if(exception instanceof AccountExpiredException) {
		 * errorMsg =
		 * messageSourceAccessor.getMessage("message.login.error.accountexpired");
		 * response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		 * 
		 * //비밀번호가 만료되었을 시 }else if(exception instanceof CredentialsExpiredException) {
		 * errorMsg =
		 * messageSourceAccessor.getMessage("message.login.error.passwordexpired");
		 * response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		 * 
		 * //사용할수 없는 사용자 }else if(exception instanceof DisabledException) { errorMsg =
		 * messageSourceAccessor.getMessage("message.login.error.enabled");
		 * response.setStatus(HttpServletResponse.SC_FORBIDDEN); }
		 * 
		 * //이중로그인 시도시 else if(exception instanceof SessionAuthenticationException) {
		 * errorMsg =
		 * messageSourceAccessor.getMessage("message.login.error.multilogin");
		 * response.setStatus(HttpServletResponse.SC_FORBIDDEN); }
		 * 
		 * //아이디저장 오류시 else if(exception instanceof CookieTheftException) { errorMsg =
		 * messageSourceAccessor.getMessage("message.login.error.rememberme");
		 * response.setStatus(HttpServletResponse.SC_FORBIDDEN); }
		 * 
		 * //그외 오류시 else { errorMsg =
		 * messageSourceAccessor.getMessage("message.login.error.server");
		 * response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); }
		 */
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		JSONObject jObj = new JSONObject();
		
		//jObj.put("message", errorMsg);
		
		PrintWriter writer = response.getWriter();
		writer.println(jObj.toJSONString());
		
		writer.flush();
		
	}

}
