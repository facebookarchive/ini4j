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

import org.ini4j.Options;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.Set;

//|                ----------------
//|                Options Tutorial
//|
//|Options Tutorial
//|
//| With standard Properties class there is several small problem. Most of them
//| came from backward compatibility.
//|
//|  * not implements Map\<String,String\>, but Map\<Object,Object\>. If you
//|    want to use Collections api, it is a bit unconfortable.
//|
//|  * only single property values allowed. Probably you already see ugly
//|    workarounds: index number in property names, like: file.1, file.2 ...
//|
//|  * can't refer to other property values. In some environment, like
//|    Apache Ant, you can use ${name} like references, but with standard
//|    java.util.Properties you can't.
//|
//| As side effect of \[ini4j\] development, there is a solution for aboves.
//| This is the Options class, which is basicly a feature rich replacement
//| for java.util.Properties.
//|
//| Code sniplets in this tutorial tested with the following .opt file:
//| {{{dwarf.html}dwarf.opt}}
//|
public class OptTutorial extends AbstractTutorial
{
    public static final String FILENAME = "dwarf.opt";

    public static void main(String[] args) throws Exception
    {
        new OptTutorial().run(args);
    }

    protected void run() throws Exception
    {
        Options opt = new Options(getArgument().toURI().toURL());

        sample01(getArgument());
        sample02(opt);
    }

//|
//|* Instantiating
//|
//| There is nothing special with instantiating Options object, but there is a
//| few constructor, to simplify loading data. These constructors simply call
//| the <<<load()>>> method on newly created instance. Ofcource these
//| constructors are throws IOException.
//|
//|+---------------------------------------------------------------------------+
//{
    void sample01(File file) throws IOException
    {
        Options opt = new Options();

        //
        // or instantiate and load data:
        //
        opt = new Options(new FileReader(file));

//}
//|       .
//|       .
//|+---------------------------------------------------------------------------+
//|
        assertEquals(7, opt.keySet().size());
    }

//|
//|* Map of String
//|
//|+---------------------------------------------------------------------------+
//{
    void sample02(Options opt)
    {
        Set<String> optionNames = opt.keySet();

        //
        String age = opt.get("age");
        String weight = opt.fetch("weight");
        String height = opt.fetch("height");

//}
//|       .
//|       .
//|+---------------------------------------------------------------------------+
//|
//| The Options is a MultiMap\<String,String\>, that is, a map that assigns
//| String values to String keys. So the <<<get>>> method is used to get values
//| inside the options. To get a value, besides <<<get()>>> you can also
//| use <<<fetch()>>> which resolves any occurrent $\{option\} format
//| variable references in the needed value.
        assertEquals("23", opt.get("age"));
        assertEquals("${bashful_weight}", opt.get("weight"));
        assertEquals("45.7", opt.fetch("weight"));
        assertEquals("${doc_height}", opt.get("height"));
        assertEquals("87.7", opt.fetch("height"));
    }
}
