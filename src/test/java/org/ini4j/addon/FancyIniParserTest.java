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

import org.easymock.EasyMock;

import org.ini4j.Config;
import org.ini4j.Helper;
import org.ini4j.Ini;
import org.ini4j.IniHandler;
import org.ini4j.IniParser;
import org.ini4j.InvalidIniFormatException;

import org.ini4j.sample.Dwarfs;

import org.junit.AfterClass;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.ByteArrayInputStream;

import java.net.MalformedURLException;

@SuppressWarnings("deprecation")
public class FancyIniParserTest
{
    private static final String[] _extraIni = { "[section]\noption\n", "[]\noption=value\n", "option=value\n" };
    private static final String[] _badIni = { "[section\noption=value\n", "[section]\n=value\n" };
    private static final String MIXEDCASE = "[SectioN]\n\nOptioN=ValuE\n";
    private static final String MORESECTIONS = "[Section]\noption=value\n\n[second]\noption=value\n";
    private static final String UNNAMED = "option=value\n";
    private static final String ANONYMOUS = "?";
    private static final String EMPTY = "";
    private static final String INCLUDE = "org/ini4j/addon/dwarfs-include.ini";
    private static final String NESTED = "org/ini4j/addon/dwarfs-nested.ini";

    @AfterClass public static void tearDownClass() throws Exception
    {
        Helper.resetConfig();
    }

    @Test public void testConvertCase() throws Exception
    {
        class Handler implements IniHandler
        {
            boolean sectionOK;
            boolean optionOK;

            @SuppressWarnings("empty-statement")
            @Override public void startIni()
            {
                ;
            }

            @Override
            @SuppressWarnings("empty-statement")
            public void endIni()
            {
                ;
            }

            @Override public void handleOption(String name, String value)
            {
                assertEquals(name.toLowerCase(), name);
                assertFalse(value.equals(value.toLowerCase()));
                optionOK = true;
            }

            @Override public void startSection(String sectionName)
            {
                assertEquals(sectionName.toLowerCase(), sectionName);
                sectionOK = true;
            }

            @Override
            @SuppressWarnings("empty-statement")
            public void endSection()
            {
                ;
            }
        }

        FancyIniParser parser = new FancyIniParser();

        parser.setAllowSectionCaseConversion(true);
        parser.setAllowOptionCaseConversion(true);
        Handler handler = new Handler();

        parser.parse(new ByteArrayInputStream(MIXEDCASE.getBytes()), handler);
        assertTrue(handler.sectionOK);
        assertTrue(handler.optionOK);
    }

    @Test public void testDefaults() throws Exception
    {
        FancyIniParser parser = new FancyIniParser();

        parser.setConfig(new Config());
        assertTrue(parser.isAllowEmptyOption());
        assertTrue(parser.isAllowMissingSection());
        assertFalse(parser.isAllowOptionCaseConversion());
        assertFalse(parser.isAllowSectionCaseConversion());
        assertTrue(parser.isAllowUnnamedSection());
    }

    @SuppressWarnings("empty-statement")
    @Test public void testInclude() throws Exception
    {
        System.setProperty(IniParser.class.getName(), FancyIniParser.class.getName());
        Ini ini = new Ini(getClass().getClassLoader().getResource(INCLUDE));

        Helper.doTestDwarfs(ini.to(Dwarfs.class));
        try
        {
            ini = new Ini(getClass().getClassLoader().getResourceAsStream(INCLUDE));
            fail();
        }
        catch (MalformedURLException x)
        {
            ;
        }

        ini = new Ini(getClass().getClassLoader().getResource(NESTED));
        Helper.doTestDwarfs(ini.to(Dwarfs.class));
        FancyIniParser parser = (FancyIniParser) IniParser.newInstance();

        assertTrue(parser.isAllowInclude());
        parser.setAllowInclude(false);
        assertFalse(parser.isAllowInclude());
    }

    /**
     * Test of newInstance method.
     *
     * @throws Exception on error
     */
    @Test public void testNewInstance() throws Exception
    {
        System.setProperty(IniParser.class.getName(), FancyIniParser.class.getName());
        FancyIniParser parser = (FancyIniParser) IniParser.newInstance();

        assertNotNull(parser);
    }

    /**
     * Test of parse method.
     *
     * @throws Exception on error
     */
    @SuppressWarnings("empty-statement")
    @Test public void testParse() throws Exception
    {
        FancyIniParser parser = new FancyIniParser();
        IniHandler handler = EasyMock.createNiceMock(IniHandler.class);

        // add-on extension should work by default
        for (String s : _extraIni)
        {
            parser.parse(new ByteArrayInputStream(s.getBytes()), handler);
        }

        // disable add-on features and expect error
        parser.setAllowEmptyOption(false);
        parser.setAllowMissingSection(false);
        parser.setAllowUnnamedSection(false);
        for (String s : _extraIni)
        {
            try
            {
                parser.parse(new ByteArrayInputStream(s.getBytes()), handler);
                fail("expected InvalidIniFormatException: " + s);
            }
            catch (InvalidIniFormatException x)
            {
                ;
            }
        }

        for (String s : _badIni)
        {
            try
            {
                parser.parse(new ByteArrayInputStream(s.getBytes()), handler);
                fail("expected InvalidIniFormatException: " + s);
            }
            catch (InvalidIniFormatException x)
            {
                ;
            }
        }

        parser.parse(new ByteArrayInputStream(EMPTY.getBytes()), handler);
        parser.parse(new ByteArrayInputStream(MORESECTIONS.getBytes()), handler);
        parser.setMissingSectionName(ANONYMOUS);
        assertEquals(ANONYMOUS, parser.getMissingSectionName());
        System.setProperty(IniParser.class.getName(), FancyIniParser.class.getName());
        Ini ini = new Ini(new ByteArrayInputStream(UNNAMED.getBytes()));

        assertNotNull(ini);
        assertNotNull(ini.get(ANONYMOUS));
        assertEquals(1, ini.size());
    }
}
