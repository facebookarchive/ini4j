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

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.lang.reflect.Proxy;
import java.net.URI;

///CLOVER:OFF
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import junit.framework.TestCase;

/**
 * Abstract base class of JUnit tests.
 */
public class AbstractTestBase extends TestCase
{
    public static String DWARFS_INI = "org/ini4j/dwarfs.ini";
    public static String DWARFS_XML = "org/ini4j/dwarfs.xml";
    
    protected static class MapBeanHandler extends AbstractBeanInvocationHandler
    {
        private Map<String,String> _map;
        
        MapBeanHandler(Map<String,String> map)
        {
            _map = map;
        }
        
        @Override
        protected void setPropertySpi(String property, Object value, Class clazz)
        {
            _map.put(property, value.toString());
        }
        
        @Override
        protected Object getPropertySpi(String property, Class clazz)
        {
            return _map.get(property);
        }
        
        @Override
        protected boolean hasPropertySpi(String property)
        {
            return _map.containsKey(property);
        }
    }
    
    protected static interface Dwarf
    {
        String PROP_AGE = "age";
        String PROP_WEIGHT = "weight";
        String PROP_HEIGHT = "height";
        String PROP_HOME_PAGE = "homePage";
        
        int getAge();
        void setAge(int age);
        
        double getWeight();
        void setWeight(double weight);
        
        double getHeight();
        void setHeight(double height) throws PropertyVetoException;
        
        void setHomePage(URI location);
        URI getHomePage();
        
        void addPropertyChangeListener(String property, PropertyChangeListener listener);
        void removePropertyChangeListener(String property, PropertyChangeListener listener);
        void addVetoableChangeListener(String property, VetoableChangeListener listener);
        void removeVetoableChangeListener(String property, VetoableChangeListener listener);
        
        boolean hasAge();
        boolean hasWeight();
        boolean hasHeight();
        boolean hasHomePage();
    }

    protected static interface Dwarfs
    {
        String PROP_BASHFUL = "bashful";
        String PROP_DOC = "doc";
        String PROP_DOPEY = "dopey";
        String PROP_GRUMPY = "grumpy";
        String PROP_HAPPY = "happy";
        String PROP_SLEEPY = "sleepy";
        String PROP_SNEEZY = "sneezy";
        
        Dwarf getBashful();
        Dwarf getDoc();
        Dwarf getDopey();
        Dwarf getGrumpy();
        Dwarf getHappy();
        Dwarf getSleepy();
        Dwarf getSneezy();
    }
    
    /**
     * Instantiate test.
     *
     * @param testName name of the test
     */
    public AbstractTestBase(String testName)
    {
        super(testName);
    }
    
    protected static void assertEquals(Dwarf expected, Dwarf actual)
    {
        assertEquals(expected.getAge(), actual.getAge());
        assertEquals(expected.getHeight(), actual.getHeight());
        assertEquals(expected.getWeight(), actual.getWeight());
        assertEquals(expected.getHomePage().toString(), actual.getHomePage().toString());
    }
    
    protected static void assertEquals(Dwarf expected, Ini.Section actual)
    {
        assertEquals("" + expected.getAge(), actual.fetch("age"));
        assertEquals("" + expected.getHeight(), actual.fetch("height"));
        assertEquals("" + expected.getWeight(), actual.fetch("weight"));
        assertEquals("" + expected.getHomePage(), actual.fetch("homePage"));
    }
    
    protected static void assertEquals(Dwarf expected, Preferences actual)
    {
        assertEquals("" + expected.getAge(), actual.get("age",null));
        assertEquals("" + expected.getHeight(), actual.get("height",null));
        assertEquals("" + expected.getWeight(), actual.get("weight",null));
        assertEquals("" + expected.getHomePage(), actual.get("homePage",null));
    }

    protected <T> T newBean(Class<T> clazz)
    {
        return newBean(clazz, new HashMap<String,String>());
    }

