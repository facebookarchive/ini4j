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

import org.ini4j.sample.Dwarf;
import org.ini4j.sample.Dwarfs;

import org.junit.AfterClass;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * JUnit test of Ini class.
 */
public class IniTest
{
    private static final String UNICODE_STRING = "áÁéÉíÍóÓöÖőŐúÚüÜűŰ-ÄÖÜäöü";
    private static final String DOC_HOME_DIR = "c:\\Documents and Settings\\doc";
    private static final String DOPEY_HOME_DIR = "c:\\\\Documents and Settings\\\\dopey";

    @AfterClass public static void tearDownClass() throws Exception
    {
        Helper.resetConfig();
    }

    /**
     * Test of bean related methods.
     *
     * @throws Exception on error
     */
    @SuppressWarnings("deprecation")
    @Test public void testBeanInterface() throws Exception
    {
        Dwarfs exp = Helper.newDwarfs();
        Ini ini = Helper.newDwarfsIni();
        Ini.Section sec = ini.get(Dwarfs.PROP_DOC);
        Dwarf bean = Helper.newDwarf();

        sec.to(bean);
        Helper.assertEquals(exp.getDoc(), bean);
        sec.clear();
        sec.from(bean);
        assertEquals(5, sec.size());
        Helper.assertEquals(exp.getDoc(), sec);

        //
        // deprecated api test
        bean = sec.to(Dwarf.class);
        assertSame(bean, sec.to(Dwarf.class));
        Helper.assertEquals(exp.getDoc(), bean);
    }

    @Test public void testConfig() throws Exception
    {
        Config cfg = Config.getGlobal().clone();

        cfg.setMultiSection(true);
        Ini ini = Helper.loadDwarfsIni(cfg);

        assertEquals(2, ini.length(Dwarfs.PROP_HAPPY));
        Ini.Section happy1 = ini.get(Dwarfs.PROP_HAPPY, 0);
        Ini.Section happy2 = ini.get(Dwarfs.PROP_HAPPY, 1);

        assertEquals(5, happy1.size());
        assertEquals(1, happy2.size());
        cfg.setMultiSection(false);
        cfg.setMultiOption(true);
        ini = Helper.loadDwarfsIni(cfg);
        Ini.Section happy = ini.get(Dwarfs.PROP_HAPPY);

        assertEquals(5, happy.size());
        assertEquals(2, happy.length(Dwarf.PROP_HOME_PAGE));
    }

    @Test public void testEscape() throws Exception
    {
        Config config = new Config();

        config.setEscape(false);
        Ini ini = Helper.loadDwarfsIni(config);
        Ini.Section doc = ini.get(Dwarfs.PROP_DOC);
        Ini.Section dopey = ini.get(Dwarfs.PROP_DOPEY);

        assertEquals(DOC_HOME_DIR, doc.get(Dwarf.PROP_HOME_DIR));
        assertEquals(DOPEY_HOME_DIR, dopey.get(Dwarf.PROP_HOME_DIR));
    }

    /**
     * Test of load method.
     *
     * @throws Exception on error
     */
    @Test public void testLoad() throws Exception
    {
        Ini ini;

        ini = new Ini(Helper.getResourceURL(Helper.DWARFS_INI));
        Helper.doTestDwarfs(ini.as(Dwarfs.class));
        ini = new Ini(Helper.getResourceStream(Helper.DWARFS_INI));
        Helper.doTestDwarfs(ini.as(Dwarfs.class));
        ini = new Ini(Helper.getResourceReader(Helper.DWARFS_INI));
        Helper.doTestDwarfs(ini.as(Dwarfs.class));
    }

    /**
     * Test of loadFromXML method.
     *
     * @throws Exception on error
     */
    @Test public void testLoadFromXML() throws Exception
    {
        Ini ini = new Ini();

        ini.loadFromXML(getClass().getClassLoader().getResourceAsStream(Helper.DWARFS_XML));
        Helper.doTestDwarfs(ini.as(Dwarfs.class));
        ini.loadFromXML(getClass().getClassLoader().getResource(Helper.DWARFS_XML));
        Helper.doTestDwarfs(ini.as(Dwarfs.class));
    }

    /**
     * Test of remove method.
     *
     * @throws Exception on error
     */
    @Test public void testRemove() throws Exception
    {
        Ini ini = Helper.newDwarfsIni();

        ini.remove(ini.get(Dwarfs.PROP_DOC));
        assertNull(ini.get(Dwarfs.PROP_DOC));
    }

