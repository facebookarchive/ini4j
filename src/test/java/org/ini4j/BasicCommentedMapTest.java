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

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class BasicCommentedMapTest
{
    private static final String KEY1 = "key1";
    private static final String KEY2 = "key2";
    private static final String KEY3 = "key3";
    private static final String VALUE1 = "value1";
    private static final String VALUE2 = "value2";
    private static final String VALUE3 = "value3";
    private static final String KEY = "key";
    private static final String VALUE = "value";
    private static final String COMMENT = "comment";
    private static final String[] VALUES = { VALUE1, VALUE2, VALUE3 };
    private CommentedMap<String, String> _map;

    @Before public void setUp()
    {
        _map = new BasicCommentedMap<String, String>();
    }

    @Test public void testClearAndRemove() throws Exception
    {
        BasicCommentedMap<String, String> map = new BasicCommentedMap<String, String>();

        assertNull(map.removeComment(KEY));

        //
        map.put(KEY, VALUE);
        map.clear();
        assertTrue(map.isEmpty());

        //
        map.put(KEY, VALUE);
        map.remove(KEY);
        assertNull(map.getComment(KEY));

        //
        map.put(KEY, VALUE);
        map.remove(KEY);
        assertNull(map.getComment(KEY));

        //
        map.put(KEY, VALUE);
        map.putComment(KEY, COMMENT);
        assertEquals(COMMENT, map.getComment(KEY));
        map.clear();
        assertNull(map.getComment(KEY));

        //
        map.put(KEY, VALUE);
        map.putComment(KEY, COMMENT);
        map.remove(KEY);
        assertNull(map.getComment(KEY));

        //
        map.put(KEY, VALUE);
        map.putComment(KEY, COMMENT);
        assertEquals(COMMENT, map.removeComment(KEY));
        assertNull(map.getComment(KEY));

        //
        map.put(KEY, VALUE);
        map.putComment(KEY, COMMENT);
        map.remove(KEY);
        assertNull(map.getComment(KEY));
    }

    @Test public void testContainsValue()
    {
        _map.put(KEY3, VALUE3);
        assertTrue(_map.containsValue(VALUE3));
        _map.clear();
        _map.put(KEY2, VALUE1);
        assertFalse(_map.containsValue(VALUE3));
    }

    @Test public void testEntrySet()
    {
        _map.put(KEY1, VALUE1);
        _map.put(KEY2, VALUE2);
        _map.put(KEY3, VALUE3);
        Set<Entry<String, String>> set = _map.entrySet();

        assertNotNull(set);
        assertEquals(3, set.size());
        for (Entry<String, String> e : set)
        {
            if (e.getKey().equals(KEY1))
            {
                assertEquals(VALUE1, e.getValue());
                e.setValue(VALUE1);
            }
            else if (e.getKey().equals(KEY2))
            {
                assertEquals(VALUE2, e.getValue());
                e.setValue(VALUE3);
            }
            else if (e.getKey().equals(KEY3))
            {
                assertEquals(VALUE3, e.getValue());
                e.setValue(VALUE2);
            }
        }

        assertEquals(VALUE1, _map.get(KEY1));
        assertEquals(VALUE3, _map.get(KEY2));
        assertEquals(VALUE2, _map.get(KEY3));
    }

    @Test public void testGetEmpty()
    {
        assertNull(_map.get(KEY1));
    }

    @Test public void testPutAll()
    {
        _map.put(KEY1, VALUE1);
        _map.put(KEY2, VALUE1);
        Map<String, String> other = new BasicCommentedMap<String, String>();

        other.putAll(_map);
        assertEquals(2, other.size());
        assertEquals(VALUE1, _map.get(KEY1));
        assertEquals(VALUE1, _map.get(KEY2));
        Map<String, String> regular = new HashMap<String, String>(_map);

        _map.clear();
        _map.putAll(regular);
        assertEquals(regular.keySet(), _map.keySet());
    }

    @Test public void testPutAllCommant() throws Exception
    {
        BasicCommentedMap<String, String> map = new BasicCommentedMap<String, String>();
        BasicCommentedMap<String, String> copy = new BasicCommentedMap<String, String>();

        map.put(KEY, VALUE);
        map.putComment(KEY, COMMENT);
        copy.putAll(map);
        assertEquals(COMMENT, copy.getComment(KEY));
        Map<String, String> simple = new HashMap<String, String>();

        simple.put(KEY, VALUE);
        copy.clear();
        assertTrue(copy.isEmpty());
        copy.putAll(simple);
        assertNull(copy.getComment(KEY));
        assertEquals(VALUE, copy.get(KEY));

        //
        map = new BasicCommentedMap<String, String>();
        map.put(KEY, VALUE);
        copy.clear();
        copy.putAll(map);
        assertEquals(VALUE, copy.get(KEY));
        assertNull(copy.getComment(KEY));
    }

    @Test public void testRemove()
    {
        _map.put(KEY1, VALUE1);
        _map.put(KEY2, VALUE2);
        _map.put(KEY3, VALUE3);
        assertEquals(VALUE1, _map.get(KEY1));
        _map.remove(KEY1);
        assertFalse(_map.containsKey(KEY1));
        assertEquals(VALUE2, _map.get(KEY2));
        _map.remove(KEY2);
        assertFalse(_map.containsKey(KEY2));
        assertEquals(VALUE3, _map.get(KEY3));
        _map.remove(KEY3);
        assertFalse(_map.containsKey(KEY3));
        assertEquals(0, _map.size());
        assertTrue(_map.isEmpty());
    }

    @Test public void testValues()
    {
        _map.put(KEY1, VALUE1);
        _map.put(KEY2, VALUE2);
        _map.put(KEY3, VALUE3);
        String[] values = _map.values().toArray(new String[] {});

        Arrays.sort(values);
        assertArrayEquals(values, VALUES);
    }
}
