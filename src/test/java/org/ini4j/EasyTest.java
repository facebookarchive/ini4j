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

import static org.ini4j.test.DwarfsData.*;
import org.ini4j.test.Helper;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import java.net.URI;

public class EasyTest
{
    private static Ini ini;
    private static final String PROP_CLONE = "clone";

    @BeforeClass public static void setUpIni() throws IOException
    {
        ini = Helper.newDwarfsIni();
    }

    @Test public void testAddPutNullAndString()
    {
        OptionMap map = new OptionMapImpl();
        Object o;

        // null
        o = null;
        map.add(Dwarf.PROP_AGE, o);
        assertNull(map.get(Dwarf.PROP_AGE));
        map.put(Dwarf.PROP_AGE, doc.age);
        assertNotNull(map.get(Dwarf.PROP_AGE));
        map.add(Dwarf.PROP_AGE, o, 0);
        assertNull(map.get(Dwarf.PROP_AGE, 0));
        map.put(Dwarf.PROP_AGE, doc.age, 0);
        assertNotNull(map.get(Dwarf.PROP_AGE, 0));
        map.put(Dwarf.PROP_AGE, o, 0);
        assertNull(map.get(Dwarf.PROP_AGE, 0));
        map.remove(Dwarf.PROP_AGE);
        map.put(Dwarf.PROP_AGE, o);
        assertNull(map.get(Dwarf.PROP_AGE));

        // str
        map.remove(Dwarf.PROP_AGE);
        o = ini.get(PROP_DOC).get(Dwarf.PROP_AGE);
        map.add(Dwarf.PROP_AGE, o);
        assertEquals(o, map.get(Dwarf.PROP_AGE));
        map.remove(Dwarf.PROP_AGE);
        map.put(Dwarf.PROP_AGE, o);
        assertEquals(o, map.get(Dwarf.PROP_AGE));
        o = ini.get(PROP_HAPPY).get(Dwarf.PROP_AGE);
        map.add(Dwarf.PROP_AGE, o, 0);
        assertEquals(happy.age, (int) map.get(Dwarf.PROP_AGE, 0, int.class));
        o = ini.get(PROP_DOC).get(Dwarf.PROP_AGE);
        map.put(Dwarf.PROP_AGE, o, 0);
        assertEquals(doc.age, (int) map.get(Dwarf.PROP_AGE, 0, int.class));
    }

    @Test public void testFetch()
    {
        OptionMap map;

        // dopey
        map = ini.get(PROP_DOPEY);
        assertEquals(dopey.weight, map.fetch(Dwarf.PROP_WEIGHT, double.class), Helper.DELTA);
        map.add(Dwarf.PROP_HEIGHT, map.get(Dwarf.PROP_HEIGHT));
        assertEquals(dopey.height, map.fetch(Dwarf.PROP_HEIGHT, 1, double.class), Helper.DELTA);

        // sneezy
        map = ini.get(PROP_SNEEZY);
        assertEquals(sneezy.homePage, map.fetch(Dwarf.PROP_HOME_PAGE, URI.class));

        // null
        map = new OptionMapImpl();
        map.add(Dwarf.PROP_AGE, null);
        assertNull(map.fetch(Dwarf.PROP_AGE, 0));
    }

    @Test public void testGet()
    {
        OptionMap map;

        // bashful
        map = ini.get(PROP_BASHFUL);
        assertEquals(bashful.weight, map.get(Dwarf.PROP_WEIGHT, double.class), Helper.DELTA);
        map.add(Dwarf.PROP_HEIGHT, map.get(Dwarf.PROP_HEIGHT));
        assertEquals(bashful.height, map.get(Dwarf.PROP_HEIGHT, 1, double.class), Helper.DELTA);
        assertEquals(bashful.homePage, map.fetch(Dwarf.PROP_HOME_PAGE, URI.class));
    }

