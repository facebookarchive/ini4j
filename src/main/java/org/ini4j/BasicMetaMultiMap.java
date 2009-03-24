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

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class BasicMetaMultiMap<K, V> extends BasicMultiMap<K, V>
{
    private static final long serialVersionUID = 3012579878005541746L;
    private static final String SEPARATOR = ";#;";
    private static final String FIRST_CATEGORY = "";
    private static final String LAST_CATEGORY = "zzzzzz";
    private NavigableMap<String, String> _meta;

    @Override public void clear()
    {
        super.clear();
        if (_meta != null)
        {
            _meta.clear();
        }
    }

    @SuppressWarnings("unchecked")
    @Override public void putAll(Map<? extends K, ? extends V> map)
    {
        super.putAll(map);
        if (map instanceof BasicMetaMultiMap)
        {
            Map<String, String> meta = ((BasicMetaMultiMap) map)._meta;

            if (meta != null)
            {
                meta().putAll(meta);
            }
        }
    }

    @Override public V remove(Object key)
    {
        V ret = super.remove(key);

        removeMeta(key);

        return ret;
    }

    @Override public V remove(Object key, int index)
    {
        V ret = super.remove(key, index);

        if (length(key) == 0)
        {
            removeMeta(key);
        }

        return ret;
    }

    protected String getMeta(String category, Object key)
    {
        return (_meta == null) ? null : _meta.get(makeKey(category, key));
    }

    protected String putMeta(String category, K key, String value)
    {
        return meta().put(makeKey(category, key), value);
    }

    protected void removeMeta(Object key)
    {
        if (_meta != null)
        {
            _meta.subMap(makeKey(FIRST_CATEGORY, key), true, makeKey(LAST_CATEGORY, key), true).clear();
        }
    }

    protected String removeMeta(String category, Object key)
    {
        return (_meta == null) ? null : _meta.remove(makeKey(category, key));
    }

    private String makeKey(String category, Object key)
    {
        StringBuilder buff = new StringBuilder();

        buff.append(String.valueOf(key));
        buff.append(SEPARATOR);
        buff.append(category);

        return buff.toString();
    }

    private Map<String, String> meta()
    {
        if (_meta == null)
        {
            _meta = new TreeMap<String, String>();
        }

        return _meta;
    }
}
