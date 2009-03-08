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
import org.ini4j.spi.ServiceFinder;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.net.URL;

import java.util.Locale;

import javax.xml.parsers.SAXParserFactory;

public class IniParser
{
    public static final String COMMENTS = ";#";
    public static final char OPERATOR = '=';
    public static final char SECTION_BEGIN = '[';
    public static final char SECTION_END = ']';
    private Config _config = Config.getGlobal();

    public static IniParser newInstance()
    {
        return ServiceFinder.findService(IniParser.class);
    }

    public static IniParser newInstance(Config config)
    {
        IniParser instance = newInstance();

        instance.setConfig(config);

        return instance;
    }

    public void setConfig(Config value)
    {
        _config = value;
    }

    public void parse(InputStream input, IniHandler handler) throws IOException, InvalidIniFormatException
    {
        parse(new IniSource(input, getConfig().isInclude(), COMMENTS), handler);
    }

    public void parse(Reader input, IniHandler handler) throws IOException, InvalidIniFormatException
    {
        parse(new IniSource(input, getConfig().isInclude(), COMMENTS), handler);
    }

    public void parse(URL input, IniHandler handler) throws IOException, InvalidIniFormatException
    {
        parse(new IniSource(input, getConfig().isInclude(), COMMENTS), handler);
    }

    public void parseXML(InputStream input, IniHandler handler) throws IOException, InvalidIniFormatException
    {
        parseXML(new InputStreamReader(input), handler);
    }

    public void parseXML(Reader input, final IniHandler handler) throws IOException, InvalidIniFormatException
    {
        class XmlToIni extends DefaultHandler
        {
            private static final String TAG_SECTION = "section";
            private static final String TAG_OPTION = "option";
            private static final String TAG_INI = "ini";
            private static final String ATTR_KEY = "key";
            private static final String ATTR_VALUE = "value";
            private static final String ATTR_VERSION = "version";
            private static final String CURRENT_VERSION = "1.0";

            @Override public void startElement(String uri, String localName, String qname, Attributes attrs) throws SAXException
            {
                String key = attrs.getValue(ATTR_KEY);

                if (qname.equals(TAG_INI))
                {
                    String ver = attrs.getValue(ATTR_VERSION);

                    if ((ver == null) || !ver.equals(CURRENT_VERSION))
                    {
                        throw new SAXException("Missing or invalid 'version' attribute");
                    }

                    handler.startIni();
                }
                else
                {
                    if (key == null)
                    {
                        throw new SAXException("missing '" + ATTR_KEY + "' attribute");
                    }

                    if (qname.equals(TAG_SECTION))
                    {
                        handler.startSection(key);
                    }
                    else if (qname.equals(TAG_OPTION))
                    {
                        handler.handleOption(key, attrs.getValue(ATTR_VALUE));
                    }
                    else
                    {
                        throw new SAXException("Invalid element: " + qname);
                    }
                }
            }

            @Override public void endElement(String uri, String localName, String qname) throws SAXException
            {
                if (qname.equals(TAG_SECTION))
                {
                    handler.endSection();
                }
                else if (qname.equals(TAG_INI))
                {
                    handler.endIni();
                }
            }
        }

        XmlToIni xmlToini = new XmlToIni();

        try
        {
            SAXParserFactory.newInstance().newSAXParser().parse(new InputSource(input), xmlToini);
        }
        catch (Exception x)
        {
            throw new InvalidIniFormatException(x);
        }
    }

    public void parseXML(URL input, IniHandler handler) throws IOException, InvalidIniFormatException
    {
        parseXML(input.openStream(), handler);
    }

    protected Config getConfig()
    {
        return _config;
    }

    protected void parseError(String line, int lineNumber) throws InvalidIniFormatException
    {
        throw new InvalidIniFormatException("parse error (at line: " + lineNumber + "): " + line);
    }

    protected String unescape(String line)
    {
        return getConfig().isEscape() ? EscapeTool.getInstance().unescape(line) : line;
    }


    private String handleSectionLine(String line, IniSource source, IniHandler handler) throws InvalidIniFormatException
    {
        String sectionName;

        if (line.charAt(line.length() - 1) != SECTION_END)
        {
            parseError(line, source.getLineNumber());
        }

        sectionName = unescape(line.substring(1, line.length() - 1).trim());
        if ((sectionName.length() == 0) && !getConfig().isUnnamedSection())
        {
            parseError(line, source.getLineNumber());
        }

        if (getConfig().isLowerCaseSection())
        {
            sectionName = sectionName.toLowerCase(Locale.getDefault());
        }

        handler.startSection(sectionName);

        return sectionName;
    }

    private void handleOptionLine(String line, IniSource source, IniHandler handler) throws InvalidIniFormatException
    {
        int idx = line.indexOf(OPERATOR);
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

        handler.handleOption(name, value);

    }

    private void parse(IniSource source, IniHandler handler) throws IOException, InvalidIniFormatException
    {
        handler.startIni();
        String sectionName = null;

        for (String line = source.readLine(); line != null; line = source.readLine())
        {

            if (line.charAt(0) == SECTION_BEGIN)
            {
                if (sectionName != null)
                {
                    handler.endSection();
                }

                sectionName = handleSectionLine(line, source, handler);

            }
            else
            {
                if (sectionName == null)
                {
                    if (getConfig().isGlobalSection())
                    {
                        sectionName = getConfig().getGlobalSectionName();
                        handler.startSection(sectionName);
                    }
                    else
                    {
                        parseError(line, source.getLineNumber());
                    }
                }

                handleOptionLine(line, source, handler);

            }
        }

        if (sectionName != null)
        {
            handler.endSection();
        }

        handler.endIni();
    }
}
