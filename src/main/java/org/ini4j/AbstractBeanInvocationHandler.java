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

import java.beans.Introspector;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URI;
import java.util.TimeZone;

abstract class AbstractBeanInvocationHandler implements InvocationHandler
{
    private static final String PARSE_METHOD = "valueOf";
    private static final String ADD_PREFIX = "add";
    private static final int ADD_PREFIX_LEN = ADD_PREFIX.length();
    private static final String REMOVE_PREFIX = "remove";
    private static final int REMOVE_PREFIX_LEN = REMOVE_PREFIX.length();
    private static final String PROPERTY_CHANGE_LISTENER = "PropertyChangeListener";
    private static final String VETOABLE_CHANGE_LISTENER = "VetoableChangeListener";
    private static final String READ_PREFIX = "get";
    private static final String READ_BOOLEAN_PREFIX = "is";
    private static final String WRITE_PREFIX = "set";
    private static final String HAS_PREFIX = "has";
    private static final int READ_PREFIX_LEN = READ_PREFIX.length();
    private static final int READ_BOOLEAN_PREFIX_LEN = READ_BOOLEAN_PREFIX.length();
    private static final int WRITE_PREFIX_LEN = WRITE_PREFIX.length();
    private static final int HAS_PREFIX_LEN = HAS_PREFIX.length();
    
    private PropertyChangeSupport _pcSupport;
    private VetoableChangeSupport _vcSupport;
    private Object _proxy;
    
