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

import java.lang.reflect.Array;
import java.lang.reflect.Proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OptionMapImpl extends MultiMapImpl<String, String> implements OptionMap
{
    private static final char SUBST_CHAR = '$';
    private static final String SYSTEM_PROPERTY_PREFIX = "@prop/";
    private static final String ENVIRONMENT_PREFIX = "@env/";
    private static final int SYSTEM_PROPERTY_PREFIX_LEN = SYSTEM_PROPERTY_PREFIX.length();
    private static final int ENVIRONMENT_PREFIX_LEN = ENVIRONMENT_PREFIX.length();
    private static final Pattern expr = Pattern.compile("(?<!\\\\)\\$\\{(([^\\[]+)(\\[([0-9]+)\\])?)\\}");
    private static final int G_OPTION = 2;
    private static final int G_INDEX = 4;
    private Map<Class, Object> _beans;

    public synchronized <T> T as(Class<T> clazz)
    {
        Object bean;

        if (_beans == null)
        {
            _beans = new HashMap<Class, Object>();
            bean = null;
        }
        else
        {
            bean = _beans.get(clazz);
        }

        if (bean == null)
        {
            bean = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { clazz }, new BeanInvocationHandler());
            _beans.put(clazz, bean);
        }

        return clazz.cast(bean);
    }

    public String fetch(Object key)
    {
        return fetch(key, 0);
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

    public void from(Object bean)
    {
        BeanTool.getInstance().inject(this, bean);
    }

    public void to(Object bean)
    {
        BeanTool.getInstance().inject(bean, this);
    }

    protected void resolve(StringBuilder buffer)
    {
        Matcher m = expr.matcher(buffer);

        while (m.find())
        {
            String name = m.group(G_OPTION);
            int index = (m.group(G_INDEX) == null) ? 0 : Integer.parseInt(m.group(G_INDEX));
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
                value = fetch(name, index);
            }

            if (value != null)
            {
                buffer.replace(m.start(), m.end(), value);
                m.reset(buffer);
            }
        }
    }

    class BeanInvocationHandler extends AbstractBeanInvocationHandler
    {
        @Override protected Object getPropertySpi(String property, Class<?> clazz)
        {
            Object ret;

            if (clazz.isArray())
            {
                String[] all = containsKey(property) ? new String[length(property)] : null;

                if (all != null)
                {
                    for (int i = 0; i < all.length; i++)
                    {
                        all[i] = fetch(property, i);
                    }
                }

                ret = all;
            }
            else
            {
                ret = fetch(property);
            }

            return ret;
        }

        @Override protected void setPropertySpi(String property, Object value, Class<?> clazz)
        {
            if (clazz.isArray())
            {
                remove(property);
                for (int i = 0; i < Array.getLength(value); i++)
                {
                    add(property, Array.get(value, i).toString());
                }
            }
            else
            {
                put(property, value.toString());
            }
        }

        @Override protected boolean hasPropertySpi(String property)
        {
            return containsKey(property);
        }
    }
}
