/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kr.acanet.portal.driver;

/**
 * Constants used as attribute keys to bind values to servlet context or servlet
 * request.
 *
 * @version 1.0
 * @since Sep 25, 2004
 */
public class AttributeKeys {

    //서블릿컨텍스트(ServletContext)에 등록 - 드라이버 설정 
    public static final String DRIVER_CONFIG = "driverConfig";

    //서블릿컨텍스트(ServletContext)에 등록 - 관리자 설정
    public static final String DRIVER_ADMIN_CONFIG = "driverAdminConfig";

    //서블릿컨텍스트(ServletContext)에 등록 - 포틀릿 컨테이너
    public static final String PORTLET_CONTAINER = "portletContainer";
 
    //리퀘스트(HttpServletRequest)에 등록 - 현재 페이지 
    public static final String CURRENT_PAGE = "currentPage";

    //리퀘스트(HttpServletRequest)에 등록 - 포틀릿 타이틀
    public static final String PORTLET_TITLE = "org.apache.pluto.driver.DynamicPortletTitle";

    public static final String PORTAL_URL_PARSER = "PORTAL_URL_PARSER";

    // Constructor -------------------------------------------------------------
    private AttributeKeys() {

    }
}

