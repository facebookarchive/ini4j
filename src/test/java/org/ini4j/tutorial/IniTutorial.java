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

import org.ini4j.Ini;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.Set;

//|                -------------
//|                Ini Tutorial
//|
//|Ini Tutorial
//|
//| The purpose of this document is to familiarize the reader with the usage of
//| the [ini4j] library's natural interface. Each chapter contains all the
//| necessary code portions and explanation for a given function.
//|
//| Code sniplets in this tutorial tested with the following .ini file:
//| {{{dwarfs.html}dwarfs.ini}}
//|
public class IniTutorial extends AbstractTutorial
{
    public static void main(String[] args) throws Exception
    {
        new IniTutorial().run(args);
    }

    protected void run() throws Exception
    {
        Ini ini = new Ini(getArgument().toURI().toURL());

        sample01(getArgument());
        sample02(ini);
    }

//|
//|* Instantiating
//|
//| There is nothing special with instantiating Ini object, but there is a few
//| constructor, to simplify loading data. These constructors simply call
//| the <<<load()>>> method on newly created instance. Ofcource these
//| constructors are throws IOException.
//|
//|+---------------------------------------------------------------------------+
//{
    void sample01(File file) throws IOException
    {
        Ini ini = new Ini();

        //
        // or instantiate and load data:
        //
        ini = new Ini(new FileReader(file));

//}
//|       .
//|       .
//|+---------------------------------------------------------------------------+
//|
        assertEquals(7, ini.keySet().size());
    }

//|
//|* Map of maps
//|
//|+---------------------------------------------------------------------------+
//{
    void sample02(Ini ini)
    {
        Set<String> sectionNames = ini.keySet();

        // you may iterate over sectionNames for example...
        Ini.Section dopey = ini.get("dopey");
        Set<String> optionNames = dopey.keySet();

        //
        String age = dopey.get("age");
        String weight = dopey.fetch("weight");
        String height = dopey.fetch("height");

//}
//|       .
//|       .
//|+---------------------------------------------------------------------------+
//|
//| The Ini object is a MultiMap\<String,Ini.Section\>, that is, a map that
//| assigns Ini.Section objects to String keys.
//|
//| The section is a MultiMap\<String,String\>, that is, a map that assigns
//| String values to String keys. So the <<<get>>> method is used to get values
//| inside the section. To get a value, besides <<<get()>>> you can also
//| use <<<fetch()>>> which resolves any occurrent $\{section/option\} format
//| variable references in the needed value.
        assertEquals("23", dopey.get("age"));
        assertEquals("${bashful/weight}", dopey.get("weight"));
        assertEquals("45.7", dopey.fetch("weight"));
        assertEquals("${doc/height}", dopey.get("height"));
        assertEquals("87.7", dopey.fetch("height"));
    }
}
