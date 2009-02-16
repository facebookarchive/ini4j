/**
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

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.prefs.Preferences;

/**
 * JUnit test of IniPreferencesFactory class.
 */
public class IniPreferencesFactoryTest
{
    static final String FACTORY = "java.util.prefs.PreferencesFactory";

    /**
     * Test of getResourceAsStream method.
     *
     * @throws Exception on error
     */
    @SuppressWarnings("empty-statement")
    @Test public void testGetResourceAsStream() throws Exception
    {
        IniPreferencesFactory factory = new IniPreferencesFactory();

        // class path
        assertNotNull(factory.getResourceAsStream(Helper.DWARFS_INI));

        // url
        String location = getClass().getClassLoader().getResource(Helper.DWARFS_INI).toString();

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

    /**
     * Test of systemRoot method.
     *
     * @throws Exception on error
     */
    @Test public void testSystemRoot() throws Exception
    {
        System.setProperty(FACTORY, IniPreferencesFactory.class.getName());
        System.setProperty(IniPreferencesFactory.KEY_SYSTEM, Helper.DWARFS_INI);
        Preferences prefs = Preferences.systemRoot();

        assertNotNull(prefs);
        assertEquals(prefs.getClass(), IniPreferences.class);
        assertSame(prefs, Preferences.systemRoot());
        Dwarfs dwarfs = Helper.newDwarfs();

        assertNotNull(prefs);
        assertEquals(prefs.getClass(), IniPreferences.class);
        Helper.assertEquals(dwarfs.getHappy(), prefs.node(Dwarfs.PROP_HAPPY));
    }

    /**
     * Test of userRoot method.
     *
     * @throws Exception on error
     */
    @Test public void testUserRoot() throws Exception
    {
        System.setProperty(FACTORY, IniPreferencesFactory.class.getName());
        Preferences prefs = Preferences.userRoot();

        assertNotNull(prefs);
        assertEquals(prefs.getClass(), IniPreferences.class);
        assertSame(prefs, Preferences.userRoot());
    }
}
