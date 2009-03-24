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

public class BasicCommentedMultiMap<K, V> extends BasicMetaMultiMap<K, V> implements CommentedMap<K, V>
{
    private static final long serialVersionUID = -3191166132698733784L;
    private static final String META_COMMENT = "comment";

    @Override public String getComment(Object key)
    {
        return getMeta(META_COMMENT, key);
    }

    @Override public String putComment(K key, String comment)
    {
        return putMeta(META_COMMENT, key, comment);
    }

    @Override public String removeComment(Object key)
    {
        return removeMeta(META_COMMENT, key);
    }
}
