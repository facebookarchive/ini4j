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

import java.util.HashMap;
import java.util.Map;

public class EscapeToolTest
{
    protected EscapeTool instance;

    @Before public void setUp() throws Exception
    {
        instance = EscapeTool.getInstance();
    }

    @Test public void testEscape() throws Exception
    {
        Map<String, String> data = new HashMap<String, String>();

        data.put("simple", "simple");
        data.put("Iv\ufffdn", "Iv\\ufffdn");
        data.put("1\t2\n3\f", "1\\t2\\n3\\f");
        data.put("Iv\u0017n", "Iv\\u0017n");
        for (String from : data.keySet())
        {
            assertEquals(data.get(from), instance.escape(from));
        }
    }

    @Test public void testSingleton() throws Exception
    {
        assertEquals(EscapeTool.class, EscapeTool.getInstance().getClass());
    }

    @SuppressWarnings("empty-statement")
    @Test public void testUnescape() throws Exception
    {
        Map<String, String> data = new HashMap<String, String>();

        data.put("simple", "simple");
        data.put("Iv\\ufffdn", "Iv\ufffdn");
        data.put("1\\t2\\n3\\f", "1\t2\n3\f");
        data.put("\\=", "=");
        for (String from : data.keySet())
        {
            assertEquals(data.get(from), instance.unescape(from));
        }

        // invalid unicode escape mean IllegalArgumentException
        try
        {
            instance.unescape("\\u98x");
            fail();
        }
        catch (IllegalArgumentException x)
        {
            ;
        }
    }
}
