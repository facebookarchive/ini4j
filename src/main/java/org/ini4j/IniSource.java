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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;

import java.net.URL;

class IniSource
{
    public static final char INCLUDE_BEGIN = '<';
    public static final char INCLUDE_END = '>';
    public static final char INCLUDE_OPTIONAL = '?';
    private static final String EMPTY = "";
    protected final boolean allowInclude;
    protected final String commentChars;
    private URL _base;
    private IniSource _chain;
    private final LineNumberReader _reader;

    protected IniSource(InputStream input, boolean includeFlag, String comments)
    {
        _reader = new LineNumberReader(new InputStreamReader(input));
        allowInclude = includeFlag;
        commentChars = comments;
    }

    protected IniSource(Reader input, boolean includeFlag, String comments)
    {
        _reader = new LineNumberReader(input);
        allowInclude = includeFlag;
        commentChars = comments;
    }

    protected IniSource(URL input, boolean includeFlag, String comments) throws IOException
    {
        _base = input;
        _reader = new LineNumberReader(new InputStreamReader(input.openStream()));
        allowInclude = includeFlag;
        commentChars = comments;
    }

    protected int getLineNumber()
    {
        return _reader.getLineNumber();
    }

    protected void close() throws IOException
    {
        _reader.close();
    }

    protected String readLine() throws IOException
    {
        String line;

        if (_chain == null)
        {
            line = readLineLocal();
        }
        else
        {
            line = _chain.readLine();
            if (line == null)
            {
                _chain = null;
                line = readLine();
            }
        }

        return line;
    }

    private String handleInclude(String input) throws IOException
    {
        String line = input;

        if (allowInclude && (line.length() > 2) && (line.charAt(0) == INCLUDE_BEGIN) && (line.charAt(line.length() - 1) == INCLUDE_END))
        {
            line = line.substring(1, line.length() - 1).trim();
            boolean optional = line.charAt(0) == INCLUDE_OPTIONAL;

            if (optional)
            {
                line = line.substring(1).trim();
            }

            URL loc = (_base == null) ? new URL(line) : new URL(_base, line);

            if (optional)
            {
                try
                {
                    _chain = new IniSource(loc, allowInclude, commentChars);
                }
                catch (IOException x)
                {
                    assert true;
                }
                finally
                {
                    line = readLine();
                }
            }
            else
            {
                _chain = new IniSource(loc, allowInclude, commentChars);
                line = readLine();
            }
        }

        return line;
    }

    private String readLineLocal() throws IOException
    {
        String line = readLineLocalOne();

        while ((line != null) && (line.length() == 0))
        {
            line = readLineLocalOne();
        }

        if (line == null)
        {
            close();
        }
        else
        {
            line = handleInclude(line);
        }

        return line;
    }

    private String readLineLocalOne() throws IOException
    {
        String line = _reader.readLine();

        if (line != null)
        {
            line = line.trim();
            if ((line.length() != 0) && (commentChars.indexOf(line.charAt(0)) >= 0))
            {
                line = EMPTY;
            }
        }

        return line;
    }
}
