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
package org.ini4j.addon;

import org.ini4j.Helper;
import org.ini4j.IniPreferences;

import org.ini4j.sample.Dwarf;
import org.ini4j.sample.Dwarfs;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.NoSuchElementException;
import java.util.prefs.Preferences;

/**
 * JUnit test of PreferencesWrapper class.
 */
public class StrictPreferencesTest
{
    public static final String DOC = Dwarfs.PROP_DOC;
    public static final String OPTION = Dwarf.PROP_HEIGHT;
    public static final String MISSING = "no such option";

    /**
     * Test of all get methods.
     *
     * @throws Exception on error
     */
    @SuppressWarnings("empty-statement")
    @Test public void testGets() throws Exception
    {
        String value;
        Preferences root = new IniPreferences(Helper.newDwarfsIni());
        Preferences peer = root.node(DOC);
        StrictPreferences pref = new StrictPreferences(peer);

        assertNotNull(pref.get(OPTION));
        try
        {
            pref.get(MISSING);
            fail();
        }
        catch (NoSuchElementException x)
        {
            ;
        }

        try
        {
            pref.getInt(MISSING);
            fail();
        }
        catch (NoSuchElementException x)
        {
            ;
        }

        try
        {
            pref.getLong(MISSING);
            fail();
        }
        catch (NoSuchElementException x)
        {
            ;
        }

        try
        {
            pref.getFloat(MISSING);
            fail();
        }
        catch (NoSuchElementException x)
        {
            ;
        }

        try
        {
            pref.getDouble(MISSING);
            fail();
        }
        catch (NoSuchElementException x)
        {
            ;
        }

        try
        {
            pref.getBoolean(MISSING);
            fail();
        }
        catch (NoSuchElementException x)
        {
            ;
        }

        try
        {
            pref.getByteArray(MISSING);
            fail();
        }
        catch (NoSuchElementException x)
        {
            ;
        }
    }
}
