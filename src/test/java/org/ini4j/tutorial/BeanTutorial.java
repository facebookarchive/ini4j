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
package org.ini4j.tutorial;

import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.Options;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import java.net.URI;
import java.net.URL;

//|                -------------
//|                Bean Tutorial
//|
//|Bean Tutorial
//|
//| The purpose of this document is to familiarize the reader with the usage of
//| the [ini4j] library's Java Beans interface. Each chapter contains all the
//| necessary code portions and explanation for a given function.
//|
//| At the end of this document, you may find the sample bean interface and bean
//| class used in code sniplets.
//|
//| Code sniplets in this tutorial tested with the following .ini file:
//| {{{dwarfs.html}dwarfs.ini}}
//|
public class BeanTutorial extends AbstractTutorial
{
    public static void main(String[] args) throws Exception
    {
        new BeanTutorial().run(args);
    }

    protected void run() throws Exception
    {
        Ini ini = new Ini(getArgument().toURI().toURL());

        sample01(ini);
        sample02(ini);
        sample03(ini);
        sample04(getArgument().toURI().toURL());
        Options opts = new Options();

        opts.putAll(ini.get("bashful"));
        sample05(opts);

        //
        File optFile = new File(getArgument().getParentFile(), OptTutorial.FILENAME);

        sample06(optFile.toURI().toURL());
    }

//|
//|* Accessing sections as beans
//|
//| While writing a program we usually know the type of the section's values,
//| so we can define one or more java interfaces to access them. An advantage of
//| this solution is that the programmer doesn't have to convert the values
//| because they are converted automatically to the type defined in the
//| interface.
//|
//| Ofcourse you may use setters as well, not just getters. In this way you can
//| change values type safe way.
//|
//|+---------------------------------------------------------------------------+
//{
    void sample01(Ini ini)
    {
        Ini.Section sec = ini.get("happy");
        Dwarf happy = sec.as(Dwarf.class);
        int age = happy.getAge();
        URI homePage = happy.getHomePage();

        happy.setHeight(45.55);

//}
//|       .
//|       .
//|+---------------------------------------------------------------------------+
//|
//| The <<<happy instanceof Dwarf>>> relation is of course fulfilled in the
//| example above.
//|
        assertEquals("http://happy.smurf", homePage.toString());
        assertEquals(99, age);
        assertEquals(45.55, happy.getHeight(), 0.01);
    }

//|* Marshalling beans
//|
//| Sometimes we want to store existing java beans in text file. This operation
//| usually called marshalling.
//|
//| With [ini4j] it is easy to store bean properties in a section. You simply
//| create a section, and call the sections's <<<from()>>> method. Thats it.
//|
//|+---------------------------------------------------------------------------+
//{
    void sample02(Ini ini)
    {
        DwarfBean sleepy = new DwarfBean();

        sleepy.setAge(87);
        sleepy.setHeight(44.3);
        Ini.Section sec = ini.add("sleepy");

        sec.from(sleepy);

//}
//|       .
//|       .
//|+---------------------------------------------------------------------------+
//|
        assertTrue(sec.containsKey("age"));
        assertTrue(sec.containsKey("height"));
    }

//|* Unmarshalling beans
//|
//| If you have a marshalled bean in text file then you may want to read it
//| into bean. This operation usually called unmarshalling.
//|
//| With [ini4j] it is easy to load bean properties from a section. You simply
//| instantiate a bean, and call the sections's <<<to()>>> method. Thats it.
//|
//|+---------------------------------------------------------------------------+
//{
    void sample03(Ini ini)
    {
        DwarfBean grumpy = new DwarfBean();

        ini.get("grumpy").to(grumpy);

//}
//|       .
//|       .
//|+---------------------------------------------------------------------------+
//|
        assertEquals(76, grumpy.getAge());
        assertEquals("/home/grumpy", grumpy.getHomeDir());
    }

//|* Indexed properties
//|
//| For handling indexed properties, you should allow mulpti option value
//| handling in configuration. After enabling this feature, option may contains
//| multiply values (multi line in file). These values can mapped to indexed
//| bean property.
//|
//|+---------------------------------------------------------------------------+
//{
    void sample04(URL location) throws IOException
    {
        Config cfg = new Config();

        cfg.setMultiOption(true);
        Ini ini = new Ini();

        ini.setConfig(cfg);
        ini.load(location);
        Ini.Section sec = ini.get("sneezy");
        Dwarf sneezy = sec.as(Dwarf.class);
        int[] numbers = sneezy.getFortuneNumber();

        //
        // same as above but with unmarshalling...
        //
        DwarfBean sneezyBean = new DwarfBean();

        sec.to(sneezyBean);
        numbers = sneezyBean.getFortuneNumber();

//}
//|       .
//|       .
//|+---------------------------------------------------------------------------+
//|
        assertEquals(4, sec.length("fortuneNumber"));
        assertEquals(4, sneezy.getFortuneNumber().length);
        assertEquals(4, sneezyBean.getFortuneNumber().length);
    }

//|* Options
//|
//| Not only Ini and Ini.Section has bean interface. There is a bean interface
//| for OptionMap class and each derived class for example for Options.
//| Options is an improved java.util.Properties replacement.
//|
//|+---------------------------------------------------------------------------+
//{
    void sample05(Options opts)
    {
        Dwarf dwarf = opts.as(Dwarf.class);
        int age = dwarf.getAge();

        //
        // same as above but with unmarshalling
        //
        DwarfBean dwarfBean = new DwarfBean();

        opts.to(dwarfBean);
        age = dwarfBean.getAge();

//}
//|       .
//|       .
//|+---------------------------------------------------------------------------+
//|
//| In sample above the top level properties (like "age") mapped to bean
//| properties.
//|
        assertEquals(67, dwarf.getAge());
        assertEquals(67, dwarfBean.getAge());
    }

//|* Prefixed mapping
//|
//| Both Ini.Section and Options has possibility to add a prefix to property
//| names while mapping from bean property name to Ini.Section or Options
//| key.
//|
//|+---------------------------------------------------------------------------+
//{
    void sample06(URL optPath) throws IOException
    {
        Options opt = new Options(optPath);
        Dwarf dwarf = opt.as(Dwarf.class, "happy.");
        DwarfBean bean = new DwarfBean();

        opt.to(bean, "dopey.");

//}
//|       .
//|       .
//|+---------------------------------------------------------------------------+
//|
//| In the above example, <<<dwarf>>> bean will contain properties starts with
//| <<<happy.>>> while <<<bean>>> will contain properties starts with
//| <<<dopey.>>>
        assertEquals(99, dwarf.getAge());
        assertEquals(23, bean.getAge());
    }

