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
package org.ini4j;

import org.ini4j.spi.EscapeTool;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import java.net.URL;

import java.util.Locale;

public abstract class AbstractParser
{
    private final String _comments;
    private Config _config = Config.getGlobal();
    private final String _operators;

    protected AbstractParser(String operators, String comments)
    {
        _operators = operators;
        _comments = comments;
    }

    public void setConfig(Config value)
    {
        _config = value;
    }

    protected Config getConfig()
    {
        return _config;
    }

    protected int indexOfOperator(String line)
    {
        int idx = -1;

        for (char c : _operators.toCharArray())
        {
            int index = line.indexOf(c);

            if ((index >= 0) && ((idx == -1) || (index < idx)))
            {
                idx = index;
            }
        }

        return idx;
    }

    protected void parseError(String line, int lineNumber) throws InvalidIniFormatException
    {
        throw new InvalidIniFormatException("parse error (at line: " + lineNumber + "): " + line);
    }

    protected void parseOptionLine(String line, OptionHandler handler, int lineNumber) throws InvalidIniFormatException
    {
        int idx = indexOfOperator(line);
        String name = null;
        String value = null;

        if (idx < 0)
        {
            if (getConfig().isEmptyOption())
            {
                name = line;
            }
            else
            {
                parseError(line, lineNumber);
            }
        }
        else
        {
            name = unescape(line.substring(0, idx)).trim();
            value = unescape(line.substring(idx + 1)).trim();
        }

        if (name.length() == 0)
        {
            parseError(line, lineNumber);
        }

        if (getConfig().isLowerCaseOption())
        {
            name = name.toLowerCase(Locale.getDefault());
        }

        handler.handleOption(name, value);
    }

    protected String unescape(String line)
    {
        return getConfig().isEscape() ? EscapeTool.getInstance().unescape(line) : line;
    }

    IniSource newIniSource(InputStream input)
    {
        return new IniSource(input, getConfig().isInclude(), _comments);
    }

    IniSource newIniSource(Reader input)
    {
        return new IniSource(input, getConfig().isInclude(), _comments);
    }

    IniSource newIniSource(URL input) throws IOException
    {
        return new IniSource(input, getConfig().isInclude(), _comments);
    }
}
