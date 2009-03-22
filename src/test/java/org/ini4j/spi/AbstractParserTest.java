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
package org.ini4j.spi;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class AbstractParserTest
{
    private static final String QUOTED1 = "'simple'";
    private static final String VALUE1 = "simple";
    private static final String QUOTED2 = "\"simple\"";
    private static final String VALUE2 = "simple";
    private static final String QUOTED3 = "'simple";
    private static final String VALUE3 = "'simple";
    private static final String QUOTED4 = "simple'";
    private static final String VALUE4 = "simple'";
    private static final String QUOTED5 = "\"simple";
    private static final String VALUE5 = "\"simple";
    private static final String QUOTED6 = "simple\"";
    private static final String VALUE6 = "simple\"";
    private static final String QUOTED7 = "";
    private static final String VALUE7 = "";
    private static final String QUOTED8 = null;
    private static final String VALUE8 = null;
    private AbstractParser _instance;

    @Before public void setUp()
    {
        _instance = new Parser();
    }

    @Test public void testUnquote()
    {
        assertEquals(VALUE1, _instance.unquote(QUOTED1));
        assertEquals(VALUE2, _instance.unquote(QUOTED2));
        assertEquals(VALUE3, _instance.unquote(QUOTED3));
        assertEquals(VALUE4, _instance.unquote(QUOTED4));
        assertEquals(VALUE5, _instance.unquote(QUOTED5));
        assertEquals(VALUE6, _instance.unquote(QUOTED6));
        assertEquals(VALUE7, _instance.unquote(QUOTED7));
        assertEquals(VALUE8, _instance.unquote(QUOTED8));
    }

    private static class Parser extends AbstractParser
    {
        private Parser()
        {
            super("=:", ";#!");
        }
    }
}