    @SuppressWarnings("empty-statement")
    protected AbstractBeanInvocationHandler()
    {
        ;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws PropertyVetoException
    {
        Object ret = null;
        String name = method.getName();
        String property;
        
        synchronized (this)
        {
            if ( _proxy == null )
            {
                _proxy = proxy;
            }
        }
        
        if ( name.startsWith(READ_PREFIX) )
        {
            property = Introspector.decapitalize(name.substring(READ_PREFIX_LEN));
            ret = getProperty(property, method.getReturnType());
        }
        else if ( name.startsWith(READ_BOOLEAN_PREFIX) )
        {
            property = Introspector.decapitalize(name.substring(READ_BOOLEAN_PREFIX_LEN));
            ret = getProperty(property, method.getReturnType());
        }
        else if ( name.startsWith(WRITE_PREFIX) )
        {
            property = Introspector.decapitalize(name.substring(WRITE_PREFIX_LEN));
            setProperty(property, args[0], method.getParameterTypes()[0]);
        }
        else if ( name.startsWith(ADD_PREFIX) )
        {
            String listener = name.substring(ADD_PREFIX_LEN);
            
            if ( listener.equals(PROPERTY_CHANGE_LISTENER) )
            {
                addPropertyChangeListener((String)args[0], (PropertyChangeListener)args[1]);
            }
            else if ( listener.equals(VETOABLE_CHANGE_LISTENER) )
            {
                addVetoableChangeListener((String)args[0], (VetoableChangeListener)args[1]);
            }
        }
        else if ( name.startsWith(REMOVE_PREFIX) )
        {
            String listener = name.substring(REMOVE_PREFIX_LEN);
            
            if ( listener.equals(PROPERTY_CHANGE_LISTENER) )
            {
                removePropertyChangeListener((String)args[0], (PropertyChangeListener)args[1]);
            }
            else if ( listener.equals(VETOABLE_CHANGE_LISTENER) )
            {
                removeVetoableChangeListener((String)args[0], (VetoableChangeListener)args[1]);
            }
        }
        else if ( name.startsWith(HAS_PREFIX) )
        {
            property = Introspector.decapitalize(name.substring(HAS_PREFIX_LEN));
            ret = Boolean.valueOf(hasProperty(property));
        }
        
        return ret;
    }
    
    protected synchronized Object getProperty(String property, Class<?> clazz)
    {
        Object o;
        
        try
        {
            o = getPropertySpi(property, clazz);
            
            if ( o == null )
            {
                o = zero(clazz);
            }
            else if ( (o instanceof String) && ! clazz.equals(String.class) )
            {
                o = parseValue((String)o, clazz);
            }
        }
        catch (Exception x)
        {
            o = zero(clazz);
        }
        
        return o;
    }
    
    protected abstract Object getPropertySpi(String property, Class<?> clazz);
    
    @SuppressWarnings("empty-statement")
    protected synchronized void setProperty(String property, Object value, Class<?> clazz) throws PropertyVetoException
    {
        try
        {
            boolean pc = (_pcSupport != null) && _pcSupport.hasListeners(property);
            boolean vc = (_vcSupport != null) && _vcSupport.hasListeners(property);
            Object old = ( pc || vc ) ? getProperty(property, clazz) : null;
            
            if ( vc )
            {
                fireVetoableChange(property,old, value);
            }
            
            if ( clazz.equals(String.class) && !(value instanceof String) )
            {
                value = value.toString();
            }
            
            setPropertySpi(property, value, clazz);
            
            if ( pc )
            {
                firePropertyChange(property,old,value);
            }
        }
        catch (PropertyVetoException x)
        {
            throw x;
        }
        catch (Exception x)
        {
            ;
        }
    }
    
    protected abstract void setPropertySpi(String property, Object value, Class<?> clazz);
    
    protected synchronized boolean hasProperty(String property)
    {
        boolean ret;
        
        try
        {
            ret = hasPropertySpi(property);
        }
        catch (Exception x)
        {
            ret = false;
        }
        
        return ret;
    }
    
    protected abstract boolean hasPropertySpi(String property);
    
    protected synchronized void addPropertyChangeListener(String property, PropertyChangeListener listener)
    {
        if ( _pcSupport == null )
        {
            _pcSupport = new PropertyChangeSupport(_proxy);
        }
        
        _pcSupport.addPropertyChangeListener(property, listener);
    }
    
    protected synchronized void removePropertyChangeListener(String property, PropertyChangeListener listener)
    {
        if ( _pcSupport != null )
        {
            _pcSupport.removePropertyChangeListener(property, listener);
        }
    }
    
    protected synchronized void addVetoableChangeListener(String property, VetoableChangeListener listener)
    {
        if ( _vcSupport == null )
        {
            _vcSupport = new VetoableChangeSupport(_proxy);
        }
        _vcSupport.addVetoableChangeListener(property, listener);
    }
    
    protected synchronized void removeVetoableChangeListener(String property, VetoableChangeListener listener)
    {
        if ( _vcSupport != null )
        {
            _vcSupport.removeVetoableChangeListener(property, listener);
        }
    }
    
    protected synchronized void firePropertyChange(String property, Object oldValue, Object newValue)
    {
        if ( _pcSupport != null )
        {
            _pcSupport.firePropertyChange(property, oldValue, newValue);
        }
    }
    
    protected synchronized void fireVetoableChange(String property, Object oldValue, Object newValue) throws PropertyVetoException
    {
        if ( _vcSupport != null )
        {
            _vcSupport.fireVetoableChange(property, oldValue, newValue);
        }
    }
    
    protected synchronized Object getProxy()
    {
        return _proxy;
    }
    
    protected static Object zero(Class clazz)
    {
        Object o = null;
        
        if ( clazz.isPrimitive() )
        {
            if (clazz == Boolean.TYPE)
            {
                o =  Boolean.FALSE;
            }
            else if (clazz == Byte.TYPE)
            {
                o = new Byte((byte)0);
            }
            else if (clazz == Character.TYPE)
            {
                o = new Character('\0');
            }
            else if (clazz == Double.TYPE)
            {
                o = new Double(0.0);
            }
            else if (clazz == Float.TYPE)
            {
                o = new Float(0.0f);
            }
            else if (clazz == Integer.TYPE)
            {
                o = new Integer(0);
            }
            else if (clazz == Long.TYPE)
            {
                o = new Long(0L);
            }
            else if (clazz == Short.TYPE)
            {
                o = new Short((short)0);
            }
        }
        return o;
    }
    
    protected static  Object parseValue(String value, Class clazz) throws IllegalArgumentException
    {
        if (clazz == null)
        {
            throw new IllegalArgumentException("null argument");
        }
        
        Object o = null;
        
        if ( value == null )
        {
            o = zero(clazz);
        }
        else if (clazz.isPrimitive())
        {
            try
            {
                if (clazz == Boolean.TYPE)
                {
                    o = Boolean.valueOf(value);
                }
                else if (clazz == Byte.TYPE)
                {
                    o = Byte.valueOf(value);
                }
                else if (clazz == Character.TYPE)
                {
                    o = new Character(value.charAt(0));
                }
                else if (clazz == Double.TYPE)
                {
                    o = Double.valueOf(value);
                }
                else if (clazz == Float.TYPE)
                {
                    o = Float.valueOf(value);
                }
                else if (clazz == Integer.TYPE)
                {
                    o = Integer.valueOf(value);
                }
                else if (clazz == Long.TYPE)
                {
                    o = Long.valueOf(value);
                }
                else if (clazz == Short.TYPE)
                {
                    o = Short.valueOf(value);
                }
            }
            catch (Exception x)
            {
                throw (IllegalArgumentException) new IllegalArgumentException().initCause(x);
            }
        }
        else
        {
            if (clazz == String.class)
            {
                o = value;
            }
            else if (clazz == Character.class)
            {
                o = new Character(value.charAt(0));
            }
            else
            {
                o = parseSpecialValue(value, clazz);
            }
        }
        
        return o;
    }
    
    protected static Object parseSpecialValue(String value, Class clazz)  throws IllegalArgumentException
    {
        Object o;
        
        try
        {
            if (clazz == File.class)
            {
                o = new File(value);
            }
            else if (clazz == URL.class)
            {
                o = new URL(value);
            }
            else if (clazz == URI.class)
            {
                o = new URI(value);
            }
            else if (clazz == Class.class)
            {
                o = Class.forName(value);
            }
            else if (clazz == TimeZone.class)
            {
                o = TimeZone.getTimeZone(value);
            }
            else
            {
                // look for "valueOf" converter method
                Method parser = clazz.getMethod(PARSE_METHOD, new Class[] {String.class});
                o = parser.invoke(null, new Object[] {value});
            }
        }
        catch (Exception x)
        {
            throw (IllegalArgumentException) new IllegalArgumentException().initCause(x);
        }
        return o;
    }
}
