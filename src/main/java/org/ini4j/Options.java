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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import java.net.URL;

public class Options extends OptionMapImpl implements Persistable
{
    private static final char OPERATOR = '=';
    private static final String NEWLINE = "\n";
    private Config _config;
    private File _file;

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

    public Options(File input) throws IOException, InvalidIniFormatException
    {
        this();
        setFile(input);
        load();
    }

    public void setConfig(Config value)
    {
        _config = value;
    }

    @Override public File getFile()
    {
        return _file;
    }

    @Override public void setFile(File value)
    {
        _file = value;
    }

    @Override public void load() throws IOException, InvalidIniFormatException
    {
        if (_file == null)
        {
            throw new FileNotFoundException();
        }

        load(_file);
    }

    @Override public void load(InputStream input) throws IOException, InvalidIniFormatException
    {
        OptionParser.newInstance(getConfig()).parse(input, new Builder());
    }

    @Override public void load(Reader input) throws IOException, InvalidIniFormatException
    {
        OptionParser.newInstance(getConfig()).parse(input, new Builder());
    }

    @Override public void load(URL input) throws IOException, InvalidIniFormatException
    {
        OptionParser.newInstance(getConfig()).parse(input, new Builder());
    }

    @Override public void load(File input) throws IOException, InvalidIniFormatException
    {
        Reader reader = new FileReader(input);

        OptionParser.newInstance(getConfig()).parse(reader, new Builder());
        reader.close();
    }

    @Override public void store() throws IOException
    {
        if (_file == null)
        {
            throw new FileNotFoundException();
        }

        store(_file);
    }

    @Override public void store(OutputStream output) throws IOException
    {
        format(new OutputStreamWriter(output));
    }

    @Override public void store(Writer output) throws IOException
    {
        format(output);
    }

    @Override public void store(File output) throws IOException
    {
        Writer writer = new FileWriter(output);

        format(writer);
        writer.close();
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

    private class Builder implements OptionHandler
    {
        @Override public void handleOption(String name, String value)
        {
            if (getConfig().isMultiOption())
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
