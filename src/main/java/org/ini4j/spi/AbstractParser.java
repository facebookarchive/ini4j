/*
 * Copyright (c) Facebook, Inc. and its affiliates. All Rights Reserved
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
import org.ini4j.InvalidFileFormatException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import java.net.URISyntaxException;
import java.net.URL;

import java.nio.file.Paths;
import java.util.Locale;

abstract class AbstractParser
{
    private final String _comments;
    private Config _config = Config.getGlobal();
    private final String _operators;

    protected AbstractParser(String operators, String comments)
    {
        _operators = operators;
        _comments = comments;
    }

    protected Config getConfig()
    {
        return _config;
    }

    protected void setConfig(Config value)
    {
        _config = value;
    }

    protected void parseError(String line, URL url, int lineNumber) throws InvalidFileFormatException
    {
        String message;
        if(url == null) {
            message = String.format("parse error (at line: %d): %s", lineNumber, line);
        } else {
            try {
                // Handle windows paths, without this conversion, they look like /C:/foo/bar
                message = String.format("parse error (in %s at line %d): %s", Paths.get(url.toURI()), lineNumber, line);
            } catch (URISyntaxException e) {
                message = String.format("parse error (in %s at line %d): %s", url, lineNumber, line);
            }
        }
        throw new InvalidFileFormatException(message);
    }

    IniSource newIniSource(InputStream input, HandlerBase handler)
    {
        return new IniSource(input, handler, _comments, getConfig());
    }

    IniSource newIniSource(Reader input, HandlerBase handler)
    {
        return new IniSource(input, handler, _comments, getConfig());
    }

    IniSource newIniSource(URL input, HandlerBase handler) throws IOException
    {
        return new IniSource(input, handler, _comments, getConfig());
    }

    void parseOptionLine(String line, HandlerBase handler, URL url, int lineNumber) throws InvalidFileFormatException
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
                parseError(line, url, lineNumber);
            }
        }
        else
        {
            name = unescapeKey(line.substring(0, idx)).trim();
            value = unescapeValue(line.substring(idx + 1)).trim();
        }

        if (name.length() == 0)
        {
            parseError(line, url, lineNumber);
        }

        if (getConfig().isLowerCaseOption())
        {
            name = name.toLowerCase(Locale.getDefault());
        }

        handler.handleOption(name, value);
    }

    String unescapeKey(String line)
    {
        return getConfig().isEscape() ? EscapeTool.getInstance().unescape(line) : line;
    }

    String unescapeValue(String line)
    {
        return (getConfig().isEscape() && !getConfig().isEscapeKeyOnly()) ? EscapeTool.getInstance().unescape(line) : line;
    }

    private int indexOfOperator(String line)
    {
        int idx = -1;

        for (char c : _operators.toCharArray())
        {
            int index = line.indexOf(c);

            while ((index >= 0))
            {
                if ((index >= 0) && ((index == 0) || (line.charAt(index - 1) != '\\')) && ((idx == -1) || (index < idx)))
                {
                    idx = index;

                    break;
                }

                index = (index == (line.length() - 1)) ? -1 : line.indexOf(c, index + 1);
            }

            //if ((index >= 0) && ((index == 0) || (line.charAt(index - 1) != '\\')) && ((idx == -1) || (index < idx)))
            // {
            //     idx = index;
            // }
        }

        return idx;
    }
}
