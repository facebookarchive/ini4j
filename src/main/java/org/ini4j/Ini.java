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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import java.lang.reflect.Array;
import java.lang.reflect.Proxy;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ini extends MultiMapImpl<String, Ini.Section>
{
    private static final char SUBST_CHAR = '$';
    private static final String SECTION_SYSTEM_PROPERTIES = "@prop";
    private static final String SECTION_ENVIRONMENT = "@env";
    private static final Pattern expr = Pattern.compile("(?<!\\\\)\\$\\{(([^\\[]+)(\\[([0-9]+)\\])?/)?([^\\[]+)(\\[(([0-9]+))\\])?\\}");
    private static final int G_SECTION = 2;
    private static final int G_SECTION_IDX = 4;
    private static final int G_OPTION = 5;
    private static final int G_OPTION_IDX = 7;
    private Map<Class, Object> _beans;
    private Config _config = Config.getGlobal();

    @SuppressWarnings("empty-statement")
    public Ini()
    {
        ;
    }

    public Ini(Reader input) throws IOException, InvalidIniFormatException
    {
        this();
        load(input);
    }

    public Ini(InputStream input) throws IOException, InvalidIniFormatException
    {
        this();
        load(input);
    }

    public Ini(URL input) throws IOException, InvalidIniFormatException
    {
        this();
        load(input);
    }

    public void setConfig(Config value)
    {
        _config = value;
    }

    public Section add(String name)
    {
        Section s = new Section(name);

        if (getConfig().isMultiSection())
        {
            add(name, s);
        }
        else
        {
            put(name, s);
        }

        return s;
    }

    public <T> T as(Class<T> clazz)
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
            bean = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { clazz }, new BeanInvocationHandler());
            _beans.put(clazz, bean);
        }

        return clazz.cast(bean);
    }

    public void load(InputStream input) throws IOException, InvalidIniFormatException
    {
        load(new InputStreamReader(input));
    }

    public void load(Reader input) throws IOException, InvalidIniFormatException
    {
        Builder builder = new Builder();

        IniParser.newInstance(getConfig()).parse(input, builder);
    }

    public void load(URL input) throws IOException, InvalidIniFormatException
    {
        Builder builder = new Builder();

        IniParser.newInstance(getConfig()).parse(input, builder);
    }

    public void loadFromXML(InputStream input) throws IOException, InvalidIniFormatException
    {
        loadFromXML(new InputStreamReader(input));
    }

    public void loadFromXML(Reader input) throws IOException, InvalidIniFormatException
    {
        Builder builder = new Builder();

        IniParser.newInstance(getConfig()).parseXML(input, builder);
    }

    public void loadFromXML(URL input) throws IOException, InvalidIniFormatException
    {
        Builder builder = new Builder();

        IniParser.newInstance(getConfig()).parseXML(input, builder);
    }

    public Section remove(Section section)
    {
        return remove((Object) section.getName());
    }

    public void store(OutputStream output) throws IOException
    {
        store(IniFormatter.newInstance(output, getConfig()));
    }

    public void store(Writer output) throws IOException
    {
        store(IniFormatter.newInstance(output, getConfig()));
    }

    public void storeToXML(OutputStream output) throws IOException
    {
        store(XMLFormatter.newInstance(output));
    }

    public void storeToXML(Writer output) throws IOException
    {
        store(XMLFormatter.newInstance(output));
    }

    @Deprecated public <T> T to(Class<T> clazz)
    {
        return as(clazz);
    }

    protected Config getConfig()
    {
        return _config;
    }

    protected void resolve(StringBuilder buffer, Section owner)
    {
        Matcher m = expr.matcher(buffer);

        while (m.find())
        {
            if (m.groupCount() < G_OPTION_IDX)
            {
                continue;
            }

            String sectionName = m.group(G_SECTION);
            String optionName = m.group(G_OPTION);
            int sectionIndex = (m.group(G_SECTION_IDX) == null) ? 0 : Integer.parseInt(m.group(G_SECTION_IDX));
            int optionIndex = (m.group(G_OPTION_IDX) == null) ? 0 : Integer.parseInt(m.group(G_OPTION_IDX));
            Section section = (sectionName == null) ? owner : get(sectionName, sectionIndex);
            String value;

            if (SECTION_ENVIRONMENT.equals(sectionName))
            {
                value = System.getenv(optionName);
            }
            else if (SECTION_SYSTEM_PROPERTIES.equals(sectionName))
            {
                value = System.getProperty(optionName);
            }
            else
            {
                value = (section == null) ? null : section.fetch(optionName, optionIndex);
            }

            if (value != null)
            {
                buffer.replace(m.start(), m.end(), value);
                m.reset(buffer);
            }
        }
    }

    protected void store(IniHandler formatter) throws IOException
    {
        formatter.startIni();
        for (Ini.Section s : values())
        {
            formatter.startSection(s.getName());
            for (String name : s.keySet())
            {
                int n = getConfig().isMultiOption() ? s.length(name) : 1;

                for (int i = 0; i < n; i++)
                {
                    formatter.handleOption(name, s.get(name, i));
                }
            }

            formatter.endSection();
        }

        formatter.endIni();
    }

    public class Section extends MultiMapImpl<String, String>
    {
        private Map<Class, Object> _beans;
        private String _name;

        public Section(String name)
        {
            super();
            _name = name;
        }

        public String getName()
        {
            return _name;
        }

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
                bean = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { clazz }, new BeanInvocationHandler());
                _beans.put(clazz, bean);
            }

            return clazz.cast(bean);
        }

        public String fetch(Object key)
        {
            return fetch(key, 0);
        }

        public String fetch(Object key, int index)
        {
            String value = get(key, index);

            if ((value != null) && (value.indexOf(SUBST_CHAR) >= 0))
            {
                StringBuilder buffer = new StringBuilder(value);

                resolve(buffer, this);
                value = buffer.toString();
            }

            return value;
        }

        public void from(Object bean)
        {
            BeanTool.getInstance().inject(this, bean);
        }

        @Deprecated public <T> T to(Class<T> clazz)
        {
            return as(clazz);
        }

        public void to(Object bean)
        {
            BeanTool.getInstance().inject(bean, this);
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

    class BeanInvocationHandler extends AbstractBeanInvocationHandler
    {
        private MultiMap<String, Object> _sectionBeans = new MultiMapImpl<String, Object>();

        @Override protected Object getPropertySpi(String property, Class<?> clazz)
        {
            Object o;

            if (clazz.isArray())
            {
                if (!_sectionBeans.containsKey(property))
                {
                    if (containsKey(property))
                    {
                        for (int i = 0; i < length(property); i++)
                        {
                            _sectionBeans.add(property, get(property, i).as(clazz.getComponentType()));
                        }
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
                else
                {
                    o = null;
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

    class Builder implements IniHandler
    {
        private Section currentSection;

        @SuppressWarnings("empty-statement")
        @Override public void endIni()
        {
            ;
        }

        @Override public void endSection()
        {
            currentSection = null;
        }

        @Override public void handleOption(String name, String value)
        {
            if (getConfig().isMultiOption())
            {
                currentSection.add(name, value);
            }
            else
            {
                currentSection.put(name, value);
            }
        }

        @SuppressWarnings("empty-statement")
        @Override public void startIni()
        {
            ;
        }

        @Override public void startSection(String sectionName)
        {
            if (getConfig().isMultiSection())
            {
                currentSection = add(sectionName);
            }
            else
            {
                Section s = get(sectionName);

                currentSection = (s != null) ? s : add(sectionName);
            }
        }
    }
}
