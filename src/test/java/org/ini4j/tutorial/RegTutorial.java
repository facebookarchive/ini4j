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
import org.ini4j.Registry;

import org.ini4j.sample.Dwarfs;

import org.ini4j.test.DwarfsData;
import org.ini4j.test.Helper;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
//| Code sniplets in this tutorial tested with the following .reg file:
//| {{{../sample/dwarfs.reg.html}dwarfs.reg}}
//|
//</editor-fold>
public class RegTutorial extends AbstractTutorial
{
    public static final String FILENAME = "../sample/dwarfs.reg";

    public static void main(String[] args) throws Exception
    {
        new RegTutorial().run(filearg(args));
    }

    @Override protected void run(File arg) throws Exception
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
        assertNotNull(reg.get(Helper.DWARFS_REG_PATH + "\\dwarfs"));
        Helper.assertEquals(DwarfsData.dwarfs, reg.as(Dwarfs.class, Helper.DWARFS_REG_PATH + "\\dwarfs\\"));
    }

//|
//|* Tree
//{
    void sample02(Reg reg)
    {
        Registry.Key base = reg.get(Reg.Hive.HKEY_CURRENT_USER + "\\Software\\ini4j-test");
        Registry.Key dwarfs = base.getChild("dwarfs");
        Registry.Key bashful = dwarfs.getChild("bashful");
        String homePage = bashful.get("homePage");
//}
//|

        assertEquals(DwarfsData.bashful.homePage.toString(), homePage);
    }
}
