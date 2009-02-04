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

import java.util.prefs.Preferences;
import junit.framework.Test;
import junit.framework.TestSuite;

///CLOVER:OFF

/**
 * JUnit test of IniPreferencesFactory class.
 */
public class IniPreferencesFactoryTest extends AbstractTestBase
{
    static final String FACTORY = "java.util.prefs.PreferencesFactory";
    
    /**
     * Instantiate test.
     *
     * @param testName name of the test
     */
    public IniPreferencesFactoryTest(String testName)
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
        return new TestSuite(IniPreferencesFactoryTest.class);
    }

    /**
     * Test of userRoot method.
     *
     * @throws Exception on error
     */
    public void testUserRoot() throws Exception
    {
        System.setProperty(FACTORY, IniPreferencesFactory.class.getName());
        Preferences prefs = Preferences.userRoot();
        
        assertNotNull(prefs);
        assertEquals(prefs.getClass(), IniPreferences.class);
        assertSame(prefs, Preferences.userRoot());
    }
    
    /**
     * Test of systemRoot method.
     *
     * @throws Exception on error
     */
    public void testSystemRoot() throws Exception
    {
        System.setProperty(FACTORY, IniPreferencesFactory.class.getName());
        System.setProperty(IniPreferencesFactory.KEY_SYSTEM, DWARFS_INI);
        Preferences prefs = Preferences.systemRoot();
        
        assertNotNull(prefs);
        assertEquals(prefs.getClass(), IniPreferences.class);
        assertSame(prefs, Preferences.systemRoot());

        Dwarfs dwarfs = newDwarfs();
        assertNotNull(prefs);
        assertEquals(prefs.getClass(), IniPreferences.class);
        assertEquals(dwarfs.getHappy(), prefs.node("happy"));
    }
    
    /**
     * Test of getResourceAsStream method.
     *
     * @throws Exception on error
     */
    @SuppressWarnings("empty-statement")
    public void testGetResourceAsStream() throws Exception
    {
        IniPreferencesFactory factory = new IniPreferencesFactory();
        
        // class path
        assertNotNull(factory.getResourceAsStream(DWARFS_INI));

        // url
        String location = getClass().getClassLoader().getResource(DWARFS_INI).toString();
        assertNotNull(factory.getResourceAsStream(location));
        
        // invalid url should throw IllegalArgumentException
        try
        {
            factory.getResourceAsStream("http://");
            fail();
        }
        catch (IllegalArgumentException x)
        {
            ;
        }
    }
}