    /**
     * Test of resolve method.
     *
     * @throws Exception on error
     */
    @Test public void testResolve() throws Exception
    {
        Ini ini = Helper.newDwarfsIni();
        Ini.Section doc = ini.get(Dwarfs.PROP_DOC);
        Dwarfs dwarfs = ini.as(Dwarfs.class);
        StringBuilder buffer;
        String input;

        // other sections's value
        input = "${happy/weight}";
        buffer = new StringBuilder(input);

        ini.resolve(buffer, doc);
        assertEquals("" + dwarfs.getHappy().getWeight(), buffer.toString());

        // same sections's value
        input = "${height}";
        buffer = new StringBuilder(input);

        ini.resolve(buffer, doc);
        assertEquals("" + dwarfs.getDoc().getHeight(), buffer.toString());

        // system property
        input = "${@prop/user.home}";
        buffer = new StringBuilder(input);

        ini.resolve(buffer, doc);
        assertEquals(System.getProperty("user.home"), buffer.toString());

        // system environment
        input = "${@env/PATH}";
        buffer = new StringBuilder(input);
        try
        {
            ini.resolve(buffer, doc);
            assertEquals(System.getenv("PATH"), buffer.toString());
        }
        catch (Error e)
        {
            // retroweaver + JDK 1.4 throws Error on getenv
        }

        // unknown variable
        input = "${no such name}";
        buffer = new StringBuilder(input);

        ini.resolve(buffer, doc);
        assertEquals(input, buffer.toString());

        // unknown section's unknown variable
        input = "${no such section/no such name}";
        buffer = new StringBuilder(input);

        ini.resolve(buffer, doc);
        assertEquals(input, buffer.toString());

        // other section's unknown variable
        input = "${happy/no such name}";
        buffer = new StringBuilder(input);

        ini.resolve(buffer, doc);
        assertEquals(input, buffer.toString());

        // small input
        input = "${";
        buffer = new StringBuilder(input);

        ini.resolve(buffer, doc);
        assertEquals(input, buffer.toString());

        // incorrect references
        input = "${doc/weight";
        buffer = new StringBuilder(input);

        ini.resolve(buffer, doc);
        assertEquals(input, buffer.toString());

        // empty references
        input = "jim${}";
        buffer = new StringBuilder(input);

        ini.resolve(buffer, doc);
        assertEquals(input, buffer.toString());

        // escaped references
        input = "${happy/weight}";
        buffer = new StringBuilder(input);

        ini.resolve(buffer, doc);
        assertEquals("" + dwarfs.getHappy().getWeight(), buffer.toString());
        input = "\\" + input;
        buffer = new StringBuilder(input);

        assertEquals(input, buffer.toString());
    }

    /**
     * Test of store method.
     *
     * @throws Exception on error
     */
    @Test public void testStore() throws Exception
    {
        Ini ini = Helper.newDwarfsIni();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        ini.store(buffer);
        Ini dup = new Ini();

        dup.load(new ByteArrayInputStream(buffer.toByteArray()));
        Helper.doTestDwarfs(dup.as(Dwarfs.class));
        buffer = new ByteArrayOutputStream();
        ini.store(new OutputStreamWriter(buffer));
        dup = new Ini();
        dup.load(new InputStreamReader(new ByteArrayInputStream(buffer.toByteArray())));
        Helper.doTestDwarfs(dup.as(Dwarfs.class));
    }

    /**
     * Test of storeToXML method.
     *
     * @throws Exception on error
     */
    @Test public void testStoreToXML() throws Exception
    {
        Ini ini = Helper.newDwarfsIni();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        ini.storeToXML(buffer);
        Ini dup = new Ini();

        dup.loadFromXML(new ByteArrayInputStream(buffer.toByteArray()));
        Helper.doTestDwarfs(dup.as(Dwarfs.class));
        buffer = new ByteArrayOutputStream();
        ini.storeToXML(new OutputStreamWriter(buffer));
        dup = new Ini();
        dup.loadFromXML(new InputStreamReader(new ByteArrayInputStream(buffer.toByteArray())));
        Helper.doTestDwarfs(dup.as(Dwarfs.class));
    }

    @Test public void testToBean() throws Exception
    {
        Ini ini = Helper.newDwarfsIni();
        Ini.Section sec = ini.get(Dwarfs.PROP_DOC);
        Dwarf doc = sec.as(Dwarf.class);
    }

    @Test public void testUnicode() throws Exception
    {
        Ini orig = new Ini();
        Ini.Section bashful = orig.add(Dwarfs.PROP_BASHFUL);

        bashful.put(Dwarf.PROP_HOME_PAGE, UNICODE_STRING);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        orig.store(out);
        Ini saved = new Ini(new ByteArrayInputStream(out.toByteArray()));
        Ini.Section bashfulSaved = saved.get(Dwarfs.PROP_BASHFUL);

        assertEquals(bashful.get(Dwarf.PROP_HOME_PAGE), bashfulSaved.get(Dwarf.PROP_HOME_PAGE));
    }

    static interface Tale extends Dwarfs
    {
        Snowwhite getSnowwhite();

        void setSnowwhite(Snowwhite s);

        boolean hasSnowwhite();

        static interface Snowwhite
        {
            String getEmail();

            void setEmail(String email);
        }
    }
}
