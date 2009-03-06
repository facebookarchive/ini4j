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
package org.ini4j.addon;

import org.ini4j.Config;
import org.ini4j.Helper;
import org.ini4j.Ini;
import org.ini4j.IniHandler;

import org.ini4j.spi.IniFormatter;

import org.junit.AfterClass;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

@SuppressWarnings("deprecation")
public class FancyIniFormatterTest
{
    private static final String STRICTOPERATOR = "[section]\noption=value\n\n";
    private static final String NORMALOPERATOR = "[section]\noption = value\n\n";
    private static final String WITHDUMMY = "[section]\noption=value\ndummy=\n\n";
    public static final String DUMMY = "dummy";
    public static final String SECTION = "section";
    private IniFormatter formatter;
    private StringWriter output;

    @AfterClass public static void tearDownClass() throws Exception
    {
        Helper.resetConfig();
    }

    @Before public void setUp() throws Exception
    {
        System.setProperty(IniFormatter.class.getName(), FancyIniFormatter.class.getName());
        output = new StringWriter();
        formatter = (FancyIniFormatter) IniFormatter.newInstance(output);
    }

    @Test public void testDefaults() throws Exception
    {
        ((FancyIniFormatter) formatter).setConfig(new Config());
        assertTrue(((FancyIniFormatter) formatter).isAllowStrictOperator());
        assertTrue(((FancyIniFormatter) formatter).isAllowEmptyOption());
    }

    @Test public void testEmptyOption() throws Exception
    {
        ((FancyIniFormatter) formatter).setAllowEmptyOption(true);
        IniHelper ini = new IniHelper(new StringReader(STRICTOPERATOR));

        ini.get(SECTION).put(DUMMY, null);
        ini.store(formatter);
        assertEquals(WITHDUMMY, output.toString());
    }

    @Test public void testNoEmptyOption() throws Exception
    {
        ((FancyIniFormatter) formatter).setAllowEmptyOption(false);
        IniHelper ini = new IniHelper(new StringReader(STRICTOPERATOR));

        ini.get(SECTION).put(DUMMY, null);
        ini.store(formatter);
        System.err.println(output.toString());
        assertEquals(STRICTOPERATOR, output.toString());
    }

    @Test public void testNoStrictOperator() throws Exception
    {
        ((FancyIniFormatter) formatter).setAllowStrictOperator(false);
        IniHelper ini = new IniHelper(new StringReader(NORMALOPERATOR));

        ini.store(formatter);
        assertEquals(NORMALOPERATOR, output.toString());
    }

    @Test public void testStrictOperator() throws Exception
    {
        ((FancyIniFormatter) formatter).setAllowStrictOperator(true);
        IniHelper ini = new IniHelper(new StringReader(STRICTOPERATOR));

        ini.store(formatter);
        assertEquals(STRICTOPERATOR, output.toString());
    }

    protected static class IniHelper extends Ini
    {
        public IniHelper(Reader input) throws IOException
        {
            super(input);
        }

        @Override protected void store(IniHandler formatter) throws IOException
        {
            super.store(formatter);
        }
    }
}
