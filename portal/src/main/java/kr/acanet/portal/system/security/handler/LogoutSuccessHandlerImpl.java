package kr.acanet.portal.system.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

	private static final Logger logger = LoggerFactory.getLogger(LogoutSuccessHandlerImpl.class);

	/*
	 * @Autowired MessageSourceAccessor messageSourceAccessor;
	 */

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		logger.info("logout success.");
		response.sendRedirect(request.getContextPath() + "/login");

	}

}
