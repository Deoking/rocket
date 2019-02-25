package kr.acanet.portal.system.configuration;

import java.io.InputStream;

import javax.servlet.ServletContext;

import kr.acanet.portal.driver.configuration.DriverConfigurationException;
import kr.acanet.portal.driver.container.impl.resource.ResourceConfig;
import kr.acanet.portal.driver.container.impl.resource.ResourceConfigReader;
import kr.acanet.portal.system.listener.PortalContextStartupListener;

/**
 * 포털 환경설정 파일 세팅 properties 파일로 대체하려 통일하려 하였으나 소스의 분석부족으로 실....패...
 */
public class StaticServletContextResourceConfiguration {
	private static ResourceConfig resourceConfig;

	private static void init(ServletContext servletContext) {
		try {
			InputStream in = servletContext.getResourceAsStream(ResourceConfigReader.CONFIG_FILE);
			resourceConfig = ResourceConfigReader.getFactory().parse(in);
		} catch (Exception e) {
			throw new DriverConfigurationException(e);
		}
	}

	public static synchronized ResourceConfig getResourceConfig() {
		if (resourceConfig == null) {
			init(PortalContextStartupListener.getServletContext());
		}
		return resourceConfig;
	}
}
