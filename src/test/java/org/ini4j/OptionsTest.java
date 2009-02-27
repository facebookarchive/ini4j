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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;

public class OptionsTest
{
    private static final String[] _badOptions = { "=value\n", "\\u000d\\u000d=value\n" };

    @Test public void testConfig()
    {
        Options opts = new Options();
        Config conf = opts.getConfig();

        assertTrue(conf.isEmptyOption());
        assertTrue(conf.isEscape());
        assertFalse(conf.isInclude());
        assertFalse(conf.isMultiOption());
        conf = new Config();
        opts.setConfig(conf);
        assertSame(conf, opts.getConfig());
    }

    @Test public void testDwarfs() throws Exception
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Dwarfs dwarfs = Helper.newDwarfs();
        Options happy = new Options();

        happy.from(dwarfs.getHappy());
        happy.store(buffer);
        Options dup = new Options(new ByteArrayInputStream(buffer.toByteArray()));

        Helper.assertEquals(dwarfs.getHappy(), dup);
        buffer = new ByteArrayOutputStream();
        happy.store(new OutputStreamWriter(buffer));
        dup = new Options(new ByteArrayInputStream(buffer.toByteArray()));
        Helper.assertEquals(dwarfs.getHappy(), dup);
    }

    @Test public void testEscapeUnescape() throws Exception
    {
        Options opts = new Options();
        Config cfg = new Config();

        assertEquals("c:\\\\dummy", opts.escape("c:\\dummy"));
        assertEquals("c:\\dummy", opts.unescape("c:\\\\dummy"));
        cfg.setEscape(false);
        opts.setConfig(cfg);
        assertEquals("c:\\dummy", opts.escape("c:\\dummy"));
        assertEquals("c:\\\\dummy", opts.unescape("c:\\\\dummy"));
    }

    @Test public void testLoad() throws Exception
    {
        Options bashful = new Options(Helper.getResource(Dwarfs.PROP_BASHFUL + Helper.PROPERTIES_SUFFIX));
        Options doc = new Options(Helper.getResource(Dwarfs.PROP_DOC + Helper.PROPERTIES_SUFFIX).openStream());
        Options dopey = new Options(new InputStreamReader(Helper.getResource(Dwarfs.PROP_DOPEY + Helper.PROPERTIES_SUFFIX).openStream()));
        Options happy = new Options(Helper.getResource(Dwarfs.PROP_HAPPY + Helper.PROPERTIES_SUFFIX));
        Dwarfs dwarfs = Helper.newDwarfs();

        Helper.assertEquals(dwarfs.getBashful(), bashful);
        Helper.assertEquals(dwarfs.getDoc(), doc);
        Helper.assertEquals(dwarfs.getDopey(), dopey);
        Helper.assertEquals(dwarfs.getHappy(), happy);
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
        Options opts = new Options();

        opts.setConfig(cfg);
        opts.load(Helper.getResource(Dwarfs.PROP_HAPPY + Helper.PROPERTIES_SUFFIX));
        assertEquals(2, opts.length(Dwarf.PROP_HOME_PAGE));
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        opts.store(buffer);
        opts = new Options();
        opts.setConfig(cfg);
        opts.load(new ByteArrayInputStream(buffer.toByteArray()));
        assertEquals(2, opts.length(Dwarf.PROP_HOME_PAGE));
    }

    @Test(expected = InvalidIniFormatException.class)
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
            catch (InvalidIniFormatException x)
            {
                ;
            }
        }
    }

    @Test public void testResolve() throws Exception
    {
        Dwarf doc = Helper.newDwarfs().getDoc();
        Options opts = new Options(Helper.getResource(Dwarfs.PROP_DOC + Helper.PROPERTIES_SUFFIX));
        StringBuilder buffer;
        String input;

        // simple value
        input = "${height}";
        buffer = new StringBuilder(input);

        opts.resolve(buffer);
        assertEquals("" + doc.getHeight(), buffer.toString());

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
        assertEquals("" + doc.getWeight(), buffer.toString());
        input = "\\" + input;
        buffer = new StringBuilder(input);

        assertEquals(input, buffer.toString());
    }
}
