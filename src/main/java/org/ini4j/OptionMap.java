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

public class OptionMap extends MultiMapImpl<String, String>
{
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

    public void from(Object bean)
    {
        BeanTool.getInstance().inject(this, bean);
    }

    public void to(Object bean)
    {
        BeanTool.getInstance().inject(bean, this);
    }

    protected String fetch(Object key)
    {
        return fetch(key, 0);
    }

    protected String fetch(Object key, int index)
    {
        return get(key, index);
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
