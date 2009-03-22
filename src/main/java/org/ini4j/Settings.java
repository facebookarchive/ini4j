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

import javax.naming.Name;

public class Settings extends BasicOptionTree implements Persistable
{
    private static final long serialVersionUID = -2821168167655687834L;
    private static final String GLOBAL_SECTION_NAME = "";
    private String _comment;
    private Config _config;
    private File _file;

    public Settings()
    {
        _config = Config.getGlobal().clone();
        _config.setGlobalSection(true);
        _config.setGlobalSectionName(GLOBAL_SECTION_NAME);
    }

    public Settings(Reader input) throws IOException, InvalidFileFormatException
    {
        this();
        load(input);
    }

    public Settings(InputStream input) throws IOException, InvalidFileFormatException
    {
        this();
        load(input);
    }

    public Settings(URL input) throws IOException, InvalidFileFormatException
    {
        this();
        load(input);
    }

    public Settings(File input) throws IOException, InvalidFileFormatException
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
        IniParser.newInstance(getConfig()).parse(input, new Builder());
    }

    @Override public void load(Reader input) throws IOException, InvalidFileFormatException
    {
        IniParser.newInstance(getConfig()).parse(input, new Builder());
    }

    @Override public void load(File input) throws IOException, InvalidFileFormatException
    {
        Reader reader = new FileReader(input);

        IniParser.newInstance(getConfig()).parse(reader, new Builder());
        reader.close();
    }

    @Override public void load(URL input) throws IOException, InvalidFileFormatException
    {
        IniParser.newInstance(getConfig()).parse(input, new Builder());
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

    protected void store(IniHandler formatter) throws IOException
    {
        formatter.startIni();
        storeComment(formatter, _comment);
        storeComment(formatter, getComment(getConfig().getGlobalSectionName()));
        store(getConfig().getGlobalSectionName(), this, formatter);
        formatter.endIni();
    }

    protected void store(String name, OptionTree node, IniHandler formatter) throws IOException
    {
        if (getConfig().isEmptySection() || (node.options().size() != 0))
        {
            formatter.startSection(name);
            OptionMap opts = node.options();

            for (String optionName : opts.keySet())
            {
                storeComment(formatter, opts.getComment(optionName));
                int n = getConfig().isMultiOption() ? opts.length(optionName) : 1;

                for (int i = 0; i < n; i++)
                {
                    formatter.handleOption(optionName, opts.get(optionName, i));
                }
            }

            formatter.endSection();
        }

        boolean topLevel = name.equals(getConfig().getGlobalSectionName());

        for (String childKey : node.keySet())
        {
            String childName = topLevel ? childKey : (name + getConfig().getPathSeparator() + childKey);

            storeComment(formatter, node.getComment(childKey));
            store(childName, node.get(childKey), formatter);
        }
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
        private OptionTree _currentNode;
        private boolean _header;
        private String _lastComment;

        public void endIni()
        {

            // comment only .ini files....
            if ((_lastComment != null) && _header)
            {
                setComment(_lastComment);
            }
        }

        public void endSection()
        {
            _currentNode = null;
        }

        public void handleComment(String comment)
        {
            if ((_lastComment != null) && _header)
            {
                setComment(_lastComment);
                _header = false;
            }

            _lastComment = comment;
        }

        public void handleOption(String name, String value)
        {
            _header = false;
            if (getConfig().isMultiOption())
            {
                _currentNode.options().add(name, value);
            }
            else
            {
                _currentNode.options().put(name, value);
            }

            if (_lastComment != null)
            {
                _currentNode.putComment(name, _lastComment);
                _lastComment = null;
            }
        }

        public void startIni()
        {
            _header = true;
        }

        public void startSection(String sectionName)
        {
            Name name = name(sectionName.replace(getConfig().getPathSeparator(), JNDI_PATH_SEPARATOR));

            if (getConfig().isMultiSection())
            {
                _currentNode = addNode(name);
            }
            else
            {
                OptionTree node = lookup(sectionName);

                _currentNode = (node == null) ? addNode(name) : node;
            }

            if (_lastComment != null)
            {
                if (_header)
                {
                    setComment(_lastComment);
                }
                else
                {
                    ((BasicOptionTree) _currentNode).getParent().putComment(name.get(name.size() - 1), _lastComment);
                }

                _lastComment = null;
            }

            _header = false;
        }

        private OptionTree addNode(Name name)
        {
            OptionTree parent = Settings.this;

            for (int i = 0; i < (name.size() - 1); i++)
            {
                OptionTree node = parent.get(name.get(i));

                if (node == null)
                {
                    node = parent.add(name.get(i));
                }

                parent = node;
            }

            return parent.add(name.get(name.size() - 1));
        }
    }
}
