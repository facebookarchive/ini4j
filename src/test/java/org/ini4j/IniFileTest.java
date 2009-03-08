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

import org.ini4j.sample.Dwarf;
import org.ini4j.sample.Dwarfs;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.File;

import java.util.prefs.BackingStoreException;

/**
 * JUnit test of IniFile class.
 */
public class IniFileTest
{

    /**
     * Test of various error conditions.
     *
     * @throws Exception on error
     */
    @SuppressWarnings("empty-statement")
    @Test public void testErrors() throws Exception
    {
        File tmp = File.createTempFile("ini4j", ".ini");

        // write only can't sync
        IniFile f = new IniFile(tmp, IniFile.Mode.WO);

        try
        {
            f.sync();
            fail();
        }
        catch (BackingStoreException x)
        {
            ;
        }

        // invalid file for write
        f = new IniFile(new File("/non existent path/to file"), IniFile.Mode.WO);
        try
        {
            f.flush();
            fail();
        }
        catch (BackingStoreException x)
        {
            ;
        }

        // invalid file for read
        try
        {
            f = new IniFile(new File("/non existent path/to file"), IniFile.Mode.RO);
            fail();
        }
        catch (BackingStoreException x)
        {
            ;
        }

        // read only can't flush
        f = new IniFile(tmp, IniFile.Mode.RO);
        try
        {
            f.sync();
            f.flush();
        }
        catch (BackingStoreException x)
        {
            ;
        }
    }

    /**
     * Test of flush method.
     *
     * @throws Exception on error
     */
    @Test public void testFlush() throws Exception
    {
        File tmp = File.createTempFile("ini4j", ".ini");
        IniFile f = new IniFile(tmp, IniFile.Mode.RW);

        assertEquals(IniFile.Mode.RW, f.getMode());
        assertEquals(tmp, f.getFile());
        f.node(Dwarfs.PROP_DOC).put(Dwarf.PROP_WEIGHT, "65");
        f.flush();
        f = new IniFile(tmp);
        assertEquals(f.node(Dwarfs.PROP_DOC).get(Dwarf.PROP_WEIGHT, null), "65");
        tmp.delete();
    }
}
