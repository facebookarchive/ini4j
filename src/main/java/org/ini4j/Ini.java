/*
 * Copyright 2005 [ini4j] Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Ini extends LinkedHashMap<String,Ini.Section>
{
    private static final String OPERATOR = " " + IniParser.OPERATOR + " ";
    private static final char SUBST_CHAR = '$';
    private static final String SUBST_BEGIN = SUBST_CHAR + "{";
    private static final int SUBST_BEGIN_LEN = SUBST_BEGIN.length();
    private static final String SUBST_END = "}";
    private static final int SUBST_END_LEN = SUBST_END.length();
    private static final char SUBST_ESCAPE = '\\';
    private static final char SUBST_SEPARATOR = '/';
    private static final String SUBST_PROPERTY = "@prop";
    private static final String SUBST_ENVIRONMENT = "@env";
    
    private Map<Class,Object> _beans;
    
    public class Section extends LinkedHashMap<String,String>
    {
        private String _name;
        private Map<Class,Object> _beans;
        
        class BeanInvocationHandler extends AbstractBeanInvocationHandler
        {
            @Override
            protected Object getPropertySpi(String property, Class<?> clazz)
            {
                return fetch(property);
            }
            
            @Override
            protected void setPropertySpi(String property, Object value, Class<?> clazz)
            {
                put(property, value.toString());
            }
            
            @Override
            protected boolean hasPropertySpi(String property)
            {
                return containsKey(property);
            }
        }
        
        public Section(String name)
        {
            super();
            _name = name;
        }
        
        public String getName()
        {
            return _name;
        }
        
        public synchronized <T> T to(Class<T> clazz)
        {
            Object bean;
            
            if ( _beans == null )
            {
                _beans = new HashMap<Class,Object>();
                bean = null;
            }
            else
            {
                bean = _beans.get(clazz);
            }
            
            if ( bean == null )
            {
                bean = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {clazz}, new BeanInvocationHandler());
                _beans.put(clazz, bean);
            }
            
            return clazz.cast(bean);
        }
        
        public String fetch(Object key)
        {
            String value = get(key);
            
            if ( (value != null) && (value.indexOf(SUBST_CHAR) >= 0) )
            {
                StringBuilder buffer = new StringBuilder(value);
                resolve(buffer, this);
                value = buffer.toString();
            }
            return value;
        }
    }
    
    class BeanInvocationHandler extends AbstractBeanInvocationHandler
    {
        private Map<String,Object> _sectionBeans = new HashMap<String, Object>();
        
        @Override
        protected Object getPropertySpi(String property, Class<?> clazz)
        {
            Object o = _sectionBeans.get(property);
            
            if ( o == null )
            {
                Section section = get(property);
                
                if ( section != null )
                {
                    o = section.to(clazz);
                    _sectionBeans.put(property, o);
                }
            }
            
            return o;
        }
        
        @Override
        protected void setPropertySpi(String property, Object value, Class<?> clazz)
        {
            throw new UnsupportedOperationException("read only bean");
        }
        
        @Override
        protected boolean hasPropertySpi(String property)
        {
            return false;
        }
    }
    
    class Builder implements IniHandler
    {
        private Section currentSection;
        
        @SuppressWarnings("empty-statement")
        @Override
        public void startIni()
        {
            ;
        }
        
        @SuppressWarnings("empty-statement")
        @Override
        public void endIni()
        {
            ;
        }
        
        @Override
        public void startSection(String sectionName)
        {
            Section s = get(sectionName);
            currentSection = (s != null) ? s : add(sectionName);
        }
        
        @Override
        public void endSection()
        {
            currentSection = null;
        }
        
        @Override
        public void handleOption(String name, String value)
        {
            currentSection.put(name, value);
        }
    }
    
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
    
    public Section add(String name)
    {
        Section s = new Section(name);
        put(name, s);
        return s;
    }
    
    public Section remove(Section section)
    {
        return remove((Object)section.getName() );
    }
    
    public void store(OutputStream output) throws IOException
    {
        store(new OutputStreamWriter(output));
    }
    
    public void store(Writer output) throws IOException
    {
        PrintWriter pr = new PrintWriter(output);
        
        for(Ini.Section s : values())
        {
            pr.print(IniParser.SECTION_BEGIN);
            pr.print(Convert.escape(s.getName()));
            pr.println(IniParser.SECTION_END);
            
            for(Map.Entry<String,String> e : s.entrySet())
            {
                pr.print(Convert.escape(e.getKey()));
                pr.print(OPERATOR);
                pr.println(Convert.escape(e.getValue()));
            }
            
            pr.println();
        }
        pr.flush();
    }
    
    public void load(InputStream input) throws IOException, InvalidIniFormatException
    {
        load(new InputStreamReader(input));
    }
    
    public void load(Reader input) throws IOException, InvalidIniFormatException
    {
        Builder builder = new Builder();
        IniParser.newInstance().parse(input, builder);
    }
    
    public void load(URL input) throws IOException, InvalidIniFormatException
    {
	Builder builder = new Builder();
	IniParser.newInstance().parse(input, builder);
    }
    
    public void storeToXML(OutputStream output) throws IOException
    {
        storeToXML(new OutputStreamWriter(output));
    }
    
    public void storeToXML(Writer output) throws IOException
    {
        PrintWriter pr = new PrintWriter(output);
        
        pr.println("<ini version='1.0'>");
        
        for(Ini.Section s : values())
        {
            pr.print(" <section key='");
            pr.print(s.getName());
            pr.println("'>");
            
            for(Map.Entry<String,String> e : s.entrySet())
            {
                pr.print("  <option key='");
                pr.print(e.getKey());
                pr.print("' value='");
                pr.print(e.getValue());
                pr.println("'/>");
            }
            
            pr.println(" </section>");
        }
        
        pr.println("</ini>");
        pr.flush();
    }
    
    public void loadFromXML(InputStream input) throws IOException, InvalidIniFormatException
    {
        loadFromXML(new InputStreamReader(input));
    }
    
    public void loadFromXML(Reader input) throws IOException, InvalidIniFormatException
    {
        Builder builder = new Builder();
        IniParser.newInstance().parseXML(input, builder);
    }
    
    public void loadFromXML(URL input) throws IOException, InvalidIniFormatException
    {
        Builder builder = new Builder();
        IniParser.newInstance().parseXML(input, builder);
    }
    
    public <T> T to(Class<T> clazz)
    {
        Object bean;
        
        if ( _beans == null )
        {
            _beans = new HashMap<Class,Object>();
            bean = null;
        }
        else
        {
            bean = _beans.get(clazz);
        }
        
        if ( bean == null )
        {
            bean = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {clazz}, new BeanInvocationHandler());
            _beans.put(clazz, bean);
        }
        
        return clazz.cast(bean);
    }
    
    protected void resolve(StringBuilder buffer, Section owner)
    {
        int begin = -1;
        int end = -1;
        
        for(int i = buffer.indexOf(SUBST_BEGIN); (i>=0); i = buffer.indexOf(SUBST_BEGIN, i+1) )
        {
            if ( (i+2) > buffer.length() )
            {
                break;
            }
            
            if ( (i != 0) && (buffer.charAt(i-1) == SUBST_ESCAPE) )
            {
                continue;
            }
            
            begin = i;
            
            end = buffer.indexOf(SUBST_END, i);
            
            if ( end < 0 )
            {
                break;
            }
            
            if ( (begin >= 0) && (end > 0) )
            {
                String var = buffer.substring(begin+SUBST_BEGIN_LEN,end);
                String group = null;
                int sep = var.indexOf(SUBST_SEPARATOR);
                String value = null;
                
                if ( sep > 0 )
                {
                    group = var.substring(0,sep);
                    var = var.substring(sep+1);
                }
                
                if ( var != null )
                {
                    if ( group == null )
                    {
                        value = owner.fetch(var);
                    }
                    else if ( SUBST_ENVIRONMENT.equals(group))
                    {
                        value = System.getenv(var);
                    }
                    else if ( SUBST_PROPERTY.equals(group) )
                    {
                        value = System.getProperty(var);
                    }
                    else
                    {
                        owner = get(group);
                        
                        if ( owner != null )
                        {
                            value = owner.fetch(var);
                        }
                    }
                }
                
                if ( value != null )
                {
                    buffer.replace(begin,end+SUBST_END_LEN, value);
                }
            }
        }
    }
    
}
