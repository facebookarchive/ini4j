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

import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;

@Ignore public class OptionsTest
{
    private static final String[] _badOptions = { "=value\n", "\\u000d\\u000d=value\n" };

    @Test public void testBean() throws Exception
    {
        Options opts = Helper.loadDwarfsOpt();
        DwarfBean bean = new DwarfBean();

        opts.to(bean, Dwarfs.PROP_BASHFUL + '.');
        Helper.assertEquals(DwarfsData.bashful, bean);
        OptionMap map = new BasicOptionMap();

        map.from(bean, Dwarfs.PROP_BASHFUL + '.');
        bean = new DwarfBean();
        map.to(bean, Dwarfs.PROP_BASHFUL + '.');
        Helper.assertEquals(DwarfsData.bashful, bean);
    }

    @Test public void testConfig()
    {
        Options opts = new Options();
        Config conf = opts.getConfig();

        assertTrue(conf.isEmptyOption());
        assertTrue(conf.isEscape());
        assertFalse(conf.isInclude());
        assertTrue(conf.isMultiOption());
        conf = new Config();
        opts.setConfig(conf);
        assertSame(conf, opts.getConfig());
    }

    @Test public void testDwarfs() throws Exception
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Options happy = new Options();

        happy.from(DwarfsData.happy);
        happy.store(buffer);
        Options dup = new Options(new ByteArrayInputStream(buffer.toByteArray()));

        Helper.assertEquals(DwarfsData.happy, dup.as(Dwarf.class));
        buffer = new ByteArrayOutputStream();
        happy.store(new OutputStreamWriter(buffer));
        dup = new Options(new ByteArrayInputStream(buffer.toByteArray()));
        Helper.assertEquals(DwarfsData.happy, dup.as(Dwarf.class));
        File file = File.createTempFile("test", ".opt");

