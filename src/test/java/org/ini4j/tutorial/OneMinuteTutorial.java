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

import org.ini4j.Ini;

import org.ini4j.sample.Dwarf;
import org.ini4j.sample.Dwarfs;

import org.ini4j.test.DwarfsData;
import org.ini4j.test.Helper;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

//<editor-fold defaultstate="collapsed" desc="apt documentation">
//|
//|                -------------------
//|                One minute Tutorial
//|
//|One minute Tutorial
//|
//| Using \[ini4j\] is realy simple !
//|
//|
//</editor-fold>
public class OneMinuteTutorial extends AbstractTutorial
{
    public static void main(String[] args) throws Exception
    {
        new OneMinuteTutorial().run(filearg(args));
    }

    protected void run(File arg) throws Exception
    {
//        sample01(arg.getCanonicalPath());
//        sample02(arg.getCanonicalPath());
    }

//|
//|*First step
//|
//| Lets read some value from .ini file (ofcourse in type safe way)...
//|
//{
    void sample01(String filename) throws IOException
    {
        Ini ini = new Ini(new File(filename));
        int age = ini.get("happy", "age", int.class);
        double height = ini.get("happy", "height", double.class);

//| ... assuming there is a section with name <<<happy>>>, which contains at least
//| two options: <<<age>>> and <<<height>>>
//}
        assertEquals(DwarfsData.happy.age, age);
        assertEquals(DwarfsData.happy.height, height, Helper.DELTA);
    }

//|
//|*Second step
//|
//| OK, reading is simple, but whats about writing values ...
//|
//{
    void sample02(String filename) throws IOException
    {
        Ini ini = new Ini(new File(filename));

        ini.put("sleepy", "age", 55);
        ini.put("sleepy", "weight", 45.6);
        ini.store();

//| ... and then file will have a section <<<sleepy>>> and in this section there
//| will be at least two options: <<<age>>> with value <<<55>>> and <<<weight>>>
//| with value <<<45.6>>>
//}
        assertEquals(55, (int) ini.get(Dwarfs.PROP_SLEEPY, Dwarf.PROP_AGE, int.class));
        assertEquals(45.6, (double) ini.get(Dwarfs.PROP_SLEEPY, Dwarf.PROP_WEIGHT, double.class), Helper.DELTA);
    }

//|
//|* Next steps
//|
//| If you want to know more about this library, read
//| {{{../tutorials.html}tutorials}}
}
