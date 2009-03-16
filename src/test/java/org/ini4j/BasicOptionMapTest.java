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
import org.ini4j.sample.DwarfBean;
import org.ini4j.sample.Dwarfs;

import org.ini4j.test.DwarfsData;
import org.ini4j.test.Helper;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class BasicOptionMapTest
{
    private static BasicOptionMap _map;

    @BeforeClass public static void setUpClass()
    {
        _map = new BasicOptionMap();
        _map.putAll(Helper.newDwarfsOpt());
    }

    @Test public void testFromToAs() throws Exception
    {
        DwarfBean bean = new DwarfBean();

        _map.to(bean, Dwarfs.PROP_BASHFUL + '.');
        Helper.assertEquals(DwarfsData.bashful, bean);
        OptionMap map = new BasicOptionMap();

        map.from(bean, Dwarfs.PROP_BASHFUL + '.');
        bean = new DwarfBean();
        map.to(bean, Dwarfs.PROP_BASHFUL + '.');
        Helper.assertEquals(DwarfsData.bashful, bean);
        Helper.assertEquals(DwarfsData.dopey, _map.as(Dwarf.class));
    }

    @Test public void testPrefixed() throws Exception
    {
        Helper.assertEquals(DwarfsData.bashful, _map.as(Dwarf.class, Dwarfs.PROP_BASHFUL + '.'));
        Helper.assertEquals(DwarfsData.doc, _map.as(Dwarf.class, Dwarfs.PROP_DOC + '.'));
        Helper.assertEquals(DwarfsData.dopey, _map.as(Dwarf.class, Dwarfs.PROP_DOPEY + '.'));
        Helper.assertEquals(DwarfsData.grumpy, _map.as(Dwarf.class, Dwarfs.PROP_GRUMPY + '.'));
        Helper.assertEquals(DwarfsData.happy, _map.as(Dwarf.class, Dwarfs.PROP_HAPPY + '.'));
        Helper.assertEquals(DwarfsData.sleepy, _map.as(Dwarf.class, Dwarfs.PROP_SLEEPY + '.'));
        Helper.assertEquals(DwarfsData.sneezy, _map.as(Dwarf.class, Dwarfs.PROP_SNEEZY + '.'));
    }

    @Test public void testResolve() throws Exception
    {
        StringBuilder buffer;
        String input;

        // simple value
        input = "${height}";
        buffer = new StringBuilder(input);

        _map.resolve(buffer);
        assertEquals("" + DwarfsData.dopey.getHeight(), buffer.toString());

        // system property
        input = "${@prop/user.home}";
        buffer = new StringBuilder(input);

        _map.resolve(buffer);
        assertEquals(System.getProperty("user.home"), buffer.toString());

        // system environment
        input = "${@env/PATH}";
        buffer = new StringBuilder(input);
        try
        {
            _map.resolve(buffer);
            assertEquals(System.getenv("PATH"), buffer.toString());
        }
        catch (Error e)
        {
            // retroweaver + JDK 1.4 throws Error on getenv
        }

        // unknown variable
        input = "${no such name}";
        buffer = new StringBuilder(input);

        _map.resolve(buffer);
        assertEquals(input, buffer.toString());

        // small input
        input = "${";
        buffer = new StringBuilder(input);

        _map.resolve(buffer);
        assertEquals(input, buffer.toString());

        // incorrect references
        input = "${weight";
        buffer = new StringBuilder(input);

        _map.resolve(buffer);
        assertEquals(input, buffer.toString());

        // empty references
        input = "jim${}";
        buffer = new StringBuilder(input);

        _map.resolve(buffer);
        assertEquals(input, buffer.toString());

        // escaped references
        input = "${weight}";
        buffer = new StringBuilder(input);

        _map.resolve(buffer);
        assertEquals("" + DwarfsData.dopey.getWeight(), buffer.toString());
        input = "\\" + input;
        buffer = new StringBuilder(input);

        assertEquals(input, buffer.toString());
    }
}
