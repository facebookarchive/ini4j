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

import java.io.InputStreamReader;
import junit.framework.Test;
import junit.framework.TestSuite;


///CLOVER:OFF

/**
 * JUnit test of IniPreferences class.
 */
public class IniPreferencesTest extends AbstractTestBase
{
    private static final String DUMMY = "dummy";
    
    /**
     * Instantiate test.
     *
     * @param testName name of the test
     */
    public IniPreferencesTest(String testName)
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
        return new TestSuite(IniPreferencesTest.class);
    }

    /**
     * Test of variuos methods.
     *
     * @throws Exception on error
     */
    public void testMisc() throws Exception
    {
        Ini ini = new Ini();
        IniPreferences prefs = new IniPreferences(ini);
        
        // do nothing, but doesn't throw exception
        prefs.sync();
        prefs.flush();
        
        // node & key count
        assertEquals(0, prefs.keysSpi().length);
        assertEquals(0, prefs.childrenNamesSpi().length);
        
        // childNode for new and for existing section
        assertNotNull(prefs.node("doc"));
        assertEquals(1, prefs.childrenNamesSpi().length);
        ini.add("happy");
        assertNotNull(prefs.node("happy"));
        assertEquals(2, prefs.childrenNamesSpi().length);
        
        // SectionPreferences
        IniPreferences.SectionPreferences sec = (IniPreferences.SectionPreferences)prefs.node("doc");
        assertEquals(0, sec.childrenNamesSpi().length);

        // do nothing, but doesn't throw exception
        sec.sync();
        sec.syncSpi();
        sec.flush();
        sec.flushSpi();
        
        // empty
        assertEquals(0, sec.keysSpi().length);
        
        // add one key
        sec.put("age","87");
        sec.flush();
        assertEquals("87", sec.getSpi("age"));
        
        // has one key
        assertEquals(1, sec.keysSpi().length);
        
        // remove key
        sec.remove("age");
        sec.flush();
        
        // has 0 key
        assertEquals(0, sec.keysSpi().length);
        
        sec.removeNode();
        prefs.flush();
        
        assertNull(ini.get("doc"));
    }

    /**
     * Test of constructors.
     *
     * @throws Exception on error
     */
    public void testConstructor() throws Exception
    {
        Ini ini = loadDwarfs();
        IniPreferences prefs = new IniPreferences(ini);
        
        assertSame(ini, prefs.getIni());
        doTestDwarfs(ini.to(Dwarfs.class));
        
        prefs = new IniPreferences(getClass().getClassLoader().getResourceAsStream(DWARFS_INI));

        assertEquals(ini.get("doc").to(Dwarf.class), prefs.node("doc"));
        
        prefs = new IniPreferences(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(DWARFS_INI)));
        
        assertEquals(ini.get("happy").to(Dwarf.class), prefs.node("happy"));
	
        prefs = new IniPreferences(getClass().getClassLoader().getResource(DWARFS_INI));
        
        assertEquals(ini.get("happy").to(Dwarf.class), prefs.node("happy"));
    }
    
    /**
     * Test of unsupported methods.
     *
     * @throws Exception on error
     */
    @SuppressWarnings("empty-statement")
    public void testUnsupported() throws Exception
    {
        Ini ini = new Ini();
        IniPreferences prefs = new IniPreferences(ini);
        
        try
        {
            prefs.getSpi(DUMMY);
            fail();
        }
        catch (UnsupportedOperationException x)
        {
            ;
        }

        try
        {
            prefs.putSpi(DUMMY,DUMMY);
            fail();
        }
        catch (UnsupportedOperationException x)
        {
            ;
        }
        
        try
        {
            prefs.removeNodeSpi();
            fail();
        }
        catch (UnsupportedOperationException x)
        {
            ;
        }
    
        try
        {
            prefs.removeSpi(DUMMY);
            fail();
        }
        catch (UnsupportedOperationException x)
        {
            ;
        }
        
        // SectionPreferences

        IniPreferences.SectionPreferences sec = (IniPreferences.SectionPreferences)prefs.node("doc");
        
        try
        {
            sec.childSpi(DUMMY);
            fail();
        }
        catch (UnsupportedOperationException x)
        {
            ;
        }
        
    }
    
}
