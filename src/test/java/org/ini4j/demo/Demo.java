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

import org.ini4j.Persistable;

import org.ini4j.demo.DemoModel.Mode;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

public class Demo implements ActionListener
{
    private static final String CMD_INI = "ini";
    private static final String CMD_OPT = "opt";
    private static final String CMD_REG = "reg";

    private enum Command
    {
        MODE_INI,
        MODE_REG,
        MODE_OPTIONS,
        LOAD_TEST_DATA,
        PARSE_DATA
    }

    private JConsole _console;
    private final Container _container;
    private Persistable _data;
    private JTextArea _inputTextArea;
    private Interpreter _interpreter;
    private Mode _mode = Mode.INI;
    private DemoModel _model;

    public Demo(Container container)
    {
        _container = container;
    }

    @Override public void actionPerformed(ActionEvent event)
    {
        Command cmd = Command.valueOf(event.getActionCommand());

        switch (cmd)
        {

            case MODE_INI:
                doMode(Mode.INI);
                break;

            case MODE_REG:
                doMode(Mode.REG);
                break;

            case MODE_OPTIONS:
                doMode(Mode.OPTIONS);
                break;

            case LOAD_TEST_DATA:
                doLoad();
                break;
        }
    }

    public void init()
    {
        _container.setBackground(Color.WHITE);
        _container.setLayout(new BoxLayout(_container, BoxLayout.PAGE_AXIS));
        JConsole console = new JConsole();

        console.setBackground(Color.WHITE);
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
        ButtonGroup group = new ButtonGroup();

        buttons.setBackground(Color.WHITE);
        buttons.add(new JLabel("Mode: "));
        addModeButton(group, buttons, Mode.INI);
        addModeButton(group, buttons, Mode.REG);
        addModeButton(group, buttons, Mode.OPTIONS);
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.add(Box.createHorizontalGlue());
        JButton reload = new JButton();

        reload.setActionCommand(Command.LOAD_TEST_DATA.name());
        JButton parse = new JButton();

        parse.setActionCommand(Command.PARSE_DATA.name());
        buttons.add(parse);
        buttons.add(reload);
        reload.setText("load test data");
        parse.setText("parse data");

        //
        JTabbedPane output = new JTabbedPane(JTabbedPane.BOTTOM);

        _console = new JConsole();

        output.addTab("Console", _console);
        output.setBackground(Color.WHITE);
        output.setPreferredSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        output.addTab("Interpreter", console);

        //
        _container.add(input);
        _container.add(buttons);
        _container.add(output);

        //
        _interpreter = new Interpreter(console);
        NameSpace namespace = _interpreter.getNameSpace();

        namespace.importPackage("org.ini4j.spi");
        namespace.importPackage("org.ini4j");
        namespace.importPackage("org.ini4j.sample");
        try
        {
            helpText.setText(_model.help());
            helpText.setEditable(false);
        }
        catch (Exception x)
        {
            x.printStackTrace();
        }

        _interpreter.setExitOnEOF(false);
        new Thread(_interpreter).start();
    }

    private void addModeButton(ButtonGroup group, JPanel panel, Mode mode)
    {
        String label = mode.name().charAt(0) + mode.name().toLowerCase().substring(1);
        JRadioButton button = new JRadioButton(label);

        button.setActionCommand("MODE_" + mode.name());
        button.setSelected(mode == Mode.INI);
        panel.add(button);
        button.addActionListener(this);
        group.add(button);
    }

    private void doLoad()
    {
        try
        {
            _inputTextArea.setText(_model.load());
        }
        catch (Exception x)
        {
            x.printStackTrace();
        }
    }

    private void doMode(Mode mode)
    {
        _model.setMode(mode);
    }

    private void doParse()
    {
        try
        {
            _model.parse(_inputTextArea.getText());
        }
        catch (Exception x)
        {
            x.printStackTrace();
        }
    }
}