    static  //
//|
//|* Bean interface used in tutorial
//|
//| This is a very simple bean interface with a few getter and setter. Some of
//| the properties are java primitive types. The <<<homePage>>> property has a
//| complex type (java.net.URI). It is not a problem for \[ini4j\] to do the
//| required type conversion automatically between java.lang.String and the tpye
//| of the given property. The <<<fortuneNumber>>> property is indexed, just to
//| show you may use indexed properties as well.
//|
//|+---------------------------------------------------------------------------+
//{
    public interface Dwarf
    {
        int getAge();

        void setAge(int age);

        int[] getFortuneNumber();

        void setFortuneNumber(int[] value);

        double getHeight();

        void setHeight(double height);

        String getHomeDir();

        void setHomeDir(String dir);

        URI getHomePage();

        void setHomePage(URI location);

        double getWeight();

        void setWeight(double weight);
    }

//}
//|+---------------------------------------------------------------------------+
//|
    static  //
//|
//|* Bean class used in tutorial
//|
//| This is a very simple bean. There is no bound or constrained properties.
//|
//|+---------------------------------------------------------------------------+
//{
    public class DwarfBean implements Dwarf
    {
        private int _age;
        private int[] _fortuneNumber;
        private double _height;
        private String _homeDir;
        private URI _homePage;
        private double _weight;

        @Override public int getAge()
        {
            return _age;
        }

        @Override public void setAge(int value)
        {
            _age = value;
        }

        @Override public int[] getFortuneNumber()
        {
            return _fortuneNumber;
        }

        @Override public void setFortuneNumber(int[] value)
        {
            _fortuneNumber = value;
        }

        @Override public double getHeight()
        {
            return _height;
        }

        @Override public void setHeight(double value)
        {
            _height = value;
        }

        @Override public String getHomeDir()
        {
            return _homeDir;
        }

        @Override public void setHomeDir(String value)
        {
            _homeDir = value;
        }

        @Override public URI getHomePage()
        {
            return _homePage;
        }

        @Override public void setHomePage(URI value)
        {
            _homePage = value;
        }

        @Override public double getWeight()
        {
            return _weight;
        }

        @Override public void setWeight(double value)
        {
            _weight = value;
        }
    }

//}
//|+---------------------------------------------------------------------------+
}
