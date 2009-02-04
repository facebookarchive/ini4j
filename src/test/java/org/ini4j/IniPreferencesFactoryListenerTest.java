/*
 * Copyright 2005 [ini4j] Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ini4j;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHttpContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

///CLOVER:OFF
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * JUnit test of IniPreferencesFactoryListener class.
 */
public class IniPreferencesFactoryListenerTest extends AbstractTestBase
{
    static final String DUMMY = "dummy";
    
    /**
     * Instantiate test.
     *
     * @param testName name of the test
     */
    public IniPreferencesFactoryListenerTest(String testName)
    {
        super(testName);
    }

    /**
     * Create test suite.
     *
     * @return new test suite
     */
    public static Test suite()
    {
        return new TestSuite(IniPreferencesFactoryListenerTest.class);
    }

    /**
     * Test of getIniLocation method.
     *
     * @throws Exception on error
     */
    public void testGetIniLocation() throws Exception
    {
        Server server = new Server();
        ServletHttpContext htc = (ServletHttpContext) server.getContext("/");
        ServletContext context = htc.getServletContext();
        
        ServletContextEvent event = new ServletContextEvent(context);
        IniPreferencesFactoryListener listener = new IniPreferencesFactoryListener();
        
        listener.contextInitialized(event);
        
        String value = listener.getIniLocation(IniPreferencesFactory.KEY_USER);
        assertEquals(IniPreferencesFactoryListener.DEFAULT_USER_LOCATION, value);
        
        value = listener.getIniLocation(IniPreferencesFactory.KEY_SYSTEM);
        assertEquals(IniPreferencesFactoryListener.DEFAULT_SYSTEM_LOCATION, value);

        htc.setInitParameter(IniPreferencesFactory.KEY_USER, DUMMY);
        value = listener.getIniLocation(IniPreferencesFactory.KEY_USER);
        assertEquals(DUMMY, value);
        
        listener.contextDestroyed(event);
    }

    /**
     * Test of getResourceAsStream method.
     *
     * @throws Exception on error
     */
    public void testGetResourceAsStream() throws Exception
    {
        Server server = new Server();
        ServletHttpContext htc = (ServletHttpContext) server.getContext("/");
        ServletContext context = htc.getServletContext();
        
        ServletContextEvent event = new ServletContextEvent(context);
        IniPreferencesFactoryListener listener = new IniPreferencesFactoryListener();
        
        listener.contextInitialized(event);
        
        assertNotNull(listener.getResourceAsStream(DWARFS_INI));
	
        listener.contextDestroyed(event);
    }
}
