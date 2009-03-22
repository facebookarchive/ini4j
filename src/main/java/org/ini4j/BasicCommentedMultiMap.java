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

import java.util.HashMap;
import java.util.Map;

public class BasicCommentedMultiMap<K, V> extends BasicMultiMap<K, V> implements CommentedMap<K, V>
{
    private static final long serialVersionUID = -3191166132698733784L;
    private Map<K, String> _comments;

    @Override public String getComment(Object key)
    {
        return (_comments == null) ? null : _comments.get(key);
    }

    @Override public void clear()
    {
        super.clear();
        if (_comments != null)
        {
            _comments.clear();
        }
    }

    @SuppressWarnings("unchecked")
    @Override public void putAll(Map<? extends K, ? extends V> map)
    {
        super.putAll(map);
        if (map instanceof BasicCommentedMultiMap)
        {
            Map<K, String> cms = ((BasicCommentedMultiMap) map)._comments;

            if (cms != null)
            {
                comments().putAll(cms);
            }
        }
    }

    @Override public String putComment(K key, String comment)
    {
        return comments().put(key, comment);
    }

    @Override public V remove(Object key)
    {
        V ret = super.remove(key);

        if (_comments != null)
        {
            _comments.remove(key);
        }

        return ret;
    }

    @Override public V remove(Object key, int index)
    {
        V ret = super.remove(key, index);

        if ((length(key) == 0) && (_comments != null))
        {
            _comments.remove(key);
        }

        return ret;
    }

    @Override public String removeComment(Object key)
    {
        return (_comments == null) ? null : _comments.remove(key);
    }

    private Map<K, String> comments()
    {
        if (_comments == null)
        {
            _comments = new HashMap<K, String>();
        }

        return _comments;
    }
}
