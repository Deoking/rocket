package kr.acanet.portal.system.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.pluto.container.PortletContainer;
import org.apache.pluto.container.PortletContainerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import kr.acanet.portal.driver.AttributeKeys;
import kr.acanet.portal.driver.configuration.AdminConfiguration;
import kr.acanet.portal.driver.configuration.DriverConfiguration;
import kr.acanet.portal.driver.configuration.DriverConfigurationException;

/**
 * 스프링 ContextLoaderListner를 확장, ServletContextListener를 구현한 포털컨텍스트 초기화 리스너.
 */
public class PortalContextStartupListener extends ContextLoaderListener implements ServletContextListener {

	private static final Logger logger = LoggerFactory.getLogger(PortalContextStartupListener.class);
	
	//서블릿컨텍스트에 등록 할 컨테이너 키값
	private static final String CONTAINER_KEY = AttributeKeys.PORTLET_CONTAINER;

	//서블릿컨텍스트에 등록 할 드라이버설정 키값
	private static final String DRIVER_CONFIG_KEY = AttributeKeys.DRIVER_CONFIG;

	//서블릿컨텍스트에 등록 할 관리자설정 키값
	private static final String ADMIN_CONFIG_KEY = AttributeKeys.DRIVER_ADMIN_CONFIG;
	
	//서블릿 컨텍스트
	private static ServletContext servletContext;

	public static ServletContext getServletContext() {
		return servletContext;
	}

	// ServletContextListener Impl ---------------------------------------------

	/**
	 * WAS가 기동될때 포털드라이버 설정 및 컨테이너 설정을 아래의 순서대로 시작. 
	 * <ol>
	 * <li>ResourceConfig 파일 검색</li>
	 * <li>ResourceConfig 파일 파싱</li>
	 * <li>포털컨텍스트 생성</li>
	 * <li>컨테이너서비스 구현체 등록</li>
	 * <li>포틀릿 컨테이너 생성</li>
	 * <li>컨테이너 초기화</li>
	 * <li>서블릿컨텍스트에 키값으로 설정 바인딩</li>
	 * <li>서블릿컨텍스트에 키값으로 컨테이너 바인딩</li>
	 * <ol>
	 *
	 * @param event the servlet context event.
	 */
	public void contextInitialized(ServletContextEvent event) {
		logger.info("Starting up Aca Portal Driver...");

		final ServletContext servletContext = event.getServletContext();
		
		PortalContextStartupListener.servletContext = servletContext;
		
		//스프링 ContextLoaderLister의 contextInitialized 실행
		super.contextInitialized(event);
		
		WebApplicationContext springContext = null;

		try {
			//스프링컨텍스트를 가져옴.(Root어플리케이션 빈들을 관리하는 컨텍스트 - PortalContextConfiguration.java 참조)
			springContext = (WebApplicationContext) servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

		} catch (RuntimeException ex) {
			String msg = "Problem getting Spring context: " + ex.getMessage();
			logger.error(msg, ex);
			throw ex;
		}
		
		logger.debug("1. Loading DriverConfiguration. . . ");
		DriverConfiguration driverConfiguration = (DriverConfiguration) springContext.getBean("driverConfiguration");
		
		logger.debug("2. Registering DriverConfiguration. . .");
		servletContext.setAttribute(DRIVER_CONFIG_KEY, driverConfiguration);

		logger.debug("3. Loading Optional AdminConfiguration. . .");
		AdminConfiguration adminConfiguration = (AdminConfiguration) springContext.getBean("adminConfiguration");

		if (adminConfiguration != null) {
			logger.debug("3.1  Registering Optional AdminConfiguration");
			servletContext.setAttribute(ADMIN_CONFIG_KEY, adminConfiguration);
		} else {
			logger.info("Optional AdminConfiguration not found. Ignoring.");
		}

		logger.info("Initializing Portlet Container...");

		// 포틀릿컨테이너 생성 및 서블릿컨텍스트에 바인딩
		logger.debug("1. Creating portlet container...");
		PortletContainer container = (PortletContainer) springContext.getBean("portletContainer");
		servletContext.setAttribute(CONTAINER_KEY, container);
		
		logger.info("\n");
		logger.info("***********************************************");
		logger.info("********** Portlet container Started **********");
		logger.info("********** Aca-Portal Driver Started **********");
		logger.info("***   Portal powered by Apache Pluto {}  ***", driverConfiguration.getPortalVersion());
		logger.info("***********************************************");
		logger.info("\n");
	}

	/**
	 * Recieve notification that the context is being shut down and subsequently
	 * destroy the container.
	 *
	 * @param event the destrubtion event.
	 */
	public void contextDestroyed(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		logger.info("Shutting down Portal...");
		destroyContainer(servletContext);
		destroyAdminConfiguration(servletContext);
		destroyDriverConfiguration(servletContext);
		logger.info("********** Aca Portal Driver Shut Down **********\n\n");
		super.contextDestroyed(event);
	}

	// Private Destruction Methods ---------------------------------------------

	/**
	 * Destroyes the portlet container and removes it from servlet context.
	 *
	 * @param servletContext the servlet context.
	 */
	private void destroyContainer(ServletContext servletContext) {
		logger.info("Shutting down Portlet Container...");
		PortletContainer container = (PortletContainer) servletContext.getAttribute(CONTAINER_KEY);
		if (container != null) {
			try {
				container.destroy();
				logger.info("Portlet Container shut down.");
			} catch (PortletContainerException ex) {
				logger.error("Unable to shut down portlet container: " + ex.getMessage(), ex);
			} finally {
				servletContext.removeAttribute(CONTAINER_KEY);
			}
		}
	}

	/**
	 * Destroyes the portal driver config and removes it from servlet context.
	 *
	 * @param servletContext the servlet context.
	 */
	private void destroyDriverConfiguration(ServletContext servletContext) {
		DriverConfiguration driverConfig = (DriverConfiguration) servletContext.getAttribute(DRIVER_CONFIG_KEY);
		if (driverConfig != null) {
			servletContext.removeAttribute(DRIVER_CONFIG_KEY);
		}
	}

	/**
	 * Destroyes the portal admin config and removes it from servlet context.
	 *
	 * @param servletContext the servlet context.
	 */
	private void destroyAdminConfiguration(ServletContext servletContext) {
		AdminConfiguration adminConfig = (AdminConfiguration) servletContext.getAttribute(ADMIN_CONFIG_KEY);
		if (adminConfig != null) {
			try {
				adminConfig.destroy();
				logger.info("Portal Admin Config destroyed.");
			} catch (DriverConfigurationException ex) {
				logger.error("Unable to destroy portal admin config: " + ex.getMessage(), ex);
			} finally {
				servletContext.removeAttribute(ADMIN_CONFIG_KEY);
			}
		}
	}

}
