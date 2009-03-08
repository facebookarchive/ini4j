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

import org.easymock.EasyMock;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.ByteArrayInputStream;

/**
 * JUnit test of IniParser class.
 */
public class IniParserTest
{
    private static final String[] _badIni =
        { "[section\noption=value\n", "[]\noption=value", "section\noption=value", "[section]\noption\n", "[section]\n=value\n", "[section]\n\\u000d\\u000d=value\n" };
    private static final String[] _badXML =
        { "[section\noption=value\n", "<?xml version='1.0' encoding='UTF-8'?>\n<ini></ini>", "<?xml version='1.0' encoding='UTF-8'?>\n<ini version='dummy'></ini>", "<?xml version='1.0' encoding='UTF-8'?>\n<ini version='1.0'><unknown-tag key='dummy'/></ini>", "<?xml version='1.0' encoding='UTF-8'?>\n<ini version='1.0'><section/></ini>", "<?xml version='1.0' encoding='UTF-8'?>\n<ini version='1.0'><section key='sec'><option/></section></ini>" };

    /**
     * Test of newInstance method.
     *
     * @throws Exception on error
     */
    @Test public void testNewInstance() throws Exception
    {
        assertNotNull(IniParser.newInstance());
    }

    /**
     * Test of parse method.
     *
     * @throws Exception on error
     */
    @SuppressWarnings("empty-statement")
    @Test public void testParse() throws Exception
    {
        IniParser parser = new IniParser();
        IniHandler handler = EasyMock.createNiceMock(IniHandler.class);

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
    }

    /**
     * Test of parseXML method.
     *
     * @throws Exception on error
     */
    @SuppressWarnings("empty-statement")
    @Test public void testParseXML() throws Exception
    {
        IniParser parser = new IniParser();
        IniHandler handler = EasyMock.createNiceMock(IniHandler.class);

        for (String s : _badXML)
        {
            try
            {
                parser.parseXML(new ByteArrayInputStream(s.getBytes()), handler);
                fail("expected InvalidIniFormatException");
            }
            catch (InvalidIniFormatException x)
            {
                ;
            }
        }
    }
}
