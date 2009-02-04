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

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;

///CLOVER:OFF
import junit.framework.Test;
import junit.framework.TestSuite;
import org.ini4j.AbstractTestBase;
import org.ini4j.Ini;
import org.ini4j.IniHandler;
import org.ini4j.IniParser;
import org.ini4j.InvalidIniFormatException;

/**
 * JUnit test of IniParser class.
 */
public class FancyIniParserTest extends AbstractTestBase
{
    private static final String[] _extraIni =
    {
        "[section]\noption\n",
        "[]\noption=value\n",
        "option=value\n"
    };

    private static final String[] _badIni =
    {
        "[section\noption=value\n",
        "[section]\n=value\n"
    };
    
    private static final String MIXEDCASE ="[SectioN]\n\nOptioN=ValuE\n";
    private static final String MORESECTIONS ="[Section]\noption=value\n\n[second]\noption=value\n";
    private static final String UNNAMED = "option=value\n";
    private static final String ANONYMOUS = "?";
    private static final String EMPTY = "";
    
    private static final String INCLUDE = "org/ini4j/addon/dwarfs-include.ini";
    private static final String NESTED = "org/ini4j/addon/dwarfs-nested.ini";

    /**
     * Instantiate test.
     *
     * @param testName name of the test
     */
    public FancyIniParserTest(String testName)
    {
        super(testName);
    }
    
    /**
     * Create test suite.
     *
     * @return new test suite
     */
    public static Test suite()
    {
        return new TestSuite(FancyIniParserTest.class);
    }

    /**
     * Test of parse method.
     *
     * @throws Exception on error
     */
    @SuppressWarnings("empty-statement")
    public void testParse() throws Exception
    {
        FancyIniParser parser = new FancyIniParser();
        IniHandler handler = newBean(IniHandler.class);

	// add-on extension should work by default
        for(String s : _extraIni)
        {
            parser.parse(new ByteArrayInputStream(s.getBytes()), handler);
	}

	// disable add-on features and expect error
	parser.setAllowEmptyOption(false);
	parser.setAllowMissingSection(false);
	parser.setAllowUnnamedSection(false);

        for(String s : _extraIni)
        {
            try
            {
                parser.parse(new ByteArrayInputStream(s.getBytes()), handler);
                fail("expected InvalidIniFormatException: " + s);
            }
            catch (InvalidIniFormatException x)
            {
                ;
            }
        }

        for(String s : _badIni)
        {
            try
            {
                parser.parse(new ByteArrayInputStream(s.getBytes()), handler);
                fail("expected InvalidIniFormatException: " + s);
            }
            catch (InvalidIniFormatException x)
            {
                ;
            }
        }
	
	parser.parse(new ByteArrayInputStream(EMPTY.getBytes()), handler);
	parser.parse(new ByteArrayInputStream(MORESECTIONS.getBytes()), handler);

	parser.setMissingSectionName(ANONYMOUS);
	assertEquals(ANONYMOUS, parser.getMissingSectionName());
	
	System.setProperty(IniParser.SERVICE_ID, FancyIniParser.class.getName());
	Ini ini = new Ini(new ByteArrayInputStream(UNNAMED.getBytes()));
	
	assertNotNull(ini);
	assertNotNull(ini.get(ANONYMOUS));
	assertEquals(1, ini.size());	
    }
    
    @SuppressWarnings("empty-statement")
    public void testInclude() throws Exception
    {
	System.setProperty(IniParser.SERVICE_ID, FancyIniParser.class.getName());

	Ini ini = new Ini(getClass().getClassLoader().getResource(INCLUDE));
        doTestDwarfs(ini.to(Dwarfs.class));
	
	try
	{
	    ini = new Ini(getClass().getClassLoader().getResourceAsStream(INCLUDE));
	    fail();
	}
	catch (MalformedURLException x)
	{
	    ;
	}
	
	ini = new Ini(getClass().getClassLoader().getResource(NESTED));
        doTestDwarfs(ini.to(Dwarfs.class));
	
	FancyIniParser parser = (FancyIniParser)IniParser.newInstance();
	
	assertTrue(parser.isAllowInclude());
	parser.setAllowInclude(false);
	assertFalse(parser.isAllowInclude());
    }
    
    public void testConvertCase() throws Exception
    {
	class Handler implements IniHandler
	{
	    boolean sectionOK;
	    boolean optionOK;
	    
            @SuppressWarnings("empty-statement")
            @Override
	    public void startIni()
	    {
		;
	    }
	    
            @Override
            @SuppressWarnings("empty-statement")
	    public void endIni()
	    {
		;
	    }
	    
            @Override
	    public void handleOption(String name, String value)
	    {
		assertEquals(name.toLowerCase(), name);
		assertFalse(value.equals(value.toLowerCase()));
		optionOK = true;
	    }
	    
            @Override
	    public void startSection(String sectionName)
	    {
		assertEquals(sectionName.toLowerCase(), sectionName);
		sectionOK = true;
	    }
	    
            @Override
            @SuppressWarnings("empty-statement")
	    public void endSection()
	    {
		;
	    }
	}
	
	FancyIniParser parser = new FancyIniParser();
	parser.setAllowSectionCaseConversion(true);
	parser.setAllowOptionCaseConversion(true);
	
	Handler handler = new Handler();
	parser.parse(new ByteArrayInputStream(MIXEDCASE.getBytes()), handler);
	
	assertTrue(handler.sectionOK);
	assertTrue(handler.optionOK);
    }
    
    /**
     * Test of newInstance method.
     *
     * @throws Exception on error
     */
    public void testNewInstance() throws Exception
    {
	System.setProperty(IniParser.SERVICE_ID, FancyIniParser.class.getName());
	FancyIniParser parser = (FancyIniParser)IniParser.newInstance();
        assertNotNull(parser);
    }
}
