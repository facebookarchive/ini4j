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

import java.io.StringReader;

import java.net.URI;

public class ArrayTest
{
    private static final String NORMAL = "[section]\nnumber=1\nlocation=http://www.ini4j.org\nnumber=2\nlocation=http://ini4j.org\n\n";
    private static final String SECTIONS = "[section]\nnumber=1\n\n[section]\nnumber=2\n\n[single]\nnumber=3\n\n";

    @Test public void testOptionArray() throws Exception
    {
        Config cfg = new Config();

        cfg.setMultiOption(true);
        Ini ini = new Ini();

        ini.setConfig(cfg);
        ini.load(new StringReader(NORMAL));
        Section s = ini.get("section").as(Section.class);

        assertNotNull(s);
        assertEquals(2, s.getNumber().length);
        assertEquals(1, s.getNumber()[0]);
        assertEquals(2, s.getNumber()[1]);
        assertEquals(2, s.getLocation().length);
        assertEquals(new URI("http://www.ini4j.org"), s.getLocation()[0]);
        assertNull(s.getMissing());
        int[] numbers = new int[] { 1, 2, 3, 4, 5 };

        s.setNumber(numbers);
        Ini.Section sec = ini.get("section");

        assertEquals(5, sec.length("number"));
    }

    @Test public void testResolveArray() throws Exception
    {
        StringBuilder buffer;
        Config cfg = new Config();

        cfg.setMultiSection(true);
        cfg.setMultiOption(true);
        Ini ini = new Ini();
        Ini.Section sec;

        ini.setConfig(cfg);
        ini.load(new StringReader(SECTIONS));
        sec = ini.get("section");

        //
        buffer = new StringBuilder("${section[0]/number}");
        ini.resolve(buffer, sec);
        assertEquals("1", buffer.toString());
        buffer = new StringBuilder("${section[1]/number}");
        ini.resolve(buffer, sec);
        assertEquals("2", buffer.toString());
        buffer = new StringBuilder("${section[0]/number}-${section[1]/number}");
        ini.resolve(buffer, sec);
        assertEquals("1-2", buffer.toString());

        //
        ini.clear();
        ini.load(new StringReader(NORMAL));
        sec = ini.get("section");
        assertEquals(2, sec.length("number"));
        buffer = new StringBuilder("${number}");
        ini.resolve(buffer, sec);
        assertEquals("1", buffer.toString());
        buffer = new StringBuilder("${number[0]}-${section/number[1]}-${section[0]/number}");
        ini.resolve(buffer, sec);
        assertEquals("1-2-1", buffer.toString());
    }

    @Test public void testSectionArray() throws Exception
    {
        Config cfg = new Config();

        cfg.setMultiSection(true);
        Ini ini = new Ini();

        ini.setConfig(cfg);
        ini.load(new StringReader(SECTIONS));
        Global g = ini.as(Global.class);

        assertNotNull(g);
        assertEquals(2, g.getSection().length);
        assertEquals(1, g.getSingle().length);
        assertNull(g.getMissing());
        assertTrue(g.hasSection());
    }

    @Test public void testSetter()
    {
        Config cfg = new Config();

        cfg.setMultiSection(true);
        Ini ini = new Ini();

        ini.setConfig(cfg);
        Global g = ini.as(Global.class);
        Section s1 = new SectionBean();
        Section s2 = new SectionBean();
        Section[] all = new Section[] { s1, s2 };

        g.setSection(all);
        assertEquals(2, ini.length("section"));
        cfg.setMultiSection(false);
        g.setSection(all);
        assertEquals(1, ini.length("section"));
        assertNull(g.getSolo());
        g.setSolo(s1);
        assertNotNull(g.getSolo());
        g.setSolo(null);
        assertEquals(0, ini.length("solo"));
    }

    public static interface Global
    {
        Section[] getMissing();

        Section[] getSection();

        void setSection(Section[] value);

        Section[] getSingle();

        Section getSolo();

        void setSolo(Section value);

        boolean hasSection();
    }

    public static interface Section
    {
        URI[] getLocation();

        void setLocation(URI[] value);

        String[] getMissing();

        void setMissing(String[] value);

        int[] getNumber();

        void setNumber(int[] value);
    }

    public static class SectionBean implements Section
    {
        private URI[] _location;
        private String[] _missing;
        private int[] _number;

        @Override public URI[] getLocation()
        {
            return _location;
        }

        @Override public void setLocation(URI[] value)
        {
            _location = value;
        }

        @Override public String[] getMissing()
        {
            return _missing;
        }

        @Override public void setMissing(String[] value)
        {
            _missing = value;
        }

        @Override public int[] getNumber()
        {
            return _number;
        }

        @Override public void setNumber(int[] value)
        {
            _number = value;
        }
    }
}
