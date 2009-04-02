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
package org.ini4j.demo;

import bsh.Interpreter;
import bsh.NameSpace;

import bsh.util.JConsole;

import org.ini4j.Ini;
import org.ini4j.Options;
import org.ini4j.Reg;

import java.awt.BorderLayout;

import java.net.URL;

import javax.swing.JApplet;

public class DemoApplet extends JApplet
{
    private static final long serialVersionUID = -7269388726751989763L;
    private static final String PREFIX = "org/ini4j/sample/";
    private static final String DWARFS_INI = PREFIX + "dwarfs.ini";
    private static final String DWARFS_OPT = PREFIX + "dwarfs.opt";
    private static final String DWARFS_REG = PREFIX + "dwarfs.reg";

    @Override public void init()
    {
        getContentPane().setLayout(new BorderLayout());
        JConsole console = new JConsole();

        getContentPane().add(BorderLayout.CENTER, console);
        Interpreter interpreter = new Interpreter(console);
        NameSpace namespace = interpreter.getNameSpace();

        namespace.importPackage("org.ini4j.spi");
        namespace.importPackage("org.ini4j");
        try
        {
            Ini ini = new Ini(getResource(DWARFS_INI));
            Options options = new Options(getResource(DWARFS_OPT));
            Reg reg = new Reg(getResource(DWARFS_REG));

            interpreter.set("ini", ini);
            interpreter.set("options", options);
            interpreter.set("reg", reg);
        }
        catch (Exception x)
        {
            x.printStackTrace();
        }

        console.print("Hi hi hi");
        new Thread(interpreter).start();
        console.println("Hi hi hi");
        console.println("Hi hi hi");
        console.println("Hi hi hi");
    }

    private static URL getResource(String path)
    {
        return Thread.currentThread().getContextClassLoader().getResource(path);
    }
}
