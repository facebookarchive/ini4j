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

import org.ini4j.spi.IniFormatter;
import org.ini4j.spi.XMLFormatter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import java.net.URL;

public class Ini extends ProfileImpl implements Persistable
{
    private String _comment;
    private Config _config = Config.getGlobal();
    private File _file;

    public Ini()
    {
        assert true;
    }

    public Ini(Reader input) throws IOException, InvalidIniFormatException
    {
        this();
        load(input);
    }

    public Ini(InputStream input) throws IOException, InvalidIniFormatException
    {
        this();
        load(input);
    }

    public Ini(URL input) throws IOException, InvalidIniFormatException
    {
        this();
        load(input);
    }

    public Ini(File input) throws IOException, InvalidIniFormatException
    {
        this();
        _file = input;
        load();
    }

    public String getComment()
    {
        return _comment;
    }

    public void setComment(String value)
    {
        _comment = value;
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
        IniParser.newInstance(getConfig()).parse(input, new Builder());
    }

    @Override public void load(Reader input) throws IOException, InvalidIniFormatException
    {
        IniParser.newInstance(getConfig()).parse(input, new Builder());
    }

    @Override public void load(File input) throws IOException, InvalidIniFormatException
    {
        Reader reader = new FileReader(input);

        IniParser.newInstance(getConfig()).parse(reader, new Builder());
        reader.close();
    }

    @Override public void load(URL input) throws IOException, InvalidIniFormatException
    {
        IniParser.newInstance(getConfig()).parse(input, new Builder());
    }

    public void loadFromXML(InputStream input) throws IOException, InvalidIniFormatException
    {
        loadFromXML(new InputStreamReader(input));
    }

    public void loadFromXML(Reader input) throws IOException, InvalidIniFormatException
    {
        Builder builder = new Builder();

        IniParser.newInstance(getConfig()).parseXML(input, builder);
    }

    public void loadFromXML(URL input) throws IOException, InvalidIniFormatException
    {
        Builder builder = new Builder();

        IniParser.newInstance(getConfig()).parseXML(input, builder);
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
        store(IniFormatter.newInstance(output, getConfig()));
    }

    @Override public void store(Writer output) throws IOException
    {
        store(IniFormatter.newInstance(output, getConfig()));
    }

    @Override public void store(File output) throws IOException
    {
        Writer writer = new FileWriter(output);

        store(IniFormatter.newInstance(writer, getConfig()));
        writer.close();
    }

    public void storeToXML(OutputStream output) throws IOException
    {
        store(XMLFormatter.newInstance(output));
    }

    public void storeToXML(Writer output) throws IOException
    {
        store(XMLFormatter.newInstance(output));
    }

    protected Config getConfig()
    {
        return _config;
    }

    protected void store(IniHandler formatter) throws IOException
    {
        formatter.startIni();
        for (Ini.Section s : values())
        {
            formatter.startSection(s.getName());
            for (String name : s.keySet())
            {
                int n = getConfig().isMultiOption() ? s.length(name) : 1;

                for (int i = 0; i < n; i++)
                {
                    formatter.handleOption(name, s.get(name, i));
                }
            }

            formatter.endSection();
        }

        formatter.endIni();
    }

    private class Builder implements IniHandler, CommentHandler
    {
        private Section _currentSection;
        private boolean _header;
        private String _lastComment;

        @Override public void endIni()
        {

            // comment only .ini files....
            if ((_lastComment != null) && _header)
            {
                setComment(_lastComment);
            }
        }

        @Override public void endSection()
        {
            _currentSection = null;
        }

        @Override public void handleComment(String comment)
        {
            if ((_lastComment != null) && _header)
            {
                setComment(_lastComment);
                _header = false;
            }

            _lastComment = comment;
        }

        @Override public void handleOption(String name, String value)
        {
            _header = false;
            if (getConfig().isMultiOption())
            {
                _currentSection.add(name, value);
            }
            else
            {
                _currentSection.put(name, value);
            }

            if (_lastComment != null)
            {
                _currentSection.putComment(name, _lastComment);
                _lastComment = null;
            }
        }

        @Override public void startIni()
        {
            _header = true;
        }

        @Override public void startSection(String sectionName)
        {
            if (getConfig().isMultiSection())
            {
                _currentSection = add(sectionName);
            }
            else
            {
                Section s = get(sectionName);

                _currentSection = (s == null) ? add(sectionName) : s;
            }

            if (_lastComment != null)
            {
                if (_header)
                {
                    setComment(_lastComment);
                }
                else
                {
                    putComment(sectionName, _lastComment);
                }

                _lastComment = null;
            }

            _header = false;
        }
    }
}
