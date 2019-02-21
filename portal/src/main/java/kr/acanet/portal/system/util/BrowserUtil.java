package kr.acanet.portal.system.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrowserUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(BrowserUtil.class);

	// 검색될 브라우저 종류
	enum Browser {
		EDGE, IE11, IE10, IE9, IE8, IE7, IE6, IE5, IE4, FIREFOXIOS, FIREFOX, CHROMEIOS, CHROME, OPERAIOS, OPERA15, OPERA, SAFARI, WHALE, UNKNOWN
	}

	// 검색될 운영체제 종류
	enum OS {
		WINDOWS10, WINDOWS81, WINDOWS8, WINDOWS7, WINDOWSVISTA, WINDOWS2003, WINDOWSXP, WINDOWS2000, WINDOWSNT40, WINDOWSME, WINDOWS98, WINDOWS95, WINDOWSCE, IPHONE, IPAD, MAC, ANDROID, LINUX, UNIX, UNKNOWN
	}
	
	private String originalUserAgent;
	private String userAgent;
	private OS os;
	private Browser browser;

	private static final Map<OS, String> OS_IDS;
	private static final Map<Browser, String> BROWSER_IDS;

	static {
		// OS enum에 대응하는 검색될 string을 순서대로 등록
		final Map<OS, String> osIds = new LinkedHashMap<OS, String>();
		osIds.put(OS.WINDOWS10, "windows nt 10.0"); // Windows 10
		osIds.put(OS.WINDOWS81, "windows nt 6.3"); // Windows 8.1
		osIds.put(OS.WINDOWS8, "windows nt 6.2");
		osIds.put(OS.WINDOWS7, "windows nt 6.1");
		osIds.put(OS.WINDOWSVISTA, "windows nt 6.0");
		osIds.put(OS.WINDOWS2003, "windows nt 5.2");
		osIds.put(OS.WINDOWSXP, "windows nt 5.1");
		osIds.put(OS.WINDOWS2000, "windows nt 5.0");
		osIds.put(OS.WINDOWSNT40, "windows nt 4");
		osIds.put(OS.WINDOWSME, "windows 98; win 9x 4.90"); // WINDOWS98 전에 WINDOWSME 체킹
		osIds.put(OS.WINDOWS98, "windows 98");
		osIds.put(OS.WINDOWS95, "windows 95");
		osIds.put(OS.WINDOWSCE, "windows ce");
		osIds.put(OS.IPHONE, "iphone"); // MAC 전에 IPHONE 체킹
		osIds.put(OS.IPAD, "ipad"); // MAC 전에 IPAD 체킹
		osIds.put(OS.MAC, "mac");
		osIds.put(OS.ANDROID, "android");  // LINUX 전에 ANDROID 체킹
		osIds.put(OS.LINUX, "linux");
		osIds.put(OS.UNIX, "unix");
		osIds.put(OS.UNKNOWN, "");
		OS_IDS = Collections.unmodifiableMap(osIds);

		// Browser enum에 대응하는 검색될 string을 순서대로 등록
		final Map<Browser, String> browserIds = new LinkedHashMap<Browser, String>();
		browserIds.put(Browser.EDGE, "edge"); //ms edge
		browserIds.put(Browser.IE11, "rv:11.0"); 
		browserIds.put(Browser.OPERAIOS, "opios"); //opera ios
		browserIds.put(Browser.OPERA15, "opr"); // OPERA 15 and Beyond
		browserIds.put(Browser.OPERA, "opera"); // MSIE 전에 OPERA 체킹
		browserIds.put(Browser.CHROMEIOS, "crios"); //chrome ios 
		browserIds.put(Browser.WHALE, "whale"); // CHROME 전에 WHALE 체킹
		browserIds.put(Browser.CHROME, "chrome"); // SAFARI 전에 CHROME 체킹
		browserIds.put(Browser.FIREFOXIOS, "fxios"); //firefox ios
		browserIds.put(Browser.FIREFOX, "firefox");
		browserIds.put(Browser.SAFARI, "safari");
		browserIds.put(Browser.IE10, "msie 10");
		browserIds.put(Browser.IE9, "msie 9");
		browserIds.put(Browser.IE8, "msie 8");
		browserIds.put(Browser.IE7, "msie 7");
		browserIds.put(Browser.IE6, "msie 6");
		browserIds.put(Browser.IE5, "msie 5");
		browserIds.put(Browser.IE4, "msie 4");
		browserIds.put(Browser.UNKNOWN, "");
		BROWSER_IDS = Collections.unmodifiableMap(browserIds);
	}

	/**
	 * 생성자
	 * @param request HttpServletRequest
	 */
	public BrowserUtil(HttpServletRequest request) {
		this(request.getHeader("User-Agent"));
	}

	/**
	 * 생성자
	 * @param userAgent User-Agent String
	 */
	public BrowserUtil(String userAgentString) {
		logger.debug("Header User Agent String - {}", userAgentString);
		originalUserAgent = userAgentString != null ? userAgentString : "";

		userAgent = userAgentString != null ? userAgentString.toLowerCase() : null;
		if (userAgent == null) {
			os = OS.UNKNOWN;
			browser = Browser.UNKNOWN;
		}
	}

	/**
	 * 클라이언트 OS 리턴
	 * @return OperatingSystem
	 */

	public String getOs() {
		if (os == null) {
			os = OS.UNKNOWN;
			for (Map.Entry<OS, String> entry : OS_IDS.entrySet()) {
				if (userAgent.indexOf(entry.getValue()) != -1) {
					os = entry.getKey();
					break;
				}
			}
		}
		
		//windows 8.1이면 WINDOWS8로 리턴
		if (os == OS.WINDOWS81){
			os = OS.WINDOWS8;
		}

		return String.valueOf(os);
	}

	/**
	 * 클라이언트 Browser 리턴
	 * @return Browser
	 */
	public String getBrowser() {
		if (browser == null) {
			browser = Browser.UNKNOWN;
			for (Map.Entry<Browser, String> entry : BROWSER_IDS.entrySet()) {
				if (userAgent.indexOf(entry.getValue()) != -1) {
					browser = entry.getKey();
					break;
				}
			}
		}
		
		//opera ios, opera 15 이상이면 opera 리턴
		if (browser == Browser.OPERAIOS || browser == Browser.OPERA15){
			browser = Browser.OPERA;
		}
		
		//firefox ios면 firefox
		if (browser == Browser.FIREFOXIOS){
			browser = Browser.FIREFOX;
		}
		
		//chrome ios면 chrome
		if (browser == Browser.CHROMEIOS){
			browser = Browser.CHROME;
		}
		
		return String.valueOf(browser);
	}

	public String getOriginalUserAgent(){
		return originalUserAgent;
	}
	
	//브라우저 유형별 파일명가져오기
	public String getDisposition(String filename, String browser) throws Exception {
        
	   String encodedFilename = null;
	   if (browser.equals("EDGE") || browser.equals("IE11") || browser.equals("IE10") || browser.equals("IE9") || browser.equals("IE8") || browser.equals("IE7") || browser.equals("IE6") || browser.equals("IE5") || browser.equals("IE4") ){
		   encodedFilename = java.net.URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
	   } else if (browser.equals("FIREFOX")) {
		   encodedFilename = "\"" + new String(filename.getBytes("UTF-8"), "8859_1") + "\"";
	   } else if (browser.equals("OPERA") || browser.equals("OPERA15"))  {
		   encodedFilename = "\"" + new String(filename.getBytes("UTF-8"), "8859_1") + "\"";
	   } else if (browser.equals("SAFARI")) {
		   encodedFilename = "\"" + new String(filename.getBytes("UTF-8"), "8859_1") + "\"";
	   } else if (browser.equals("CHROME")) {
		   StringBuffer sb = new StringBuffer();
		   for (int i = 0; i < filename.length(); i++) {
			   char c = filename.charAt(i);
			   if (c > '~') {
				   sb.append(java.net.URLEncoder.encode("" + c, "UTF-8"));	
				   } else {
				  sb.append(c);
			  }
		  }
		  encodedFilename = sb.toString();
	   } else {
		   //throw new RuntimeException("Not supported browser");
		   encodedFilename = "\"" + new String(filename.getBytes("UTF-8"), "8859_1") + "\"";
	   }
	   return encodedFilename;
	}
}
