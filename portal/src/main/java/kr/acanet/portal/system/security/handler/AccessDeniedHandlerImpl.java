package kr.acanet.portal.system.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.AccessDeniedHandler;

import kr.acanet.portal.system.util.SecurityUtils;

public class AccessDeniedHandlerImpl implements AccessDeniedHandler{

	private static final Logger logger = LoggerFactory.getLogger(AccessDeniedHandlerImpl.class);

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception)
			throws IOException, ServletException {
		Authentication auth = SecurityUtils.getAuthentication();
      if (auth != null) {
    	  logger.warn("User: {} attempted to access the protected URL: {}", auth.getName(), request.getRequestURI());
      }

      response.sendRedirect("/forbidden");
	}

}
