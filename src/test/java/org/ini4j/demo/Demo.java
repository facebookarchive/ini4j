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

import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.Options;
import org.ini4j.Reg;

import java.awt.Color;
import java.awt.Container;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

public class Demo
{
    public static enum Mode
    {
        INI,
        REG,
        OPTIONS;
        private final String _data;
        private final String _help;

        private Mode()
        {
            _data = name().toLowerCase() + "-data.txt";
            _help = name().toLowerCase() + "-help.txt";
        }

        public String getData()
        {
            return _data;
        }

        public String getHelp()
        {
            return _help;
        }
    }

    private final Container _container;
    private final Mode _mode;

    public Demo(Mode mode, Container container)
    {
        _container = container;
        _mode = mode;
    }

    public void init()
    {
        _container.setBackground(Color.WHITE);
        _container.setLayout(new BoxLayout(_container, BoxLayout.PAGE_AXIS));
        JConsole console = new JConsole();
        JTabbedPane input = new JTabbedPane(JTabbedPane.TOP);

        input.setBackground(Color.WHITE);
        JTextArea inputText = new JTextArea();
        JScrollPane sp = new JScrollPane(inputText);

        input.addTab("data", sp);
        JTextArea helpText = new JTextArea();

        sp = new JScrollPane(helpText);

        input.addTab("help", sp);
//
        JPanel buttons = new JPanel();

        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        JButton reload = new JButton("reload");
        JButton parse = new JButton("parse");

        buttons.add(parse);
        buttons.add(reload);

        //
        JTabbedPane output = new JTabbedPane(JTabbedPane.BOTTOM);

        output.addTab("Interpreter", console);

        //
        _container.add(input);
        _container.add(buttons);
        _container.add(output);

        //
        Interpreter interpreter = new Interpreter(console);
        NameSpace namespace = interpreter.getNameSpace();

        namespace.importPackage("org.ini4j.spi");
        namespace.importPackage("org.ini4j");
        namespace.importPackage("org.ini4j.sample");
        try
        {
            Ini ini = new Ini();
            Options options = new Options();
            Reg reg = new Reg();

            interpreter.set("ini", ini);
            interpreter.set("options", options);
            interpreter.set("reg", reg);
            inputText.setText(readResource(_mode.getData()));
            helpText.setText(readResource(_mode.getHelp()));
            helpText.setEditable(false);
        }
        catch (Exception x)
        {
            x.printStackTrace();
        }

        interpreter.setExitOnEOF(false);
        new Thread(interpreter).start();
    }

    private String readResource(String path) throws IOException
    {
        InputStream in = getClass().getResourceAsStream(path);
        Reader reader = new InputStreamReader(in, Config.DEFAULT_FILE_ENCODING);
        StringBuilder str = new StringBuilder();
        char[] buff = new char[8192];
        int n;

        while ((n = reader.read(buff)) >= 0)
        {
            str.append(buff, 0, n);
        }

        return str.toString();
    }
}
