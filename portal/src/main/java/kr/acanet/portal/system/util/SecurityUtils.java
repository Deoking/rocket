package kr.acanet.portal.system.util;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import kr.acanet.portal.system.model.Role;
import kr.acanet.portal.system.security.PortalAuthority;

/**
 * <pre>
 * description : 스프링 시큐리티 관련 유틸클래스
 * </pre>
 *
 * @author 	: wdkang
 * @date 	: 2018. 1. 18.
 * @version : 1.0
 */
public class SecurityUtils {
	
	/**
	 * <pre>
	 * description 	:현재 로그인한 사용자의 인증정보를 가져온다.
	 * </pre>
	 *
	 * @return
	 */
	public static Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}
	
	/**
	 * <pre>
	 * description 	: 현재 사용자가 인증받은 사용자인지 체크한다.
	 * </pre>
	 *
	 * @return
	 */
	public static boolean isAuthenticated() {
		Authentication auth = getAuthentication();
		
		if(auth == null) {
			return false;
		}
		
		return auth.isAuthenticated();
	}
	
	/**
	 * <pre>
	 * description 	: 현재 사용자가 로그인 유지기능으로 인증받은 사용자인지 체크한다.
	 * </pre>
	 *
	 * @return
	 */
	public static boolean isRememberMeAuthenticated() {
		Authentication auth = getAuthentication();
		
		if(auth == null) {
			return false;
		}
		
		return RememberMeAuthenticationToken.class.isAssignableFrom(auth.getClass());
	}
	
	/**
	 * <pre>
	 * description 	:현재 사용자가 해당 권한을 가지고있는지 체크한다.
	 * </pre>
	 *
	 * @param role
	 * @return
	 */
	public static boolean hasRole(Role role){
		PortalAuthority authority = new PortalAuthority(role);
		return getAuthentication().getAuthorities().contains(authority);
	}
	
	/**
	 * <pre>
	 * description : 현재 사용자가 익명유저인지 체크
	 * </pre>
	 *
	 * @return
	 */
	public static boolean isAnonymous() {
		Authentication auth = getAuthentication();
		
		if(auth instanceof AnonymousAuthenticationToken) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * <pre>
	 * description : 자동로그인인지 체크
	 * </pre>
	 *
	 * @return
	 */
	public static boolean isRememberMe() {
		Authentication auth = getAuthentication();
		
		if(auth instanceof RememberMeAuthenticationToken) {
			return true;
		}else {
			return false;
		}
	}
	
	
	
	
 }
 