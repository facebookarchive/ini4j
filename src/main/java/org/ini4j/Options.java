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
package org.ini4j;

import org.ini4j.spi.EscapeTool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import java.net.URL;

import java.util.Locale;

public class Options extends OptionMapImpl
{
    private static final char OPERATOR = '=';
    private static final char COMMENT = '#';
    private static final String NEWLINE = "\n";
    public static final String COMMENTS = "!;" + COMMENT;
    public static final String OPERATORS = ":" + OPERATOR;
    private Config _config;

    public Options()
    {
        _config = Config.getGlobal().clone();
        _config.setEmptyOption(true);
    }

    public Options(Reader input) throws IOException, InvalidIniFormatException
    {
        this();
        load(input);
    }

    public Options(InputStream input) throws IOException, InvalidIniFormatException
    {
        this();
        load(input);
    }

    public Options(URL input) throws IOException, InvalidIniFormatException
    {
        this();
        load(input);
    }

    public void setConfig(Config value)
    {
        _config = value;
    }

    public void load(InputStream input) throws IOException, InvalidIniFormatException
    {
        parse(new IniSource(input, getConfig().isInclude()));
    }

    public void load(Reader input) throws IOException, InvalidIniFormatException
    {
        parse(new IniSource(input, getConfig().isInclude()));
    }

    public void load(URL input) throws IOException, InvalidIniFormatException
    {
        parse(new IniSource(input, getConfig().isInclude()));
    }

    public void store(OutputStream output) throws IOException
    {
        format(new OutputStreamWriter(output));
    }

    public void store(Writer output) throws IOException
    {
        format(output);
    }

    protected Config getConfig()
    {
        return _config;
    }

    protected String escape(String input)
    {
        return getConfig().isEscape() ? EscapeTool.getInstance().escape(input) : input;
    }

    protected void format(Writer output) throws IOException
    {
        for (String name : keySet())
        {
            int n = getConfig().isMultiOption() ? length(name) : 1;

            for (int i = 0; i < n; i++)
            {
                String value = get(name, i);

                if ((value != null) || getConfig().isEmptyOption())
                {
                    output.append(escape(name));
                    output.append(OPERATOR);
                    if (value != null)
                    {
                        output.append(escape(value));
                    }

                    output.append(NEWLINE);
                }
            }
        }

        output.flush();
    }

    protected void parseError(String line, int lineNumber) throws InvalidIniFormatException
    {
        throw new InvalidIniFormatException("parse error (at line: " + lineNumber + "): " + line);
    }

    protected String unescape(String line)
    {
        return getConfig().isEscape() ? EscapeTool.getInstance().unescape(line) : line;
    }

    private void parse(IniSource source) throws IOException, InvalidIniFormatException
    {
        boolean multi = getConfig().isMultiOption();

        for (String srcline = source.readLine(); srcline != null; srcline = source.readLine())
        {
            String line = srcline.trim();

            if ((line.length() == 0) || (COMMENTS.indexOf(line.charAt(0)) >= 0))
            {
                continue;
            }

            int idx = -1;

            for (char c : OPERATORS.toCharArray())
            {
                int index = line.indexOf(c);

                if ((index >= 0) && ((idx == -1) || (index < idx)))
                {
                    idx = index;
                }
            }

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
                    parseError(line, source.getLineNumber());
                }
            }
            else
            {
                name = unescape(line.substring(0, idx)).trim();
                value = unescape(line.substring(idx + 1)).trim();
            }

            if (name.length() == 0)
            {
                parseError(line, source.getLineNumber());
            }

            if (getConfig().isLowerCaseOption())
            {
                name = name.toLowerCase(Locale.getDefault());
            }

            if (multi)
            {
                add(name, value);
            }
            else
            {
                put(name, value);
            }
        }
    }
}