    @Test public void testIniAddPut()
    {
        ini.add(PROP_CLONE, Dwarf.PROP_AGE, sneezy.age);
        ini.put(PROP_CLONE, Dwarf.PROP_HEIGHT, sneezy.height);
        ini.add(PROP_CLONE, Dwarf.PROP_HOME_DIR, sneezy.homeDir);
        ini.add(PROP_CLONE, Dwarf.PROP_WEIGHT, sneezy.weight);
        ini.put(PROP_CLONE, Dwarf.PROP_HOME_PAGE, null);
        ini.put(PROP_CLONE, Dwarf.PROP_HOME_PAGE, sneezy.homePage);
        ini.add(PROP_CLONE, Dwarf.PROP_FORTUNE_NUMBER, sneezy.fortuneNumber[0]);
        ini.add(PROP_CLONE, Dwarf.PROP_FORTUNE_NUMBER, sneezy.fortuneNumber[1]);
        ini.add(PROP_CLONE, Dwarf.PROP_FORTUNE_NUMBER, sneezy.fortuneNumber[2]);
        ini.add(PROP_CLONE, Dwarf.PROP_FORTUNE_NUMBER, sneezy.fortuneNumber[3]);
        Helper.assertEquals(sneezy, ini.get(PROP_CLONE).as(Dwarf.class));
        assertNotNull(ini.remove(PROP_CLONE, Dwarf.PROP_FORTUNE_NUMBER));
        assertEquals(0, ini.get(PROP_CLONE).length(Dwarf.PROP_FORTUNE_NUMBER));
        assertNotNull(ini.remove(PROP_CLONE));
        assertNull(ini.remove(PROP_CLONE, Dwarf.PROP_FORTUNE_NUMBER));
    }

    @Test public void testIniGetFetch()
    {
        Ini.Section sec = ini.get(PROP_DOPEY);

        assertEquals(sec.get(Dwarf.PROP_AGE), ini.get(PROP_DOPEY, Dwarf.PROP_AGE));
        assertEquals(dopey.age, (int) ini.get(PROP_DOPEY, Dwarf.PROP_AGE, int.class));
        assertEquals(sec.get(Dwarf.PROP_WEIGHT), ini.get(PROP_DOPEY, Dwarf.PROP_WEIGHT));
        assertEquals(dopey.weight, ini.fetch(PROP_DOPEY, Dwarf.PROP_WEIGHT, double.class), Helper.DELTA);
        assertEquals(sec.fetch(Dwarf.PROP_HEIGHT), ini.fetch(PROP_DOPEY, Dwarf.PROP_HEIGHT));
        assertEquals(dopey.weight, ini.fetch(PROP_DOPEY, Dwarf.PROP_WEIGHT, double.class), Helper.DELTA);
        assertEquals(sec.fetch(Dwarf.PROP_HOME_PAGE), ini.fetch(PROP_DOPEY, Dwarf.PROP_HOME_PAGE));
        assertEquals(dopey.homePage, ini.fetch(PROP_DOPEY, Dwarf.PROP_HOME_PAGE, URI.class));

        // nulls
        assertNull(ini.get(PROP_CLONE, Dwarf.PROP_AGE));
        assertEquals(0, (int) ini.get(PROP_CLONE, Dwarf.PROP_AGE, int.class));
        assertNull(ini.get(PROP_CLONE, Dwarf.PROP_WEIGHT));
        assertEquals(0.0, ini.fetch(PROP_CLONE, Dwarf.PROP_WEIGHT, double.class), Helper.DELTA);
        assertNull(ini.fetch(PROP_CLONE, Dwarf.PROP_HEIGHT));
        assertEquals(0.0, ini.fetch(PROP_CLONE, Dwarf.PROP_WEIGHT, double.class), Helper.DELTA);
        assertNull(ini.fetch(PROP_CLONE, Dwarf.PROP_HOME_PAGE));
        assertNull(ini.fetch(PROP_CLONE, Dwarf.PROP_HOME_PAGE, URI.class));
    }

    @Test public void testPut()
    {
        OptionMap map = new OptionMapImpl();

        map.add(Dwarf.PROP_AGE, sneezy.age);
        map.put(Dwarf.PROP_HEIGHT, sneezy.height);
        map.add(Dwarf.PROP_HOME_DIR, sneezy.homeDir);
        map.add(Dwarf.PROP_WEIGHT, sneezy.weight, 0);
        map.put(Dwarf.PROP_HOME_PAGE, null);
        map.put(Dwarf.PROP_HOME_PAGE, sneezy.homePage);
        map.add(Dwarf.PROP_FORTUNE_NUMBER, sneezy.fortuneNumber[1]);
        map.add(Dwarf.PROP_FORTUNE_NUMBER, sneezy.fortuneNumber[2]);
        map.add(Dwarf.PROP_FORTUNE_NUMBER, 0);
        map.put(Dwarf.PROP_FORTUNE_NUMBER, sneezy.fortuneNumber[3], 2);
        map.add(Dwarf.PROP_FORTUNE_NUMBER, sneezy.fortuneNumber[0], 0);
        Helper.assertEquals(sneezy, map.as(Dwarf.class));
    }
}
