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

import org.junit.Ignore;
import org.junit.Test;

@Ignore public class CommentMultiMapTest
{
    private static final String KEY = "key";
    private static final String VALUE = "value";
    private static final String COMMENT = "comment";

    @Test public void testClearAndRemove() throws Exception
    {
        CommentMultiMap<String, String> map = new BasicCommentMultiMap<String, String>();

        assertNull(map.removeComment(KEY));

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
        map.remove(KEY, 0);
        assertNull(map.getComment(KEY));
    }
}
