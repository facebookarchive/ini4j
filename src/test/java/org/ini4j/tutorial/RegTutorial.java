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
package org.ini4j.tutorial;

import org.ini4j.Reg;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Set;

//<editor-fold defaultstate="collapsed" desc="apt documentation">
//|
//|                -------------
//|                Reg Tutorial
//|
//|Reg Tutorial
//|
//| The purpose of this document is to familiarize the reader with the usage of
//| the [ini4j] library's .reg interface. Each chapter contains all the
//| necessary code portions and explanation for a given function.
//|
//| Code sniplets in this tutorial tested with the following .ini file:
//| {{{../sample/dwarfs.reg.html}dwarfs.ini}}
//|
//</editor-fold>
public class RegTutorial extends AbstractTutorial
{
    public static final String FILENAME = "../sample/dwarfs.reg";

    public static void main(String[] args) throws Exception
    {
        new RegTutorial().run(filearg(args));
    }

    protected void run(File arg) throws Exception
    {
        Reg reg = new Reg(arg.toURI().toURL());

        sample01(arg);
        sample02(reg);
    }

//|
//|* Instantiating
//|
//| There is nothing special with instantiating Ini object, but there is a few
//| constructor, to simplify loading data. These constructors simply call
//| the <<<load()>>> method on newly created instance. Ofcource these
//| constructors are throws IOException.
//{
    void sample01(File file) throws IOException
    {
        Reg reg = new Reg();

        //
        // or instantiate and load data:
        //
        reg = new Reg(new FileInputStream(file));

//}
        //assertEquals(7, reg.keySet().size());
    }

//|
//|* Map of maps
//{
    void sample02(Reg reg)
    {
        Set<String> sectionNames = reg.keySet();

        // you may iterate over sectionNames for example...
        //  Ini.Section dopey = reg.get("dopey");
        // Set<String> optionNames = dopey.keySet();
        //
        // String age = dopey.get("age");
        // String weight = dopey.fetch("weight");
        // String height = dopey.fetch("height");
//}
//|
//| The Ini object is a MultiMap\<String,Ini.Section\>, that is, a map that
//| assigns Ini.Section objects to String keys.
//|
//| The section is a MultiMap\<String,String\>, that is, a map that assigns
//| String values to String keys. So the <<<get>>> method is used to get values
//| inside the section. To get a value, besides <<<get()>>> you can also
//| use <<<fetch()>>> which resolves any occurrent $\{section/option\} format
//| variable references in the needed value.
        //Helper.assertEquals(DwarfsData.dopey, dopey.as(Dwarf.class));
    }
}
