package kr.acanet.portal.system.security.provider;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import kr.acanet.portal.system.model.User;
import kr.acanet.portal.system.security.UserSecurityService;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider{

	private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);
	
	@Autowired
	UserSecurityService userService;
	
	/*
	 * @Autowired PasswordEncoder passwordEncoder;
	 */
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		logger.info("starting authenticate...");
		
		String username = (String) authentication.getPrincipal();		
		
		String password = (String) authentication.getCredentials();
		
		User user = (User) userService.loadUserByUsername(username);
		
		if(user == null) {
			throw new UsernameNotFoundException("사용자를 찾을 수 없습니다 - '" + username +"'");
		}
		/*
		 * if(!userService.validPassword(user, password)) { throw new
		 * BadCredentialsException("비밀번호가 일치하지 않습니다."); }
		 */
		if(!user.isAccountNonExpired()) {
			
			if(user.getIdExpiredDate() == null || user.getIdExpiredDate().compareTo(new Date()) < 0) {
				throw new AccountExpiredException("아이디가 만료되었습니다.");
			}
		}
		
		if(!user.isCredentialsNonExpired()) {
			if(user.getPasswordExpiredDate() == null || user.getPasswordExpiredDate().compareTo(new Date()) < 0) {
				throw new CredentialsExpiredException("비밀번호가 만료되었습니다.");
			}
		}
		
		if(!user.isEnabled()) {
			throw new DisabledException("해당 사용자는 더이상 사용할 수 없습니다.");
		}
		
		//사용자 및 권한이 정상적으로 존재하면 토큰생성
		//Authentication token = userService.createTokenByUserAuthentication(user);
		
		//return token;
		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		// TODO Auto-generated method stub
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
