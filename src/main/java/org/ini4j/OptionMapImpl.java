/**
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

import org.ini4j.spi.BeanAccess;
import org.ini4j.spi.BeanTool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OptionMapImpl extends MultiMapImpl<String, String> implements OptionMap
{
    private static final char SUBST_CHAR = '$';
    private static final String SYSTEM_PROPERTY_PREFIX = "@prop/";
    private static final String ENVIRONMENT_PREFIX = "@env/";
    private static final int SYSTEM_PROPERTY_PREFIX_LEN = SYSTEM_PROPERTY_PREFIX.length();
    private static final int ENVIRONMENT_PREFIX_LEN = ENVIRONMENT_PREFIX.length();
    private static final Pattern EXPRESSION = Pattern.compile("(?<!\\\\)\\$\\{(([^\\[]+)(\\[([0-9]+)\\])?)\\}");
    private static final int G_OPTION = 2;
    private static final int G_INDEX = 4;
    private BeanAccess _defaultBeanAccess;

    @Override public <T> T as(Class<T> clazz)
    {
        return BeanTool.getInstance().proxy(clazz, getDefaultBeanAccess());
    }

    @Override public <T> T as(Class<T> clazz, String keyPrefix)
    {
        return BeanTool.getInstance().proxy(clazz, newBeanAccess(keyPrefix));
    }

    @Override public String fetch(Object key)
    {
        int len = length(key);

        return (len == 0) ? null : fetch(key, len - 1);
    }

    @Override public String fetch(Object key, int index)
    {
        String value = get(key, index);

        if ((value != null) && (value.indexOf(SUBST_CHAR) >= 0))
        {
            StringBuilder buffer = new StringBuilder(value);

            resolve(buffer);
            value = buffer.toString();
        }

        return value;
    }

    @Override public void from(Object bean)
    {
        BeanTool.getInstance().inject(getDefaultBeanAccess(), bean);
    }

    @Override public void from(Object bean, String keyPrefix)
    {
        BeanTool.getInstance().inject(newBeanAccess(keyPrefix), bean);
    }

    @Override public void to(Object bean)
    {
        BeanTool.getInstance().inject(bean, getDefaultBeanAccess());
    }

    @Override public void to(Object bean, String keyPrefix)
    {
        BeanTool.getInstance().inject(bean, newBeanAccess(keyPrefix));
    }

    protected synchronized BeanAccess getDefaultBeanAccess()
    {
        if (_defaultBeanAccess == null)
        {
            _defaultBeanAccess = newBeanAccess();
        }

        return _defaultBeanAccess;
    }

    protected BeanAccess newBeanAccess()
    {
        return new Access();
    }

    protected BeanAccess newBeanAccess(String propertyNamePrefix)
    {
        return new Access(propertyNamePrefix);
    }

    protected void resolve(StringBuilder buffer)
    {
        Matcher m = EXPRESSION.matcher(buffer);

        while (m.find())
        {
            String name = m.group(G_OPTION);
            int index = (m.group(G_INDEX) == null) ? -1 : Integer.parseInt(m.group(G_INDEX));
            String value;

            if (name.startsWith(ENVIRONMENT_PREFIX))
            {
                value = System.getenv(name.substring(ENVIRONMENT_PREFIX_LEN));
            }
            else if (name.startsWith(SYSTEM_PROPERTY_PREFIX))
            {
                value = System.getProperty(name.substring(SYSTEM_PROPERTY_PREFIX_LEN));
            }
            else
            {
                value = (index == -1) ? fetch(name) : fetch(name, index);
            }

            if (value != null)
            {
                buffer.replace(m.start(), m.end(), value);
                m.reset(buffer);
            }
        }
    }

    protected class Access implements BeanAccess
    {
        private final String _prefix;

        protected Access()
        {
            this(null);
        }

        protected Access(String prefix)
        {
            _prefix = prefix;
        }

        public void propAdd(String propertyName, String value)
        {
            add(transform(propertyName), value);
        }

        public String propDel(String propertyName)
        {
            return remove(transform(propertyName));
        }

        public String propGet(String propertyName)
        {
            return fetch(transform(propertyName));
        }

        public String propGet(String propertyName, int index)
        {
            return fetch(transform(propertyName), index);
        }

        public int propLength(String propertyName)
        {
            return length(transform(propertyName));
        }

        public String propSet(String propertyName, String value)
        {
            return put(transform(propertyName), value);
        }

        public String propSet(String propertyName, String value, int index)
        {
            return put(transform(propertyName), value, index);
        }

        private String transform(String orig)
        {
            return (orig == null) ? null : ((_prefix == null) ? orig : (_prefix + orig));
        }
    }
}
