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
package org.ini4j.spi;

import org.ini4j.Config;
import org.ini4j.IniHandler;
import org.ini4j.IniParser;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

public class IniFormatter implements IniHandler
{
    private static final String OPERATOR = " " + IniParser.OPERATOR + " ";
    private Config _config = Config.getGlobal();
    private PrintWriter _output;

    public static IniFormatter newInstance(Writer out)
    {
        IniFormatter instance = newInstance();

        instance.setOutput(new PrintWriter(out));

        return instance;
    }

    public static IniFormatter newInstance(OutputStream out)
    {
        return newInstance(new OutputStreamWriter(out));
    }

    public static IniFormatter newInstance(Writer out, Config config)
    {
        IniFormatter instance = newInstance(out);

        instance.setConfig(config);

        return instance;
    }

    public static IniFormatter newInstance(OutputStream out, Config config)
    {
        return newInstance(new OutputStreamWriter(out), config);
    }

    public Config getConfig()
    {
        return _config;
    }

    public void endIni()
    {
        getOutput().flush();
    }

    public void endSection()
    {
        getOutput().println();
    }

    public void handleOption(String optionName, String optionValue)
    {
        if (getConfig().isStrictOperator())
        {
            if (getConfig().isEmptyOption() || (optionValue != null))
            {
                getOutput().print(escape(optionName));
                getOutput().print(IniParser.OPERATOR);
            }

            if (optionValue != null)
            {
                getOutput().print(escape(optionValue));
            }

            if (getConfig().isEmptyOption() || (optionValue != null))
            {
                getOutput().println();
            }
        }
        else
        {
            String value = ((optionValue == null) && getConfig().isEmptyOption()) ? "" : optionValue;

            if (value != null)
            {
                getOutput().print(escape(optionName));
                getOutput().print(OPERATOR);
                getOutput().println(escape(value));
            }
        }
    }

    @SuppressWarnings("empty-statement")
    public void startIni()
    {
        ;
    }

    public void startSection(String sectionName)
    {
        getOutput().print(IniParser.SECTION_BEGIN);
        getOutput().print(escape(sectionName));
        getOutput().println(IniParser.SECTION_END);
    }

    protected static IniFormatter newInstance()
    {
        return ServiceFinder.findService(IniFormatter.class);
    }

    protected void setConfig(Config value)
    {
        _config = value;
    }

    protected PrintWriter getOutput()
    {
        return _output;
    }

    protected void setOutput(PrintWriter value)
    {
        _output = value;
    }

    protected String escape(String input)
    {
        return getConfig().isEscape() ? EscapeTool.getInstance().escape(input) : input;
    }
}
