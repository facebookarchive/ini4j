/*
 * Copyright 2005,2009 Ivan SZKIBA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ini4j;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class IniPreferencesFactoryListener extends IniPreferencesFactory implements ServletContextListener
{
    public static final String DEFAULT_USER_LOCATION = "/WEB-INF/user.ini";
    public static final String DEFAULT_SYSTEM_LOCATION = "/WEB-INF/system.ini";
    private ServletContext _context;

    @Override public void contextDestroyed(ServletContextEvent event)
    {
        _context = null;
    }

    @Override public void contextInitialized(ServletContextEvent event)
    {
        _context = event.getServletContext();
        System.setProperty("java.util.prefs.PreferencesFactory", getClass().getName());
    }

    @Override protected String getIniLocation(String key)
    {
        String location = _context.getInitParameter(key);

        if (location == null)
        {
            location = key.equals(KEY_USER) ? DEFAULT_USER_LOCATION : DEFAULT_SYSTEM_LOCATION;
        }

        return location;
    }

    @Override protected URL getResource(String location) throws IllegalArgumentException
    {
        try
        {
            URL url = _context.getResource(location);

            if (url == null)
            {
                url = super.getResource(location);
            }

            return url;
        }
        catch (MalformedURLException x)
        {
            throw (IllegalArgumentException) new IllegalArgumentException().initCause(x);
        }
    }
}
