package kr.acanet.portal.web.controller;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pluto.container.HeaderData;
import org.apache.pluto.container.PageResourceId;
import org.apache.pluto.container.PortletContainer;
import org.apache.pluto.container.PortletContainerException;
import org.apache.pluto.container.om.portlet.ContainerRuntimeOption;
import org.apache.pluto.container.om.portlet.Dependency;
import org.apache.pluto.container.om.portlet.PortletDefinition;
import org.apache.pluto.driver.AttributeKeys;
import org.apache.pluto.driver.PartialActionResponse;
import org.apache.pluto.driver.config.AdminConfiguration;
import org.apache.pluto.driver.config.DriverConfiguration;
import org.apache.pluto.driver.core.PortalRequestContext;
import org.apache.pluto.driver.core.PortletWindowImpl;
import org.apache.pluto.driver.services.portal.PageConfig;
import org.apache.pluto.driver.services.portal.PageResources;
import org.apache.pluto.driver.services.portal.PortletWindowConfig;
import org.apache.pluto.driver.services.portal.admin.DriverAdministrationException;
import org.apache.pluto.driver.services.portal.admin.PortletRegistryAdminService;
import org.apache.pluto.driver.url.PortalURL;
import org.apache.pluto.driver.url.PortalURL.URLType;
import org.apache.pluto.driver.util.PageState;
import org.apache.pluto.driver.util.RenderData;
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
		AdminConfiguration adminConfig = (AdminConfiguration) servletContext.getAttribute(AttributeKeys.DRIVER_ADMIN_CONFIG);
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

		String targetWindow = portalURL.getTargetWindow();

		if (targetWindow != null && portalURL.getType() != URLType.Render && portalURL.getType() != URLType.Portal) {

			String reqType = portalURL.getType().toString();
			PortletWindowConfig windowConfig = PortletWindowConfig.fromId(targetWindow);
			PortletWindowImpl portletWindow = new PortletWindowImpl(container, windowConfig, portalURL);

			logger.debug("Processing - {} request for window - {}", reqType, portletWindow.getId().getStringId());

			try {
				PageState ps;
				String jsondata;
				
				URLType uType = portalURL.getType();
				logger.debug("Request URL type - {}", uType);
				switch (uType) {
				case Action:
					container.doAction(portletWindow, request, response, true);
					break;
				case AjaxAction:
					container.doAction(portletWindow, request, response, false);
					response.setContentType("application/json");
					ps = new PageState(request);
					Writer writer = response.getWriter();
					jsondata = ps.toJSONString();
					logger.debug("Ajax Action: returning new page state to client: " + jsondata);
					writer.write(jsondata);
					break;
				case PartialAction:
					container.doAction(portletWindow, request, response, false);
					Map<String, RenderData> renderDataMap = new HashMap<String, RenderData>();
					PartialActionResponse partialActionResponse = new PartialActionResponse(response);
					container.doServeResource(portletWindow, request, partialActionResponse);
					String pid = portletWindow.getId().getStringId();
					renderDataMap.put(pid, partialActionResponse.getRenderData());
					ps = new PageState(request, renderDataMap);
					jsondata = ps.toJSONString();
					logger.debug("Ajax Action: returning new page state to client: " + jsondata);
					response.setContentType("application/json");
					Writer responseWriter = response.getWriter();
					responseWriter.write(jsondata);

					break;
				case Resource:
					container.doServeResource(portletWindow, request, response);
					break;
				default:
					logger.warn("Unknown request - {} ", reqType);
				}
			} catch (PortletContainerException ex) {
				logger.error(ex.getMessage(), ex);
				throw new ServletException(ex);
			} catch (PortletException ex) {
				logger.error(ex.getMessage(), ex);
				throw new ServletException(ex);
			}

			logger.debug("{} request processed - ", reqType);

		} else {
			// Otherwise, handle the render request.
			logger.debug("Processing render request.");

			PageConfig pageConfig = portalURL.getPageConfig(servletContext);

			if (pageConfig == null) {
				String renderPath = (portalURL == null ? "" : portalURL.getRenderPath());
				String msg = "PageConfig for render path [" + renderPath + "] could not be found.";
				logger.error(msg);
				throw new ServletException(msg);
			}

			request.setAttribute(AttributeKeys.CURRENT_PAGE, pageConfig);
			String uri = (pageConfig.getUri() != null) ? pageConfig.getUri() : DEFAULT_PAGE_URI;

			// Execute header request for each portlet on the page

			logger.debug("Executing header requests for target portlets.");

			response.setContentType("text/html;charset=UTF-8");
			doHeaders(request, response, portalURL);

			logger.debug("Dispatching to - {} ", uri);

			// Dispatch to the JSP that aggregates the page.
			RequestDispatcher dispatcher = request.getRequestDispatcher(uri);
			dispatcher.forward(request, response);

			logger.debug("Render request processed.");
		}
	}

	private void doHeaders(HttpServletRequest request, HttpServletResponse respnose, PortalURL pUrl)
			throws ServletException, IOException {

		ServletContext sc = request.getServletContext();
		DriverConfiguration dc = (DriverConfiguration) sc.getAttribute(AttributeKeys.DRIVER_CONFIG);
		StringBuilder markup = new StringBuilder(128);
		List<PageResourceId> portletdeps = new ArrayList<PageResourceId>();
		List<PageResourceId> dynamicdeps = new ArrayList<PageResourceId>();
		Map<PageResourceId, String> dynamicResources = new HashMap<PageResourceId, String>();

		for (String pid : pUrl.getPortletIds()) {

			HeaderData hd = null;
			PortletWindowConfig wcfg = null;
			PortletWindowImpl pwin = null;
			try {
				wcfg = PortletWindowConfig.fromId(pid);
				pwin = new PortletWindowImpl(container, wcfg, pUrl);
			} catch (Throwable e) {
				logger.warn("Could not retrieve configuration for portlet ID: " + pid);
				continue;
			}

			try {

				String appName = wcfg.getContextPath();
				String portletName = PortletWindowConfig.parsePortletName(pid);
				PortletDefinition pd = dc.getPortletRegistryService().getPortletApplication(appName)
						.getPortlet(portletName);

				if (pUrl.isVersion3(pid)) {
					hd = container.doHeader(pwin, request, respnose);

					// collect the page dependencies
					for (Dependency dep : pd.getDependencies()) {
						portletdeps.add(new PageResourceId(dep.getName(), dep.getScope(), dep.getVersion()));
					}

					// Process any dependencies that were dynamically added during the header
					// request
					Map<PageResourceId, String> resources = hd.getDynamicResources();
					for (PageResourceId id : resources.keySet()) {
						dynamicdeps.add(id);
						if (resources.get(id) != null) {
							dynamicResources.put(id, resources.get(id));
						}
					}

				} else if (pUrl.getVersion(pid).equalsIgnoreCase("2.0")) {
					ContainerRuntimeOption crt = pd.getContainerRuntimeOption("javax.portlet.renderHeaders");
					if (crt != null) {
						List<String> headers = crt.getValues();
						if (headers.size() == 1 && headers.get(0).equalsIgnoreCase("true")) {
							hd = container.doRender(pwin, request, respnose, PortletRequest.RENDER_HEADERS);
						}
					}
				}

				if (hd != null) {

					// handle markup for document head section
					markup.append(hd.getHeadSectionMarkup()).append("\n");

					// add the cookies & http headers to the response

					List<Cookie> cookies = hd.getCookies();
					for (Cookie c : cookies) {
						respnose.addCookie(c);
					}

					// Add the HTTP headers to the response
					Map<String, List<String>> headers = hd.getHttpHeaders();
					for (String name : headers.keySet()) {
						for (String val : headers.get(name)) {
							respnose.addHeader(name, val);
						}
					}
				}

			} catch (PortletContainerException ex) {
				logger.error(ex.getMessage(), ex);
				throw new ServletException(ex);
			} catch (PortletException ex) {
				logger.error(ex.getMessage(), ex);
				throw new ServletException(ex);
			}
		}

		// Set the header section markup provided by the portlets as an attribute
		// The main rendering JSP uses this when rendering the head section.
		request.setAttribute(AttributeKeys.HEAD_SECTION_MARKUP, markup.toString());

		// Now generate the markup for the configured page resources
		markup.setLength(0);
		PageResources pageres = dc.getRenderConfigService().getPageResources();
		if (!dynamicResources.isEmpty()) {

			// the dynamically added resources are only valid for this rendering, so
			// avoid modifying the original configured resources.

			pageres = new PageResources(pageres);
			for (PageResourceId id : dynamicResources.keySet()) {
				pageres.addResource(id, PageResources.Type.MARKUP, dynamicResources.get(id));
			}
		}

		// start with the default page resources
		List<PageResourceId> deps = new ArrayList<PageResourceId>(
				dc.getRenderConfigService().getDefaultPageDependencies());
		int defDespSize = deps.size();

		// add in the page-specific resources
		List<PageResourceId> pagedeps = pUrl.getPageConfig(request.getServletContext()).getPageResources();
		deps.addAll(pagedeps);

		// and the portlet dependencies
		deps.addAll(portletdeps);

		// and finally the dynamic portlet dependencies
		deps.addAll(dynamicdeps);
		
		logger.debug("Page dependency list.  total deps - {}, default deps - {}, page deps - {} , portlet deps - {}, dynamic deps - {}", deps.size(), defDespSize, pagedeps.size(), portletdeps.size(), dynamicdeps.size());
		
		// Set the markup resulting from the specified page resources as an attribute
		// The main rendering JSP uses this when rendering the head section.
		markup.append(pageres.getMarkup(deps, request.getContextPath()));
		request.setAttribute(AttributeKeys.DYNAMIC_PAGE_RESOURCES, markup.toString());

		return;
	}
}
