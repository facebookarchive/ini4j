/*
 * Copyright 2005 [ini4j] Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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

import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class IniParser
{
    public static final String COMMENTS = ";#";
    public static final char OPERATOR = '=';
    public static final char SECTION_BEGIN = '[';
    public static final char SECTION_END = ']';
    
    public static final String SERVICE_ID = "org.ini4j.IniParser";
    public static final String DEFAULT_SERVICE = SERVICE_ID;

    public static IniParser newInstance()
    {
        return (IniParser) ServiceFinder.findService(SERVICE_ID, DEFAULT_SERVICE);
    }
    
    public void parse(InputStream input, IniHandler handler) throws IOException, InvalidIniFormatException
    {
        parse(new InputStreamReader(input), handler);
    }

    public void parse(Reader input, IniHandler handler) throws IOException, InvalidIniFormatException
    {
        LineNumberReader reader = new LineNumberReader(input);
        
        handler.startIni();
        
        String sectionName = null;
        
        for (String line = reader.readLine(); line != null; line = reader.readLine())
        {
            line = line.trim();

            if ( (line.length() == 0) || (COMMENTS.indexOf(line.charAt(0)) >= 0))
            {
                continue;
            }
            
            if ( line.charAt(0) == SECTION_BEGIN )
            {
                if ( sectionName != null )
                {
                    handler.endSection();
                }
                
                if ( line.charAt(line.length()-1) != SECTION_END )
                {
                    parseError(line, reader.getLineNumber());
                }

                sectionName = unescape(line.substring(1, line.length()-1).trim());
                
                if ( sectionName.length() == 0 )
                {
                    parseError(line, reader.getLineNumber());
                }

                handler.startSection(sectionName);
            }
            else
            {
                if ( sectionName == null )
                {
                    parseError(line, reader.getLineNumber());
                }
                
                int idx = line.indexOf(OPERATOR);
                
                if ( idx <= 0 )
                {
                    parseError(line, reader.getLineNumber());
                }

                String name = unescape(line.substring(0, idx)).trim();
                String value = unescape(line.substring(idx+1)).trim();
                
                if ( name.length() == 0)
                {
                    parseError(line, reader.getLineNumber());
                }

                handler.handleOption(name, value);
            }
        }

        if ( sectionName != null  )
        {
            handler.endSection();
        }

        handler.endIni();
    }
    
    public void parse(URL input, IniHandler handler) throws IOException, InvalidIniFormatException
    {
        InputStream stream = input.openStream();
        try
        {
	    parse(stream, handler);
        }
        finally
        {
            stream.close();
        }
    }

    public void parseXML(InputStream input, IniHandler handler) throws IOException, InvalidIniFormatException
    {
        parseXML(new InputStreamReader(input), handler);
    }

    public void parseXML(Reader input, final IniHandler handler) throws IOException, InvalidIniFormatException
    {
        class XML2Ini extends DefaultHandler
        {
            static final String TAG_SECTION = "section";
            static final String TAG_OPTION = "option";
            static final String TAG_INI = "ini";
            static final String ATTR_KEY = "key";
            static final String ATTR_VALUE = "value";
            static final String ATTR_VERSION = "version";
            
            static final String CURRENT_VERSION = "1.0";
            
            @Override
            public void startElement(String uri, String localName, String qname, Attributes attrs) throws SAXException
            {
                String key = attrs.getValue(ATTR_KEY);

                if ( qname.equals(TAG_INI) )
                {
                    String ver = attrs.getValue(ATTR_VERSION);
                    
                    if ( (ver == null) || ! ver.equals(CURRENT_VERSION))
                    {
                        throw new SAXException("Missing or invalid 'version' attribute");
                    }
                    
                    handler.startIni();
                }
                else
                {
                    if ( key == null )
                    {
                        throw new SAXException("missing '" + ATTR_KEY + "' attribute");
                    }
                    
                    if ( qname.equals(TAG_SECTION) )
                    {
                        handler.startSection(key);
                    }
                    else if ( qname.equals(TAG_OPTION) )
                    {
                        handler.handleOption(key, attrs.getValue(ATTR_VALUE));
                    }
                    else
                    {
                        throw new SAXException("Invalid element: " + qname);
                    }
                }
            }
            
            @Override
            public void endElement(String uri, String localName, String qname) throws SAXException
            {
                if ( qname.equals(TAG_SECTION) )
                {
                    handler.endSection();
                }
                else if ( qname.equals(TAG_INI))
                {
                    handler.endIni();
                }
            }
        }
        
        XML2Ini xml2ini = new XML2Ini();
        
        try
        {
            SAXParserFactory.newInstance().newSAXParser().parse(new InputSource(input), xml2ini);
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
    
    protected String unescape(String line)
    {
	return Convert.unescape(line);
    }
    
    protected void parseError(String line, int lineNumber) throws InvalidIniFormatException
    {
        throw new InvalidIniFormatException("parse error (at line: " + lineNumber + "): " + line);
    }
}
