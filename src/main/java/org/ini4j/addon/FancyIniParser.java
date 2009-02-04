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

package org.ini4j.addon;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Locale;
import java.util.Stack;
import org.ini4j.IniHandler;
import org.ini4j.IniParser;
import org.ini4j.InvalidIniFormatException;

public class FancyIniParser extends IniParser
{
    public static final char INCLUDE_BEGIN = '<';
    public static final char INCLUDE_END = '>';
    public static final char INCLUDE_OPTIONAL = '?';
    
    private boolean _allowEmptyOption = true;
    private boolean _allowUnnamedSection = true;
    private boolean _allowMissingSection = true;
    private String _missingSectionName = "?";
    private boolean _allowSectionCaseConversion;
    private boolean _allowOptionCaseConversion;
    private boolean _allowInclude = true;

    public synchronized void setAllowEmptyOption(boolean flag)
    {
	_allowEmptyOption = flag;
    }
    
    public synchronized boolean isAllowEmptyOption()
    {
	return _allowEmptyOption;
    }
    
    public synchronized void setAllowUnnamedSection(boolean flag)
    {
	_allowUnnamedSection = flag;
    }
    
    public synchronized boolean isAllowUnnamedSection()
    {
	return _allowUnnamedSection;
    }
    
    public synchronized void setAllowMissingSection(boolean flag)
    {
	_allowMissingSection = flag;
    }

    public synchronized boolean isAllowMissingSection()
    {
	return _allowMissingSection;
    }
    
    public synchronized void setMissingSectionName(String name)
    {
	_missingSectionName = name;
    }
    
    public synchronized String getMissingSectionName()
    {
	return _missingSectionName;
    }

    public synchronized void setAllowSectionCaseConversion(boolean flag)
    {
	_allowSectionCaseConversion = flag;
    }

    public synchronized boolean isAllowSectionCaseConversion()
    {
	return _allowSectionCaseConversion;
    }

    public synchronized void setAllowOptionCaseConversion(boolean flag)
    {
	_allowOptionCaseConversion = flag;
    }

    public synchronized boolean isAllowOptionCaseConversion()
    {
	return _allowOptionCaseConversion;
    }
    
    public synchronized boolean isAllowInclude()
    {
	return _allowInclude;
    }
    
    public synchronized void setAllowInclude(boolean flag)
    {
	_allowInclude = flag;
    }

    protected static class IniSource
    {
	protected boolean allowInclude;
	
	protected URL base;
	protected Stack<URL> bases;
	
	protected LineNumberReader reader;
	protected Stack<LineNumberReader> readers;
	
	protected IniSource(Reader input, boolean includeFlag)
	{
	    reader = new LineNumberReader(input);
	    allowInclude = includeFlag;
	}
	
	protected IniSource(URL base, boolean includeFlag) throws IOException
	{
	    this.base = base;
	    reader = new LineNumberReader(new InputStreamReader(base.openStream()));
	    allowInclude = includeFlag;
	}
	
	protected void include(URL location) throws IOException
	{
	    LineNumberReader input = new LineNumberReader(new InputStreamReader(location.openStream()));
	    
	    if ( readers == null )
	    {
		readers = new Stack<LineNumberReader>();
		bases = new Stack<URL>();
	    }
	    
	    readers.push(reader);
	    bases.push(base);
	    
	    reader = input;
	    base = location;
	}
	
	protected int getLineNumber()
	{
	    return reader.getLineNumber();
	}
	
        @SuppressWarnings("empty-statement")
	protected String readLine() throws IOException
	{
	    String line = reader.readLine();
	    
	    if ( line == null )
	    {
		if ( (readers != null) && ! readers.empty() )
		{
		    reader = readers.pop();
		    base = bases.pop();
		    line = readLine();
		}
	    }
	    else
	    {
		String buff = line.trim();
		
		if ( allowInclude && (buff.length() > 2) && (buff.charAt(0) == INCLUDE_BEGIN) && (buff.charAt(buff.length() - 1) == INCLUDE_END) )
		{
		    buff = buff.substring(1, buff.length()-1).trim();
		    
		    boolean optional = buff.charAt(0) == INCLUDE_OPTIONAL;
		    
		    if ( optional )
		    {
			buff = buff.substring(1).trim();
		    }
		    
		    URL loc = base == null ? new URL(buff) : new URL(base, buff);
		    
		    if ( optional )
		    {
			try
			{
			    include(loc);
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
		        include(loc);
		        line = readLine();
		    }
		}
	    }
	    
	    return line;
	}
    }
    
    @Override
    public void parse(Reader input, IniHandler handler) throws IOException, InvalidIniFormatException
    {
        parse(new IniSource(input, isAllowInclude()), handler);
    }

    @Override
    public void parse(URL input, IniHandler handler) throws IOException, InvalidIniFormatException
    {
        parse(new IniSource(input, isAllowInclude()), handler);
    }
    
    protected void parse(IniSource source, IniHandler handler) throws IOException, InvalidIniFormatException
    {
        handler.startIni();
        
        String sectionName = null;
        
        for (String line = source.readLine(); line != null; line = source.readLine())
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
                    parseError(line, source.getLineNumber());
                }

                sectionName = unescape(line.substring(1, line.length()-1).trim());
                
                if ( (sectionName.length() == 0) && ! isAllowUnnamedSection() )
                {
                    parseError(line, source.getLineNumber());
                }

		if ( isAllowSectionCaseConversion() )
		{
		    sectionName = sectionName.toLowerCase(Locale.getDefault());
		}
		
                handler.startSection(sectionName);
            }
            else
            {
                if ( sectionName == null )
                {
		    if ( isAllowMissingSection() )
		    {
			sectionName = getMissingSectionName();
			handler.startSection(sectionName);
		    }
		    else
		    {
                        parseError(line, source.getLineNumber());
		    }
                }
                
                int idx = line.indexOf(OPERATOR);
                
		String name = null;
		String value = null;
		
                if ( idx < 0 )
                {
		    if ( isAllowEmptyOption() )
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
                    value = unescape(line.substring(idx+1)).trim();
		}
                
                if ( name.length() == 0)
                {
                    parseError(line, source.getLineNumber());
                }

		if ( isAllowOptionCaseConversion() )
		{
		    name = name.toLowerCase(Locale.getDefault());
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
}
