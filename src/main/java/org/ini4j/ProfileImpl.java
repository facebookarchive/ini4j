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

import org.ini4j.spi.AbstractBeanInvocationHandler;
import org.ini4j.spi.BeanTool;

import java.lang.reflect.Array;
import java.lang.reflect.Proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileImpl extends CommentMultiMapImpl<String, Profile.Section> implements Profile<Profile.Section>
{
    private static final String SECTION_SYSTEM_PROPERTIES = "@prop";
    private static final String SECTION_ENVIRONMENT = "@env";
    private static final Pattern EXPRESSION = Pattern.compile("(?<!\\\\)\\$\\{(([^\\[]+)(\\[([0-9]+)\\])?/)?([^\\[]+)(\\[(([0-9]+))\\])?\\}");
    private static final int G_SECTION = 2;
    private static final int G_SECTION_IDX = 4;
    private static final int G_OPTION = 5;
    private static final int G_OPTION_IDX = 7;
    private Map<Class, Object> _beans;

    @Override public Section add(String name)
    {
        Section s = new SectionImpl(name);

        add(name, s);

        return s;
    }

    @Override public void add(String section, String option, Object value)
    {
        getOrAdd(section).add(option, value);
    }

    @Override public <T> T as(Class<T> clazz)
    {
        return clazz.cast(Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { clazz }, new BeanInvocationHandler()));
    }

    @Override public String fetch(Object sectionName, Object optionName)
    {
        Section sec = get(sectionName);

        return (sec == null) ? null : sec.fetch(optionName);
    }

    @Override public <T> T fetch(Object sectionName, Object optionName, Class<T> clazz)
    {
        Section sec = get(sectionName);

        return (sec == null) ? BeanTool.getInstance().zero(clazz) : sec.fetch(optionName, clazz);
    }

    @Override public String get(Object sectionName, Object optionName)
    {
        Section sec = get(sectionName);

        return (sec == null) ? null : sec.get(optionName);
    }

    @Override public <T> T get(Object sectionName, Object optionName, Class<T> clazz)
    {
        Section sec = get(sectionName);

        return (sec == null) ? BeanTool.getInstance().zero(clazz) : sec.get(optionName, clazz);
    }

    @Override public String put(String sectionName, String optionName, Object value)
    {
        return getOrAdd(sectionName).put(optionName, value);
    }

    @Override public Section remove(Section section)
    {
        return remove((Object) section.getName());
    }

    @Override public String remove(Object sectionName, Object optionName)
    {
        Section sec = get(sectionName);

        return (sec == null) ? null : sec.remove(optionName);
    }

    @Override @Deprecated public synchronized <T> T to(Class<T> clazz)
    {
        Object bean = null;

        if (_beans == null)
        {
            _beans = new HashMap<Class, Object>();
        }
        else
        {
            bean = _beans.get(clazz);
        }

        if (bean == null)
        {
            bean = as(clazz);
            _beans.put(clazz, bean);
        }

        return clazz.cast(bean);
    }

    protected void resolve(StringBuilder buffer, Section owner)
    {
        Matcher m = EXPRESSION.matcher(buffer);

        while (m.find())
        {
            String sectionName = m.group(G_SECTION);
            String optionName = m.group(G_OPTION);
            int optionIndex = parseOptionIndex(m);
            Section section = parseSection(m, owner);
            String value = null;

            if (SECTION_ENVIRONMENT.equals(sectionName))
            {
                value = System.getenv(optionName);
            }
            else if (SECTION_SYSTEM_PROPERTIES.equals(sectionName))
            {
                value = System.getProperty(optionName);
            }
            else if (section != null)
            {
                value = (optionIndex == -1) ? section.fetch(optionName) : section.fetch(optionName, optionIndex);
            }

            if (value != null)
            {
                buffer.replace(m.start(), m.end(), value);
                m.reset(buffer);
            }
        }
    }

    private Section getOrAdd(String sectionName)
    {
        Section section = get(sectionName);

        return ((section == null)) ? add(sectionName) : section;
    }

    private int parseOptionIndex(Matcher m)
    {
        return (m.group(G_OPTION_IDX) == null) ? -1 : Integer.parseInt(m.group(G_OPTION_IDX));
    }

    private Section parseSection(Matcher m, Section owner)
    {
        String sectionName = m.group(G_SECTION);
        int sectionIndex = parseSectionIndex(m);

        return (sectionName == null) ? owner : ((sectionIndex == -1) ? get(sectionName) : get(sectionName, sectionIndex));
    }

    private int parseSectionIndex(Matcher m)
    {
        return (m.group(G_SECTION_IDX) == null) ? -1 : Integer.parseInt(m.group(G_SECTION_IDX));
    }

    protected class SectionImpl extends OptionMapImpl implements Section
    {
        private Map<Class, Object> _beans;
        private final String _name;

        protected SectionImpl(String name)
        {
            super();
            _name = name;
        }

        @Override public String getName()
        {
            return _name;
        }

        @Deprecated @Override public synchronized <T> T to(Class<T> clazz)
        {
            Object bean = null;

            if (_beans == null)
            {
                _beans = new HashMap<Class, Object>();
            }
            else
            {
                bean = _beans.get(clazz);
            }

            if (bean == null)
            {
                bean = as(clazz);
                _beans.put(clazz, bean);
            }

            return clazz.cast(bean);
        }

        @Override protected void resolve(StringBuilder buffer)
        {
            ProfileImpl.this.resolve(buffer, this);
        }
    }

    private class BeanInvocationHandler extends AbstractBeanInvocationHandler
    {
        private final MultiMap<String, Object> _sectionBeans = new MultiMapImpl<String, Object>();

        @Override protected Object getPropertySpi(String property, Class<?> clazz)
        {
            Object o = null;

            if (clazz.isArray())
            {
                if (!_sectionBeans.containsKey(property) && containsKey(property))
                {
                    for (int i = 0; i < length(property); i++)
                    {
                        _sectionBeans.add(property, get(property, i).as(clazz.getComponentType()));
                    }
                }

                if (_sectionBeans.containsKey(property))
                {
                    o = Array.newInstance(clazz.getComponentType(), _sectionBeans.length(property));
                    for (int i = 0; i < _sectionBeans.length(property); i++)
                    {
                        Array.set(o, i, _sectionBeans.get(property, i));
                    }
                }
            }
            else
            {
                o = _sectionBeans.get(property);
                if (o == null)
                {
                    Section section = get(property);

                    if (section != null)
                    {
                        o = section.as(clazz);
                        _sectionBeans.put(property, o);
                    }
                }
            }

            return o;
        }

        @Override protected void setPropertySpi(String property, Object value, Class<?> clazz)
        {
            remove(property);
            if (value != null)
            {
                if (clazz.isArray())
                {
                    for (int i = 0; i < Array.getLength(value); i++)
                    {
                        Section sec = add(property);

                        sec.from(Array.get(value, i));
                    }
                }
                else
                {
                    Section sec = add(property);

                    sec.from(value);
                }
            }
        }

        @Override protected boolean hasPropertySpi(String property)
        {
            return containsKey(property);
        }
    }
}
