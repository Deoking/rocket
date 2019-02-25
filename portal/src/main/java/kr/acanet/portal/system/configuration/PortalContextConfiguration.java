package kr.acanet.portal.system.configuration;

import javax.portlet.PortalContext;

import org.apache.pluto.container.EventCoordinationService;
import org.apache.pluto.container.FilterManagerService;
import org.apache.pluto.container.PortletContainer;
import org.apache.pluto.container.PortletRequestContextService;
import org.apache.pluto.container.PortletURLListenerService;
import org.apache.pluto.container.driver.PortalAdministrationService;
import org.apache.pluto.container.driver.PortalDriverServices;
import org.apache.pluto.container.impl.PortletAppDescriptorServiceImpl;
import org.apache.pluto.container.impl.PortletContainerImpl;
import org.apache.pluto.container.impl.RequestDispatcherServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import kr.acanet.portal.driver.configuration.AdminConfiguration;
import kr.acanet.portal.driver.configuration.DriverConfiguration;
import kr.acanet.portal.driver.configuration.impl.AdminConfigurationImpl;
import kr.acanet.portal.driver.configuration.impl.DriverConfigurationImpl;
import kr.acanet.portal.driver.container.DefaultPortalAdministrationService;
import kr.acanet.portal.driver.container.PortalDriverServicesImpl;
import kr.acanet.portal.driver.container.PortletContextManager;
import kr.acanet.portal.driver.container.impl.EventCoordinationServiceImpl;
import kr.acanet.portal.driver.container.impl.FilterManagerServiceImpl;
import kr.acanet.portal.driver.container.impl.PortalContextImpl;
import kr.acanet.portal.driver.container.impl.PortletRequestContextServiceImpl;
import kr.acanet.portal.driver.container.impl.PortletURLListenerServiceImpl;
import kr.acanet.portal.driver.container.impl.resource.RenderConfigServiceImpl;
import kr.acanet.portal.driver.container.impl.resource.ResourceConfig;
import kr.acanet.portal.driver.container.impl.resource.SupportedModesServiceImpl;
import kr.acanet.portal.driver.container.impl.resource.SupportedWindowStateServiceImpl;
import kr.acanet.portal.driver.service.portal.RenderConfigService;
import kr.acanet.portal.driver.service.portal.SupportedModesService;
import kr.acanet.portal.driver.service.portal.SupportedWindowStateService;
import kr.acanet.portal.driver.service.portal.admin.RenderConfigAdminService;
import kr.acanet.portal.driver.url.PortalURLParser;
import kr.acanet.portal.driver.url.impl.PortalURLParserImpl;

/**
 * 포털 시스템, 플루토 포틀릿 컨테이너, 플루토 포틀릿 컨테이너 드라이버 자바설정
 */
@Configuration
@EnableWebMvc
@ComponentScan({ "kr.acanet.portal.system" })
public class PortalContextConfiguration extends WebMvcConfigurerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(PortalContextConfiguration.class);

	@Bean
	public RequestDispatcherServiceImpl requestDispatcherService() {
		return new RequestDispatcherServiceImpl();
	}
	
	@Bean(initMethod="init")
	@Scope("singleton")
	public PortletContainer portletContainer() {
		return new PortletContainerImpl(getPortalName(), portalDriverServices());
	}
	
	@Bean
	public DriverConfiguration driverConfiguration() { 
		return new DriverConfigurationImpl(portalUrlParser(), 
				resourceConfigFactory(), 
				renderService(), 
				supportedModesService(), 
				supportedWindowStatesService());
	}
	
	@Bean
	public String getPortalName() {
		return driverConfiguration().getPortalName();
	}
	
	//DriverConfiguration
	@Bean
	@Scope("singleton")
	public PortalURLParser portalUrlParser() {
		return PortalURLParserImpl.getParser();
	}
	
	@Bean 
	public ResourceConfig resourceConfigFactory() {
		return StaticServletContextResourceConfiguration.getResourceConfig();
	}
	
	@Bean
	@Scope("singleton")
	public RenderConfigService renderService() {
		return new RenderConfigServiceImpl((ResourceConfig)resourceConfigFactory());
	}
	
	@Bean
	public SupportedModesService supportedModesService() {
		return new SupportedModesServiceImpl(resourceConfigFactory(), portletContextService(), portletContextService());
	}
	
	@Bean
	public PortletContextManager portletContextService() {
		return new PortletContextManager(requestDispatcherService(), new PortletAppDescriptorServiceImpl());
	}
	
	@Bean
	@Scope("singleton")
	public SupportedWindowStateService supportedWindowStatesService() {
		return new SupportedWindowStateServiceImpl(resourceConfigFactory(), portletContextService());
	}
	
//	@Bean
//	@Scope("singleton")
//	public PublicRenderParameterService publicRenderParameterService() {
//		return new PublicRenderParameterServiceImpl(renderService(), portletContextService());
//	}
	
	//portal driver service configuration.
	@Bean
	@Scope("singleton")
	public PortalDriverServices portalDriverServices() {
		return new PortalDriverServicesImpl(portalContext(), 
				portletRequestContextService(), 
				eventCoordinationService(), 
				filterManagerService(), 
				portletURLListenerService(), 
				null, 
				portletContextService(), 
				portletContextService(), 
				portalAdministrationService());
	}
	
	@Bean
	public PortalContext portalContext() {
		return new PortalContextImpl(driverConfiguration());
	}
	
	@Bean
	@Scope("singleton")
	public PortletRequestContextService portletRequestContextService() {
		return new PortletRequestContextServiceImpl();
	}
	
	@Bean
	@Scope("singleton")
	public EventCoordinationService eventCoordinationService() {
		return new EventCoordinationServiceImpl(portletContextService(), portletContextService());
	}
	
	@Bean
	@Scope("singleton")
	public FilterManagerService filterManagerService() {
		return new FilterManagerServiceImpl();
	}
	
	@Bean
	@Scope("singleton")
	public PortletURLListenerService portletURLListenerService() {
		return new PortletURLListenerServiceImpl();
	}
	
	@Bean
	public PortalAdministrationService portalAdministrationService() {
		return new DefaultPortalAdministrationService();
	}
	
	@Bean
	public AdminConfiguration adminConfiguration() {
		AdminConfigurationImpl adminConfiguration = new AdminConfigurationImpl();
		adminConfiguration.setRenderConfigAdminService(renderConfigService());
		return adminConfiguration;
	}
	
	@Bean 
	@Scope("singleton")
	public RenderConfigAdminService renderConfigService() {
		return new RenderConfigServiceImpl(resourceConfigFactory());
	}
	
}	