        file.deleteOnExit();
        happy.setFile(file);
        happy.store();
        dup = new Options();
        dup.setFile(file);
        assertEquals(file, dup.getFile());
        dup.load();
        Helper.assertEquals(DwarfsData.happy, dup.as(Dwarf.class));
        file.delete();
    }

    @Test public void testEscape() throws Exception
    {
        Options opts = new Options();
        Config cfg = new Config();

        assertEquals("c:\\\\dummy", opts.escape("c:\\dummy"));
        cfg.setEscape(false);
        opts.setConfig(cfg);
        assertEquals("c:\\dummy", opts.escape("c:\\dummy"));
    }

    @Test public void testLoad() throws Exception
    {
        Options o1 = new Options(Helper.getResourceURL(Helper.DWARFS_OPT));
        Options o2 = new Options(Helper.getResourceURL(Helper.DWARFS_OPT).openStream());
        Options o3 = new Options(new InputStreamReader(Helper.getResourceURL(Helper.DWARFS_OPT).openStream()));
        Options o4 = new Options(Helper.getResourceURL(Helper.DWARFS_OPT));
        Options o5 = new Options(Helper.getSourceFile(Helper.DWARFS_OPT));
        Options o6 = new Options();

        o6.setFile(Helper.getSourceFile(Helper.DWARFS_OPT));
        o6.load();
        Helper.assertEquals(DwarfsData.dopey, o1.as(Dwarf.class));
        Helper.assertEquals(DwarfsData.dopey, o2.as(Dwarf.class));
        Helper.assertEquals(DwarfsData.dopey, o3.as(Dwarf.class));
        Helper.assertEquals(DwarfsData.dopey, o4.as(Dwarf.class));
        Helper.assertEquals(DwarfsData.dopey, o5.as(Dwarf.class));
        Helper.assertEquals(DwarfsData.dopey, o6.as(Dwarf.class));
    }

    @Test(expected = FileNotFoundException.class)
    public void testLoadException() throws Exception
    {
        Options opt = new Options();

        opt.load();
    }

    @Test public void testLowerCase() throws Exception
    {
        Config cfg = new Config();
        Options opts = new Options();

        cfg.setLowerCaseOption(true);
        opts.setConfig(cfg);
        opts.load(new StringReader("OptIon=value\n"));
        assertTrue(opts.containsKey("option"));
    }

    @Test public void testMultiOption() throws Exception
    {
        Config cfg = new Config();

        cfg.setMultiOption(true);
        Options opts = Helper.loadDwarfsOpt(cfg);

        assertEquals(2, opts.length(Dwarfs.PROP_HAPPY + '.' + Dwarf.PROP_HOME_PAGE));
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        opts.store(buffer);
        opts = new Options();
        opts.setConfig(cfg);
        opts.load(new ByteArrayInputStream(buffer.toByteArray()));
        assertEquals(2, opts.length(Dwarfs.PROP_HAPPY + '.' + Dwarf.PROP_HOME_PAGE));
    }

    @Test(expected = InvalidFileFormatException.class)
    public void testNoEmptyOption() throws Exception
    {
        Config cfg = new Config();
        Options opts = new Options();

        opts.setConfig(cfg);
        opts.load(new StringReader("dummy\n"));
        assertTrue(opts.containsKey("dummy"));
        assertNull(opts.get("dummy"));
        cfg.setEmptyOption(false);
        opts.load(new StringReader("foo\n"));
    }

    @Test
    @SuppressWarnings("empty-statement")
    public void testParseError() throws Exception
    {
        for (String s : _badOptions)
        {
            try
            {
                new Options(new ByteArrayInputStream(s.getBytes()));
                fail("expected InvalidIniFormatException: " + s);
            }
            catch (InvalidFileFormatException x)
            {
                ;
            }
        }
    }

    @Test public void testPrefixed() throws Exception
    {
        Options opts = Helper.loadDwarfsOpt();

        Helper.assertEquals(DwarfsData.bashful, opts.as(Dwarf.class, Dwarfs.PROP_BASHFUL + '.'));
        Helper.assertEquals(DwarfsData.doc, opts.as(Dwarf.class, Dwarfs.PROP_DOC + '.'));
        Helper.assertEquals(DwarfsData.dopey, opts.as(Dwarf.class, Dwarfs.PROP_DOPEY + '.'));
        Helper.assertEquals(DwarfsData.grumpy, opts.as(Dwarf.class, Dwarfs.PROP_GRUMPY + '.'));
        Helper.assertEquals(DwarfsData.happy, opts.as(Dwarf.class, Dwarfs.PROP_HAPPY + '.'));
        Helper.assertEquals(DwarfsData.sleepy, opts.as(Dwarf.class, Dwarfs.PROP_SLEEPY + '.'));
        Helper.assertEquals(DwarfsData.sneezy, opts.as(Dwarf.class, Dwarfs.PROP_SNEEZY + '.'));
    }

    @Test public void testResolve() throws Exception
    {
        Options opts = Helper.loadDwarfsOpt();
        StringBuilder buffer;
        String input;

        // simple value
        input = "${height}";
        buffer = new StringBuilder(input);

        opts.resolve(buffer);
        assertEquals("" + DwarfsData.dopey.getHeight(), buffer.toString());

        // system property
        input = "${@prop/user.home}";
        buffer = new StringBuilder(input);

        opts.resolve(buffer);
        assertEquals(System.getProperty("user.home"), buffer.toString());

        // system environment
        input = "${@env/PATH}";
        buffer = new StringBuilder(input);
        try
        {
            opts.resolve(buffer);
            assertEquals(System.getenv("PATH"), buffer.toString());
        }
        catch (Error e)
        {
            // retroweaver + JDK 1.4 throws Error on getenv
        }

        // unknown variable
        input = "${no such name}";
        buffer = new StringBuilder(input);

        opts.resolve(buffer);
        assertEquals(input, buffer.toString());

        // small input
        input = "${";
        buffer = new StringBuilder(input);

        opts.resolve(buffer);
        assertEquals(input, buffer.toString());

        // incorrect references
        input = "${weight";
        buffer = new StringBuilder(input);

        opts.resolve(buffer);
        assertEquals(input, buffer.toString());

        // empty references
        input = "jim${}";
        buffer = new StringBuilder(input);

        opts.resolve(buffer);
        assertEquals(input, buffer.toString());

        // escaped references
        input = "${weight}";
        buffer = new StringBuilder(input);

        opts.resolve(buffer);
        assertEquals("" + DwarfsData.dopey.getWeight(), buffer.toString());
        input = "\\" + input;
        buffer = new StringBuilder(input);

        assertEquals(input, buffer.toString());
    }

    @Test(expected = FileNotFoundException.class)
    public void testStoreException() throws Exception
    {
        Options opt = new Options();

        opt.store();
    }
}
