package kr.acanet.portal.driver.container.impl.resource;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.digester.Digester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import kr.acanet.portal.driver.service.portal.PageConfig;
import kr.acanet.portal.driver.service.portal.RenderConfig;

/**
 * @version 1.0
 * @since Sep 23, 2004
 */
public class ResourceConfigReader {

	private static final Logger logger = LoggerFactory.getLogger(ResourceConfigReader.class);

    public static final String CONFIG_FILE = "/WEB-INF/configuration/portal-driver-config.xml";


    private static ResourceConfigReader factory;

    public static ResourceConfigReader getFactory() {
        if (factory == null) {
            factory = new ResourceConfigReader();
        }
        return factory;
    }

    private Digester digester;

    private ResourceConfigReader() {
        digester = new Digester();
        digester.setClassLoader(Thread.currentThread().getContextClassLoader());
        init();
    }

    public ResourceConfig parse(InputStream in)
        throws IOException, SAXException {
        return (ResourceConfig) digester.parse(in);
    }

// Digester Setup

    private void init() {
    	logger.debug("Setting up digester...");
        digester.addObjectCreate(
            "pluto-portal-driver",
            ResourceConfig.class
        );
        digester.addBeanPropertySetter(
            "pluto-portal-driver/portal-name",
            "portalName"
        );
        digester.addBeanPropertySetter(
            "pluto-portal-driver/portal-version",
            "portalVersion"
        );
        digester.addBeanPropertySetter(
            "pluto-portal-driver/container-name",
            "containerName"
        );

        digester.addCallMethod(
            "pluto-portal-driver/supports/portlet-mode",
            "addSupportedPortletMode", 0
        );
        digester.addCallMethod(
            "pluto-portal-driver/supports/window-state",
            "addSupportedWindowState", 0
        );

        digester.addObjectCreate(
            "pluto-portal-driver/render-config",
            RenderConfig.class
        );
        digester.addSetProperties(
            "pluto-portal-driver/render-config",
            "default", "defaultPageId"
        );
        digester.addObjectCreate(
            "pluto-portal-driver/render-config/page",
            PageConfig.class
        );
        digester.addSetProperties("pluto-portal-driver/render-config/page");
        digester.addCallMethod(
            "pluto-portal-driver/render-config/page/portlet", "addPortlet", 2
        );
        digester.addCallParam(
            "pluto-portal-driver/render-config/page/portlet",
            0, "context"
        );
        digester.addCallParam(
            "pluto-portal-driver/render-config/page/portlet",
            1, "name"
        );
        digester.addSetNext(
            "pluto-portal-driver/render-config/page",
            "addPage"
        );
        digester.addSetNext(
            "pluto-portal-driver/render-config",
            "setRenderConfig"
        );
    }

}

