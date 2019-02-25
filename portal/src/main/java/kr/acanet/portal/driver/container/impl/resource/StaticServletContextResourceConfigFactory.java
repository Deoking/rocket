/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package kr.acanet.portal.driver.container.impl.resource;

import java.io.InputStream;

import javax.servlet.ServletContext;

import kr.acanet.portal.driver.PortalStartupListener;
import kr.acanet.portal.driver.configuration.DriverConfigurationException;

/**
 * gross DI component to work around Spring limitations
 * @version $Rev: 893689 $ $Date: 2009-12-23 20:58:17 -0600 (Wed, 23 Dec 2009) $
 */
public class StaticServletContextResourceConfigFactory
{

    private static ResourceConfig resourceConfig;

    private static void init(ServletContext servletContext)
    {
        try
        {
            InputStream in = servletContext.getResourceAsStream(ResourceConfigReader.CONFIG_FILE);
            resourceConfig = ResourceConfigReader.getFactory().parse(in);
        }
        catch (Exception e)
        {
            throw new DriverConfigurationException(e);
        }
    }

    public static synchronized ResourceConfig getResourceConfig()
    {
        if (resourceConfig == null)
        {
            init(PortalStartupListener.getServletContext());
        }
        return resourceConfig;
    }
}
