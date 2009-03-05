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

public class IniPreferencesTest
{
    private static final String DUMMY = "dummy";

    /**
     * Test of constructors.
     *
     * @throws Exception on error
     */
    @Test public void testConstructor() throws Exception
    {
        Ini ini = Helper.newDwarfsIni();
        IniPreferences prefs = new IniPreferences(ini);

        assertSame(ini, prefs.getIni());
        Helper.doTestDwarfs(ini.as(Dwarfs.class));
        prefs = new IniPreferences(Helper.getResourceStream(Helper.DWARFS_INI));
        Helper.assertEquals(ini.get(Dwarfs.PROP_DOC).as(Dwarf.class), prefs.node(Dwarfs.PROP_DOC));
        prefs = new IniPreferences(Helper.getResourceReader(Helper.DWARFS_INI));
        Helper.assertEquals(ini.get(Dwarfs.PROP_HAPPY).as(Dwarf.class), prefs.node(Dwarfs.PROP_HAPPY));
        prefs = new IniPreferences(Helper.getResourceURL(Helper.DWARFS_INI));
        Helper.assertEquals(ini.get(Dwarfs.PROP_HAPPY).as(Dwarf.class), prefs.node(Dwarfs.PROP_HAPPY));
    }

    /**
     * Test of variuos methods.
     *
     * @throws Exception on error
     */
    @Test public void testMisc() throws Exception
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
        assertNotNull(prefs.node(Dwarfs.PROP_DOC));
        assertEquals(1, prefs.childrenNamesSpi().length);
        ini.add(Dwarfs.PROP_HAPPY);
        assertNotNull(prefs.node(Dwarfs.PROP_HAPPY));
        assertEquals(2, prefs.childrenNamesSpi().length);

        // SectionPreferences
        IniPreferences.SectionPreferences sec = (IniPreferences.SectionPreferences) prefs.node(Dwarfs.PROP_DOC);

        assertEquals(0, sec.childrenNamesSpi().length);

        // do nothing, but doesn't throw exception
        sec.sync();
        sec.syncSpi();
        sec.flush();
        sec.flushSpi();

        // empty
        assertEquals(0, sec.keysSpi().length);

        // add one key
        sec.put(Dwarf.PROP_AGE, "87");
        sec.flush();
        assertEquals("87", sec.getSpi(Dwarf.PROP_AGE));

        // has one key
        assertEquals(1, sec.keysSpi().length);

        // remove key
        sec.remove(Dwarf.PROP_AGE);
        sec.flush();

        // has 0 key
        assertEquals(0, sec.keysSpi().length);
        sec.removeNode();
        prefs.flush();
        assertNull(ini.get(Dwarfs.PROP_DOC));
    }

    /**
     * Test of unsupported methods.
     *
     * @throws Exception on error
     */
    @SuppressWarnings("empty-statement")
    @Test public void testUnsupported() throws Exception
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
            prefs.putSpi(DUMMY, DUMMY);
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
        IniPreferences.SectionPreferences sec = (IniPreferences.SectionPreferences) prefs.node(Dwarfs.PROP_DOC);

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
