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
    protected boolean allowInclude;
    private URL _base;
    private IniSource _chain;
    private final LineNumberReader _reader;

    protected IniSource(InputStream input, boolean includeFlag)
    {
        _reader = new LineNumberReader(new InputStreamReader(input));
        allowInclude = includeFlag;
    }

    protected IniSource(Reader input, boolean includeFlag)
    {
        _reader = new LineNumberReader(input);
        allowInclude = includeFlag;
    }

    protected IniSource(URL input, boolean includeFlag) throws IOException
    {
        _base = input;
        _reader = new LineNumberReader(new InputStreamReader(input.openStream()));
        allowInclude = includeFlag;
    }

    protected int getLineNumber()
    {
        return _reader.getLineNumber();
    }

    protected void close() throws IOException
    {
        _reader.close();
    }

    @SuppressWarnings("empty-statement")
    protected String readLine() throws IOException
    {
        String line;

        if (_chain == null)
        {
            line = _reader.readLine();
            if (line == null)
            {
                close();
            }
            else
            {
                String buff = line.trim();

                if (allowInclude && (buff.length() > 2) && (buff.charAt(0) == INCLUDE_BEGIN) && (buff.charAt(buff.length() - 1) == INCLUDE_END))
                {
                    buff = buff.substring(1, buff.length() - 1).trim();
                    boolean optional = buff.charAt(0) == INCLUDE_OPTIONAL;

                    if (optional)
                    {
                        buff = buff.substring(1).trim();
                    }

                    URL loc = (_base == null) ? new URL(buff) : new URL(_base, buff);

                    if (optional)
                    {
                        try
                        {
                            _chain = new IniSource(loc, allowInclude);
                        }
                        catch (IOException x)
                        {
                            ;
                        }
                        finally
                        {
                            line = readLine();
                        }
                    }
                    else
                    {
                        _chain = new IniSource(loc, allowInclude);
                        line = readLine();
                    }
                }
            }
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
}
