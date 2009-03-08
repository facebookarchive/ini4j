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

import org.ini4j.spi.ServiceFinder;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import java.net.URL;

public class OptionParser extends AbstractParser
{
    private static final String COMMENTS = "!#";
    private static final String OPERATORS = ":=";

    public OptionParser()
    {
        super(OPERATORS, COMMENTS);
    }

    public static OptionParser newInstance()
    {
        return ServiceFinder.findService(OptionParser.class);
    }

    public static OptionParser newInstance(Config config)
    {
        OptionParser instance = newInstance();

        instance.setConfig(config);

        return instance;
    }

    public void parse(InputStream input, OptionHandler handler) throws IOException, InvalidIniFormatException
    {
        parse(newIniSource(input), handler);
    }

    public void parse(Reader input, OptionHandler handler) throws IOException, InvalidIniFormatException
    {
        parse(newIniSource(input), handler);
    }

    public void parse(URL input, OptionHandler handler) throws IOException, InvalidIniFormatException
    {
        parse(newIniSource(input), handler);
    }

    private void parse(IniSource source, OptionHandler handler) throws IOException, InvalidIniFormatException
    {
        for (String line = source.readLine(); line != null; line = source.readLine())
        {
            parseOptionLine(line, handler, source.getLineNumber());
        }
    }
}
