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

import org.ini4j.spi.AbstractBeanInvocationHandler;
import org.ini4j.spi.IniFormatter;
import org.ini4j.spi.XMLFormatter;

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
    private static final String SECTION_SYSTEM_PROPERTIES = "@prop";
    private static final String SECTION_ENVIRONMENT = "@env";
    private static final Pattern EXPRESSION = Pattern.compile("(?<!\\\\)\\$\\{(([^\\[]+)(\\[([0-9]+)\\])?/)?([^\\[]+)(\\[(([0-9]+))\\])?\\}");
    private static final int G_SECTION = 2;
    private static final int G_SECTION_IDX = 4;
    private static final int G_OPTION = 5;
    private static final int G_OPTION_IDX = 7;
    private Map<Class, Object> _beans;
    private Config _config = Config.getGlobal();

    public Ini()
    {
        assert true;
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
        return clazz.cast(Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { clazz }, new BeanInvocationHandler()));
    }

    public void load(InputStream input) throws IOException, InvalidIniFormatException
    {
        Builder builder = new Builder();

        IniParser.newInstance(getConfig()).parse(input, builder);
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

    @Deprecated public synchronized <T> T to(Class<T> clazz)
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

    protected Config getConfig()
    {
        return _config;
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

    public class Section extends OptionMapImpl
    {
        private Map<Class, Object> _beans;
        private final String _name;

        public Section(String name)
        {
            super();
            _name = name;
        }

        public String getName()
        {
            return _name;
        }

        @Deprecated public synchronized <T> T to(Class<T> clazz)
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
            Ini.this.resolve(buffer, this);
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

    private class Builder implements IniHandler
    {
        private Section _currentSection;

        public void endIni()
        {
            assert true;
        }

        @Override public void endSection()
        {
            _currentSection = null;
        }

        @Override public void handleOption(String name, String value)
        {
            if (getConfig().isMultiOption())
            {
                _currentSection.add(name, value);
            }
            else
            {
                _currentSection.put(name, value);
            }
        }

        public void startIni()
        {
            assert true;
        }

        @Override public void startSection(String sectionName)
        {
            if (getConfig().isMultiSection())
            {
                _currentSection = add(sectionName);
            }
            else
            {
                Section s = get(sectionName);

                _currentSection = (s == null) ? add(sectionName) : s;
            }
        }
    }
}
