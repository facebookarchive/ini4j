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
import org.ini4j.spi.IniHandler;
import org.ini4j.spi.IniParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import java.net.URL;

public class Ini extends BasicProfile implements Persistable
{
    private static final long serialVersionUID = -6029486578113700585L;
    private String _comment;
    private Config _config;
    private File _file;

    public Ini()
    {
        _config = Config.getGlobal();
    }

    public Ini(Reader input) throws IOException, InvalidFileFormatException
    {
        this();
        load(input);
    }

    public Ini(InputStream input) throws IOException, InvalidFileFormatException
    {
        this();
        load(input);
    }

    public Ini(URL input) throws IOException, InvalidFileFormatException
    {
        this();
        load(input);
    }

    public Ini(File input) throws IOException, InvalidFileFormatException
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

    @Override public void load() throws IOException, InvalidFileFormatException
    {
        if (_file == null)
        {
            throw new FileNotFoundException();
        }

        load(_file);
    }

    @Override public void load(InputStream input) throws IOException, InvalidFileFormatException
    {
        IniParser.newInstance(getConfig()).parse(input, newBuilder());
    }

    @Override public void load(Reader input) throws IOException, InvalidFileFormatException
    {
        IniParser.newInstance(getConfig()).parse(input, newBuilder());
    }

    @Override public void load(File input) throws IOException, InvalidFileFormatException
    {
        Reader reader = new FileReader(input);

        IniParser.newInstance(getConfig()).parse(reader, newBuilder());
        reader.close();
    }

    @Override public void load(URL input) throws IOException, InvalidFileFormatException
    {
        IniParser.newInstance(getConfig()).parse(input, newBuilder());
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

    protected Config getConfig()
    {
        return _config;
    }

    @Override protected boolean isTreeMode()
    {
        return getConfig().isTree();
    }

    @Override protected char getPathSeparator()
    {
        return getConfig().getPathSeparator();
    }

    @Override protected boolean isPropertyFirstUpper()
    {
        return getConfig().isPropertyFirstUpper();
    }

    protected IniHandler newBuilder()
    {
        return new Builder();
    }

    protected void store(IniHandler formatter) throws IOException
    {
        formatter.startIni();
        storeComment(formatter, _comment);
        for (Ini.Section s : values())
        {
            if (!getConfig().isEmptySection() && (s.size() == 0))
            {
                continue;
            }

            storeComment(formatter, getComment(s.getName()));
            formatter.startSection(s.getName());
            for (String name : s.keySet())
            {
                storeComment(formatter, s.getComment(name));
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

    private void storeComment(IniHandler formatter, String comment)
    {
        if ((comment != null) && (comment.length() != 0))
        {
            formatter.handleComment(comment);
        }
    }

    private class Builder implements IniHandler
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
