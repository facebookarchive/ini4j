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

import static org.junit.Assert.*;

import org.junit.Test;

public class BasicRegistryTest
{
    private static final String KEY = "key";
    private static final String DUMMY = "dummy";
    public static final String UNQUOTED1 = "simple";
    public static final String QUOTED1 = "\"simple\"";
    public static final String UNQUOTED2 = "no\\csak\"";
    public static final String QUOTED2 = "\"no\\\\csak\\\"\"";
    public static final String UNQUOTED3 = "";
    public static final String QUOTED3 = "";
    private static final String VERSION = "Windows Registry Editor Version 5.00";

    @Test public void testHexadecimal()
    {
        BasicRegistry reg = new BasicRegistry();

        assertEquals(0, reg.hexadecimal(null).length());
        assertEquals(0, reg.hexadecimal("").length());
    }

    @Test public void testQuote() throws Exception
    {
        BasicRegistry reg = new BasicRegistry();

        assertEquals(QUOTED1, reg.quote(UNQUOTED1));
        assertEquals(QUOTED2, reg.quote(UNQUOTED2));
        assertEquals(QUOTED3, reg.quote(UNQUOTED3));
        assertNull(reg.quote(null));
    }

    @Test public void testVersion()
    {
        BasicRegistry reg = new BasicRegistry();

        assertEquals(VERSION, reg.getVersion());
        reg.setVersion(DUMMY);
        assertEquals(DUMMY, reg.getVersion());
    }

    @Test public void testWrapped() throws Exception
    {
        BasicRegistry reg = new BasicRegistry();
        Registry.Key key1 = reg.add(KEY);
        Registry.Key key2 = reg.add(KEY);

        assertNotNull(key1);
        assertNotNull(key2);
        assertTrue(reg.get(KEY) instanceof Registry.Key);
        assertTrue(reg.get(KEY, 1) instanceof Registry.Key);
        Registry.Key key3 = new BasicRegistryKey(reg, KEY);

        assertSame(key1, reg.put(KEY, key3, 0));
        assertSame(key2, reg.put(KEY, key3));
        assertSame(key3, reg.remove(KEY, 1));
        assertSame(key3, reg.remove(KEY));
        key1 = reg.add(KEY);
        assertSame(key1, reg.remove(key1));
    }
}
