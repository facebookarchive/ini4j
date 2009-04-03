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
import org.ini4j.Persistable;
import org.ini4j.Reg;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

public class Demo
{
    private static enum Mode
    {
        INI("ini"),
        REG("reg"),
        OPTIONS("options");
        private final String _data;
        private final String _help;

        Mode(String prefix)
        {
            _data = prefix + "-data.txt";
            _help = prefix + "-help.txt";
        }

        String getData()
        {
            return _data;
        }

        String getHelp()
        {
            return _help;
        }
    }

    private JConsole _console;
    private final Container _container;
    private Persistable _data;
    private JTextArea _inputTextArea;
    private Interpreter _interpreter;
    private Mode _mode = Mode.INI;

    public Demo(Container container)
    {
        _container = container;
    }

    public void init()
    {
        _container.setBackground(Color.WHITE);
        _container.setLayout(new BoxLayout(_container, BoxLayout.PAGE_AXIS));
        _console = new JConsole();
        JTabbedPane input = new JTabbedPane(JTabbedPane.TOP);

        input.setPreferredSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        input.setBackground(Color.WHITE);
        _inputTextArea = new JTextArea();
        JScrollPane sp = new JScrollPane(_inputTextArea);

        input.addTab("data", sp);
        JTextArea helpText = new JTextArea();

        sp = new JScrollPane(helpText);

        input.addTab("help", sp);
//
        JPanel buttons = new JPanel();

        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        JButton reload = new JButton(new AbstractAction()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        loadTestData();
                    }
                });
        JButton parse = new JButton(new AbstractAction()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        parseData();
                    }
                });

        buttons.add(parse);
        buttons.add(reload);
        reload.setText("load test data");
        parse.setText("parse data");

        //
        JTabbedPane output = new JTabbedPane(JTabbedPane.BOTTOM);

        output.setPreferredSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        output.addTab("Interpreter", _console);

        //
        _container.add(input);
        _container.add(buttons);
        _container.add(output);

        //
        _interpreter = new Interpreter(_console);
        NameSpace namespace = _interpreter.getNameSpace();

        namespace.importPackage("org.ini4j.spi");
        namespace.importPackage("org.ini4j");
        namespace.importPackage("org.ini4j.sample");
        try
        {
            helpText.setText(readResource(_mode.getHelp()));
            helpText.setEditable(false);
        }
        catch (Exception x)
        {
            x.printStackTrace();
        }

        _interpreter.setExitOnEOF(false);
        new Thread(_interpreter).start();
    }

    private void loadTestData()
    {
        try
        {
            _inputTextArea.setText(readResource(_mode.getData()));
        }
        catch (Exception x)
        {
            x.printStackTrace();
        }
    }

    private Persistable newData()
    {
        Persistable ret = null;

        switch (_mode)
        {

            case INI:
                ret = new Ini();
                break;

            case REG:
                ret = new Reg();
                break;

            case OPTIONS:
                ret = new Options();
                break;
        }

        return ret;
    }

    private void parseData()
    {
        Persistable data = newData();

        try
        {
            data.load(new StringReader(_inputTextArea.getText()));
            _interpreter.set("data", data);
        }
        catch (Exception x)
        {
            x.printStackTrace();
        }
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
