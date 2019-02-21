package kr.acanet.portal.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import kr.acanet.portal.system.util.BrowserUtil;
import kr.acanet.portal.system.util.SecurityUtils;

/**
 * 포털 진입 및 로그인 요청 처리 컨트롤러
 */
@Controller
public class LoginController {
	
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	/**
	 * 루트접근
	 */
	@RequestMapping("/")
	public String entrance(HttpServletRequest request, HttpServletResponse response) {
		
		logger.debug("Portal session checking...");
		
		String redirect = "redirect:/login/form";
		
		// 인증유저
		if(SecurityUtils.isAuthenticated()) {
			logger.debug("Portal session exists.");
			logger.debug("Checking for anonymous user...");
			
			if(!SecurityUtils.isAnonymous()) {
				// 익명의 사용자가 아닐경우
				logger.debug("This connection is not an anonymous user.");
				redirect = "redirect:/portal/";
			}else {
				// 익명의 사용자일 경우
				logger.debug("This connection is an anonymous user.");
			}
		}else {
			logger.debug("Portal session does not exist. go to login page");
		}
		
		// 미인증 사용자는 로그인페이지로..
		return redirect;
	}
	
	/**
	 * 로그인 페이지
	*/
	@RequestMapping(value = "/login/form", method = RequestMethod.GET)
	public String loginForm(HttpServletRequest request, HttpServletResponse response, Model model) {
		
		BrowserUtil browserUtil = new BrowserUtil(request);
	
		logger.debug("Login form access.");
		
		logger.debug("----------------------------------------------");
		logger.debug("index referer : " + request.getHeader("referer"));
		logger.debug("index remote addr : " + request.getRemoteAddr());
		logger.debug("index user agent : " + browserUtil.getOriginalUserAgent());
		logger.debug("index user OS : " + browserUtil.getOs() + ", Browser : " + browserUtil.getBrowser());
		logger.debug("----------------------------------------------");
		
		String redirect = "/login/form";
		
		return redirect;
	}
	
	/**
	 * 로그인
	*/
	@RequestMapping(value = "/login/process", method = RequestMethod.POST)
	public String doLogin(HttpServletRequest request, HttpServletResponse response, Model model) {
		String redirect = "tiles/common/login";
		
		if(SecurityUtils.isAuthenticated()) {
			if(!SecurityUtils.isAnonymous()) {
				if(!SecurityUtils.isRememberMe()) {
					redirect = "redirect:/dashboard/main";
				}
			}
		}
		return redirect;
	}
}
