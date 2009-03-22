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

import java.io.Serializable;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class BasicCommentedMap<K, V> implements CommentedMap<K, V>, Serializable
{
    private static final long serialVersionUID = 6461782712691165293L;
    private Map<K, String> _comments;
    private final Map<K, V> _impl;

    public BasicCommentedMap()
    {
        this(new LinkedHashMap<K, V>());
    }

    public BasicCommentedMap(Map<K, V> impl)
    {
        _impl = impl;
    }

    @Override public String getComment(Object key)
    {
        return (_comments == null) ? null : _comments.get(key);
    }

    @Override public boolean isEmpty()
    {
        return _impl.isEmpty();
    }

    @Override public void clear()
    {
        _impl.clear();
        if (_comments != null)
        {
            _comments.clear();
        }
    }

    @Override public boolean containsKey(Object key)
    {
        return _impl.containsKey(key);
    }

    @Override public boolean containsValue(Object value)
    {
        return _impl.containsValue(value);
    }

    @Override public Set<Entry<K, V>> entrySet()
    {
        return _impl.entrySet();
    }

    @Override public V get(Object key)
    {
        return _impl.get(key);
    }

    @Override public Set<K> keySet()
    {
        return _impl.keySet();
    }

    @Override public V put(K key, V value)
    {
        return _impl.put(key, value);
    }

    @SuppressWarnings("unchecked")
    @Override public void putAll(Map<? extends K, ? extends V> map)
    {
        _impl.putAll(map);
        if (map instanceof BasicCommentedMap)
        {
            Map<K, String> cms = ((BasicCommentedMap) map)._comments;

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
        V ret = _impl.remove(key);

        if (_comments != null)
        {
            _comments.remove(key);
        }

        return ret;
    }

    @Override public String removeComment(Object key)
    {
        return (_comments == null) ? null : _comments.remove(key);
    }

    @Override public int size()
    {
        return _impl.size();
    }

    @Override public Collection<V> values()
    {
        return _impl.values();
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
