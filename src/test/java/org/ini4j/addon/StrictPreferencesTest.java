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

import java.util.NoSuchElementException;
import java.util.prefs.Preferences;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.ini4j.AbstractTestBase;
import org.ini4j.IniPreferences;

///CLOVER:OFF

/**
 * JUnit test of PreferencesWrapper class.
 */
public class StrictPreferencesTest extends AbstractTestBase
{
    public static final String DOC = "doc";
    
    public static final String OPTION = "height";
    public static final String MISSING = "no such option";
    
    /**
     * Instantiate test.
     *
     * @param testName name of the test
     */
    public StrictPreferencesTest(String testName)
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
        return new TestSuite(StrictPreferencesTest.class);
    }
    
    /**
     * Test of all get methods.
     *
     * @throws Exception on error
     */
    @SuppressWarnings("empty-statement")
    public void testGets() throws Exception
    {
	String value;
	
        Preferences root = new IniPreferences(loadDwarfs());
	
	Preferences peer = root.node(DOC);
	StrictPreferences pref = new StrictPreferences(peer);

	assertNotNull(pref.get(OPTION));
	try
	{
	    pref.get(MISSING);
	    fail();
	}
	catch(NoSuchElementException x)
	{
	    ;
	}

	try
	{
	    pref.getInt(MISSING);
	    fail();
	}
	catch(NoSuchElementException x)
	{
	    ;
	}

	try
	{
	    pref.getLong(MISSING);
	    fail();
	}
	catch(NoSuchElementException x)
	{
	    ;
	}
	
	try
	{
	    pref.getFloat(MISSING);
	    fail();
	}
	catch(NoSuchElementException x)
	{
	    ;
	}

	try
	{
	    pref.getDouble(MISSING);
	    fail();
	}
	catch(NoSuchElementException x)
	{
	    ;
	}
	
	try
	{
	    pref.getBoolean(MISSING);
	    fail();
	}
	catch(NoSuchElementException x)
	{
	    ;
	}
	
	try
	{
	    pref.getByteArray(MISSING);
	    fail();
	}
	catch(NoSuchElementException x)
	{
	    ;
	}
    }
    
}