    protected <T> T newBean(Class<T> clazz, Map<String,String> map)
    {
        return clazz.cast(Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] {clazz}, new MapBeanHandler(map)));
    }
    
    protected Dwarf newDwarf()
    {
        return newBean(Dwarf.class);
    }
    
    protected Dwarfs newDwarfs() throws Exception
    {
        Dwarfs dwarfs = new Dwarfs()
        {
            private Dwarf _bashful = newDwarf();
            private Dwarf _doc = newDwarf();
            private Dwarf _dopey = newDwarf();
            private Dwarf _grupy = newDwarf();
            private Dwarf _happy = newDwarf();
            private Dwarf _sleepy = newDwarf();
            private Dwarf _sneezy = newDwarf();
            
            @Override
            public Dwarf getBashful()
            {
                return _bashful;
            }
            
            @Override
            public Dwarf getDoc()
            {
                return _doc;
            }
            
            @Override
            public Dwarf getDopey()
            {
                return _dopey;
            }
            
            @Override
            public Dwarf getGrumpy()
            {
                return _grupy;
            }
            
            @Override
            public Dwarf getHappy()
            {
                return _happy;
            }
            
            @Override
            public Dwarf getSleepy()
            {
                return _sleepy;
            }
            
            @Override
            public Dwarf getSneezy()
            {
                return _sneezy;
            }
        };
        
        Dwarf d;
        
        d = dwarfs.getBashful();
        d.setWeight(45.7);
        d.setHeight(98.8);
        d.setAge(67);
        d.setHomePage(new URI("http://snowwhite.tale/~bashful"));

        d = dwarfs.getDoc();
        d.setWeight(49.5);
        d.setHeight(87.7);
        d.setAge(63);
        d.setHomePage(new URI("http://doc.dwarfs"));

        d = dwarfs.getDopey();
        d.setWeight(dwarfs.getBashful().getWeight());
        d.setHeight(dwarfs.getDoc().getHeight());
        d.setAge(23);
        d.setHomePage(new URI("http://dopey.snowwhite.tale/"));
        
        d = dwarfs.getGrumpy();
        d.setWeight(65.3);
        d.setHeight(dwarfs.getDopey().getHeight());
        d.setAge(76);
        d.setHomePage(new URI("http://snowwhite.tale/~grumpy/"));

        d = dwarfs.getHappy();
        d.setWeight(56.4);
        d.setHeight(77.66);
        d.setAge(99);
        d.setHomePage(new URI("http://happy.smurf"));

        d = dwarfs.getSleepy();
        d.setWeight(76.11);
        d.setHeight(87.78);
        d.setAge(121);
        d.setHomePage(new URI("http://snowwhite.tale/~sleepy"));
        
        d = dwarfs.getSneezy();
        d.setWeight(69.7);
        d.setHeight(76.88);
        d.setAge(64);
        d.setHomePage(new URI( dwarfs.getHappy().getHomePage().toString() + "/~sneezy"));

        return dwarfs;
    }
    
    protected Ini loadDwarfs() throws Exception
    {
        return new Ini(getClass().getClassLoader().getResourceAsStream(DWARFS_INI));
    }
    
    private void assertHasProperties(Dwarf dwarf)
    {
        assertTrue(dwarf.hasWeight());
        assertTrue(dwarf.hasHeight());
        assertTrue(dwarf.hasAge());
        assertTrue(dwarf.hasHomePage());
    }

    protected void doTestDwarfs(Dwarfs dwarfs) throws Exception
    {
        Dwarf d;
        
        d = dwarfs.getBashful();
        assertHasProperties(d);
        
        assertEquals(45.7, d.getWeight());
        assertEquals(98.8, d.getHeight());
        assertEquals(67, d.getAge());
        assertEquals("http://snowwhite.tale/~bashful", d.getHomePage().toString());
        
        
        d = dwarfs.getDoc();
        assertHasProperties(d);
        
        assertEquals(49.5, d.getWeight());
        assertEquals(87.7, d.getHeight());
        assertEquals(63, d.getAge());
        assertEquals("http://doc.dwarfs", d.getHomePage().toString());
        
        d = dwarfs.getDopey();
        assertHasProperties(d);
        
        assertEquals(dwarfs.getBashful().getWeight(), d.getWeight());
        assertEquals(dwarfs.getDoc().getHeight(), d.getHeight());
        assertEquals(23, d.getAge());
        assertEquals("http://dopey.snowwhite.tale/", d.getHomePage().toString());
        
        d = dwarfs.getGrumpy();
        assertHasProperties(d);
        
        assertEquals(65.3, d.getWeight());
        assertEquals(dwarfs.getDopey().getHeight(), d.getHeight());
        assertEquals(76, d.getAge());
        assertEquals("http://snowwhite.tale/~grumpy/", d.getHomePage().toString());
        
        d = dwarfs.getHappy();
        assertHasProperties(d);
        
        assertEquals(56.4, d.getWeight());
        assertEquals(77.66, d.getHeight());
        assertEquals(99, d.getAge());
        assertEquals("http://happy.smurf", d.getHomePage().toString());
        
        d = dwarfs.getSleepy();
        assertHasProperties(d);
        
        assertEquals(76.11, d.getWeight());
        assertEquals(87.78, d.getHeight());
        assertEquals(121, d.getAge());
        assertEquals("http://snowwhite.tale/~sleepy", d.getHomePage().toString());
        
        d = dwarfs.getSneezy();
        assertHasProperties(d);
        
        assertEquals(69.7, d.getWeight());
        assertEquals(76.88, d.getHeight());
        assertEquals(64, d.getAge());
        assertEquals(dwarfs.getHappy().getHomePage().toString() + "/~sneezy", d.getHomePage().toString());
    }
}
