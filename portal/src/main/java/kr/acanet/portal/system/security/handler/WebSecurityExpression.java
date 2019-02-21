package kr.acanet.portal.system.security.handler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.WebSecurityExpressionRoot;
import org.springframework.util.StringUtils;

import kr.acanet.portal.system.model.Permission;
import kr.acanet.portal.system.security.PortalAuthority;

public class WebSecurityExpression extends WebSecurityExpressionRoot {

	private static final Logger logger = LoggerFactory.getLogger(WebSecurityExpression.class);

	public WebSecurityExpression(Authentication a, FilterInvocation fi) {
		super(a, fi);
	}

	/**
	 * <pre>
	 * description 	: hasRole() 외에 추가로 퍼미션 체크용으로 hasPermission() 메서드 생성.
	 * </pre>
	 *
	 * @param permission
	 * @return
	 */
	public boolean hasPermission(String permission) {
		return hasAnyPermission(permission);
	}

	public boolean hasAnyPermission(String... permissionArray) {

		if (isAnonymous()) {
			return false;
		}

		if (isRememberMe()) {
		}

		Set<String> permissions = new HashSet<String>();

		Authentication authentication = getAuthentication();

		// User details = (User) authentication.getDetails();

		@SuppressWarnings("unchecked")
		List<GrantedAuthority> roleList = (List<GrantedAuthority>) authentication.getAuthorities();

		for (GrantedAuthority grantedAuthority : roleList) {

			PortalAuthority authohrity = (PortalAuthority) grantedAuthority;

			for (Permission permission : authohrity.getPermission()) {
				permissions.add(permission.getPermissionId());
			}

			for (String permission : permissionArray) {

				if (permissions.contains(permission)) {
					return true;
				}

			}

		}

		return false;
	}

	/**
	 * <pre>
	 * description : URI를 분석하여 접근가능한지 체크
	 * </pre>
	 *
	 * @return
	 */
	public boolean isAccessible() {

		Authentication authentication = getAuthentication();

		String requestUri = request.getRequestURI();

		// 익명의 사용자일 경우 접근 제한
		if (isAnonymous()) {
			return false;
		}

		// 인증받은 사용자중
		if (isAuthenticated()) {
			// 아이디 및 비밀번호로 로그인한 사용자
			if (isFullyAuthenticated() || isRememberMe()) {

				logger.debug("isFullyAuthenticated : {}, isRememberMe : {}", isFullyAuthenticated(), isRememberMe());

				@SuppressWarnings("unchecked")
				List<GrantedAuthority> roleList = (List<GrantedAuthority>) authentication.getAuthorities();

				for (GrantedAuthority grantedAuthority : roleList) {

					PortalAuthority authohrity = (PortalAuthority) grantedAuthority;

					for (Permission permission : authohrity.getPermission()) {
						String[] urls = StringUtils.delimitedListToStringArray(permission.getPermissionUrl(), ",");

						for (String url : urls) {

							if (requestUri.startsWith(request.getContextPath() + url)) {
								logger.debug("request uri : {}...accessbile!", requestUri);
								return true;
							}
						}
					}
				}
			}
		}

		boolean ajax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

		// ajax 요청
		if (ajax) {

		}
		// non-ajax 요청
		else {

		}

		logger.debug("request uri : {}...access fail!", requestUri);
		return false;
	}

}
