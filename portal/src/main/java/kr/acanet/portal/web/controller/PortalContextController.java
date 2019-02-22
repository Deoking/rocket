package kr.acanet.portal.web.controller;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pluto.container.PortletContainer;
import org.apache.pluto.container.PortletContainerException;
import org.apache.pluto.driver.AttributeKeys;
import org.apache.pluto.driver.config.AdminConfiguration;
import org.apache.pluto.driver.core.PortalRequestContext;
import org.apache.pluto.driver.core.PortletWindowImpl;
import org.apache.pluto.driver.services.portal.PageConfig;
import org.apache.pluto.driver.services.portal.PortletWindowConfig;
import org.apache.pluto.driver.services.portal.admin.DriverAdministrationException;
import org.apache.pluto.driver.services.portal.admin.PortletRegistryAdminService;
import org.apache.pluto.driver.url.PortalURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;

@Controller
@RequestMapping("/portal")
public class PortalContextController implements ServletContextAware, ServletConfigAware {

	private static final Logger logger = LoggerFactory.getLogger(PortalContextController.class);

	private ServletContext servletContext;

	private ServletConfig servletConfig;

	protected PortletContainer container;

	public static final String DEFAULT_PAGE_URI = "/WEB-INF/themes/pluto-default-theme.jsp";

	private String contentType = "";

	@Override
	public void setServletContext(ServletContext servletContext) {
		logger.debug("ServletContextAware setting...");
		this.servletContext = servletContext;
		initContainer(servletContext);
	}

	@Override
	public void setServletConfig(ServletConfig servletConfig) {
		logger.debug("ServletConfigAware setting...");
		this.servletConfig = servletConfig;
		initCharset(servletConfig);
	}

	private void initCharset(ServletConfig servletConfig2) {
		String charset = this.servletConfig.getInitParameter("charset");
		if (charset != null && charset.length() > 0) {
			contentType = "text/html; charset=" + charset;
		}
	}

	private void initContainer(ServletContext servletContext) {
		logger.debug("Portlet container setting...");
		this.container = (PortletContainer) servletContext.getAttribute(AttributeKeys.PORTLET_CONTAINER);
	}

	@RequestMapping("/publish")
	public void publish(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String context = request.getParameter("context");
		try {
			doPublish(context);
		} catch (Throwable t) {
			StringBuffer sb = new StringBuffer();
			sb.append("Unable to publish portlet application bound to context '" + context + "'.");
			sb.append("Reason: ").append(t.getMessage());
			logger.debug(sb.toString());
			response.getWriter().println(sb.toString());
		}
	}

	private void doPublish(String context) throws DriverAdministrationException {
		AdminConfiguration adminConfig = (AdminConfiguration) servletContext
				.getAttribute(AttributeKeys.DRIVER_ADMIN_CONFIG);
		PortletRegistryAdminService admin = adminConfig.getPortletRegistryAdminService();
		admin.addPortletApplication(context);
	}

	@RequestMapping("/*")
	public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.debug("Common portal URL process - {}", request.getRequestURI());

		if (contentType != "") {
			logger.debug("contentType - {}", contentType);
			response.setContentType(contentType);
		}

		PortalRequestContext portalRequestContext = new PortalRequestContext(servletContext, request, response);

		PortalURL portalURL = null;

		try {
			portalURL = portalRequestContext.getRequestedPortalURL();
		} catch (Exception ex) {
			String msg = "Cannot handle request for portal URL. Problem - " + ex.getMessage();
			logger.error(msg, ex);
			throw new ServletException(msg, ex);
		}

		String actionWindowId = portalURL.getActionWindow();
		String resourceWindowId = portalURL.getResourceWindow();

		PortletWindowConfig actionWindowConfig = null;
		PortletWindowConfig resourceWindowConfig = null;

		if (resourceWindowId != null) {
			resourceWindowConfig = PortletWindowConfig.fromId(resourceWindowId);
		} else if (actionWindowId != null) {
			actionWindowConfig = PortletWindowConfig.fromId(actionWindowId);
		}

		// Action window config will only exist if there is an action request.
		if (actionWindowConfig != null) {
			PortletWindowImpl portletWindow = new PortletWindowImpl(container, actionWindowConfig, portalURL);
			logger.debug("Processing action request for window: {}", portletWindow.getId().getStringId());
			try {
				container.doAction(portletWindow, request, response);
			} catch (PortletContainerException ex) {
				logger.error(ex.getMessage(), ex);
				throw new ServletException(ex);
			} catch (PortletException ex) {
				logger.error(ex.getMessage(), ex);
				throw new ServletException(ex);
			}
			logger.debug("Action request processed.\n\n");
		}
		// Resource request
		else if (resourceWindowConfig != null) {
			PortletWindowImpl portletWindow = new PortletWindowImpl(container, resourceWindowConfig, portalURL);
			logger.debug("Processing resource Serving request for window: {}" , portletWindow.getId().getStringId());
			try {
				container.doServeResource(portletWindow, request, response);
			} catch (PortletContainerException ex) {
				logger.error(ex.getMessage(), ex);
				throw new ServletException(ex);
			} catch (PortletException ex) {
				logger.error(ex.getMessage(), ex);
				throw new ServletException(ex);
			}
			logger.debug("Resource serving request processed.\n\n");
		}
		// Otherwise (actionWindowConfig == null), handle the render request.
		else {
			if (logger.isDebugEnabled()) {
				logger.debug("Processing render request.");
			}
			PageConfig pageConfig = portalURL.getPageConfig(servletContext);
			if (pageConfig == null) {
				String renderPath = (portalURL == null ? "" : portalURL.getRenderPath());
				String msg = "PageConfig for render path [" + renderPath + "] could not be found.";
				logger.error(msg);
				throw new ServletException(msg);
			}

			request.setAttribute(AttributeKeys.CURRENT_PAGE, pageConfig);
			String uri = (pageConfig.getUri() != null) ? pageConfig.getUri() : DEFAULT_PAGE_URI;
			if (logger.isDebugEnabled()) {
				logger.debug("Dispatching to: " + uri);
			}
			RequestDispatcher dispatcher = request.getRequestDispatcher(uri);
			dispatcher.forward(request, response);
			if (logger.isDebugEnabled()) {
				logger.debug("Render request processed.\n\n");
			}
		}
	}

}
