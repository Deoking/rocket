package kr.acanet.portal.system.security.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import kr.acanet.portal.system.model.User;
import kr.acanet.portal.system.security.UserSecurityService;

public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

	private static final Logger logger = LoggerFactory.getLogger(AuthenticationSuccessHandlerImpl.class);

	@Autowired
	UserSecurityService userService;

	// Spring security의 로직으로 인하여(권한 없이 특정 페이지를 요청하여 로그인페이지로 갔을경우) 로그인페이지로 이동전 요청한
	// 페이지의 정보를 저장한 객체.
	private RequestCache requestCache = new HttpSessionRequestCache();

	private String defaultUrl = "/dashboard/main";

	@Override
	@SuppressWarnings("unchecked")
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		String ip = null;

		ip = request.getHeader("X-FORWARDED-FOR");
		if (ip == null)
			ip = request.getRemoteAddr();

		// 로그인기록 insert
		User user = (User) authentication.getDetails();
		HashMap params = new HashMap();
		params.put("username", user.getUsername());
		params.put("roleId", user.getRole().getRoleId());
		params.put("ip", ip);
		params.put("useragent", request.getHeader("User-Agent"));

		//userService.mergeLoginHistoryAndLastLoginDate(params);

		String redirectUrl = request.getContextPath() + defaultUrl;

		// SavedRequest 객체를 가져옴
		SavedRequest savedRequest = requestCache.getRequest(request, response);

		// SavedRequest 객체가 없다면 사용자가 직접적으로 로그인페이지를 요청 -> dafault url로 이동.
		if (savedRequest != null) {

			// SavedRequest객체가 있다면 원래의 redirecturl을 가져옴.
			redirectUrl = savedRequest.getRedirectUrl();

			logger.info("saved redirect url - {}", redirectUrl);

		}

		JSONObject jObj = new JSONObject();

		jObj.put("redirect", redirectUrl);

		logger.info("redirect to ...{}", redirectUrl);

		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);

		PrintWriter writer = response.getWriter();
		writer.println(jObj.toJSONString());

		writer.flush();

	}

}
