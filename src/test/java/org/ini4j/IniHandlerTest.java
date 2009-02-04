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

import junit.framework.Test;
import junit.framework.TestSuite;
import org.easymock.classextension.EasyMock;

/**
 * JUnit test of IniParser class.
 */
public class IniHandlerTest extends AbstractTestBase
{
    private static final String DOPEY_WEIGHT = "${bashful/weight}";
    private static final String DOPEY_HEIGHT = "${doc/height}";
    
    private static final String GRUMPY_HEIGHT = "${dopey/height}";
    
    private static final String SLEEPY_HEIGHT = "${doc/height}8";
    
    private static final String SNEEZY_HOME_PAGE = "${happy/homePage}/~sneezy";
    
    /**
     * Instantiate test.
     *
     * @param testName name of the test
     */    public IniHandlerTest(String name)
    {
        super(name);
    }
     
    /**
     * Create test suite.
     *
     * @return new test suite
     */
    public static Test suite()
    {
        return new TestSuite(IniHandlerTest.class);
    }
    
    protected IniHandler newHandler() throws Exception
    {
        IniHandler handler = EasyMock.createMock(IniHandler.class);
        
        Dwarfs dwarfs = newDwarfs();
        Dwarf dwarf;
        
        handler.startIni();
        
        dwarf = dwarfs.getBashful();
        handler.startSection(Dwarfs.PROP_BASHFUL);
        handler.handleOption(Dwarf.PROP_WEIGHT, String.valueOf(dwarf.getWeight()));
        handler.handleOption(Dwarf.PROP_HEIGHT, String.valueOf(dwarf.getHeight()));
        handler.handleOption(Dwarf.PROP_AGE, String.valueOf(dwarf.getAge()));
        handler.handleOption(Dwarf.PROP_HOME_PAGE, String.valueOf(dwarf.getHomePage()));
        handler.endSection();

        dwarf = dwarfs.getDoc();
        handler.startSection(Dwarfs.PROP_DOC);
        handler.handleOption(Dwarf.PROP_WEIGHT, String.valueOf(dwarf.getWeight()));
        handler.handleOption(Dwarf.PROP_HEIGHT, String.valueOf(dwarf.getHeight()));
        handler.handleOption(Dwarf.PROP_AGE, String.valueOf(dwarf.getAge()));
        handler.handleOption(Dwarf.PROP_HOME_PAGE, String.valueOf(dwarf.getHomePage()));
        handler.endSection();
        
        dwarf = dwarfs.getDopey();
        handler.startSection(Dwarfs.PROP_DOPEY);
        handler.handleOption(Dwarf.PROP_WEIGHT, DOPEY_WEIGHT);
        handler.handleOption(Dwarf.PROP_HEIGHT, DOPEY_HEIGHT);
        handler.handleOption(Dwarf.PROP_AGE, String.valueOf(dwarf.getAge()));
        handler.handleOption(Dwarf.PROP_HOME_PAGE, String.valueOf(dwarf.getHomePage()));
        handler.endSection();
        
        dwarf = dwarfs.getGrumpy();
        handler.startSection(Dwarfs.PROP_GRUMPY);
        handler.handleOption(Dwarf.PROP_WEIGHT, String.valueOf(dwarf.getWeight()));
        handler.handleOption(Dwarf.PROP_HEIGHT, GRUMPY_HEIGHT);
        handler.handleOption(Dwarf.PROP_AGE, String.valueOf(dwarf.getAge()));
        handler.handleOption(Dwarf.PROP_HOME_PAGE, String.valueOf(dwarf.getHomePage()));
        handler.endSection();
        
        dwarf = dwarfs.getHappy();
        handler.startSection(Dwarfs.PROP_HAPPY);
        handler.handleOption(Dwarf.PROP_WEIGHT, String.valueOf(dwarf.getWeight()));
        handler.handleOption(Dwarf.PROP_HEIGHT, String.valueOf(dwarf.getHeight()));
        handler.handleOption(Dwarf.PROP_AGE, String.valueOf(dwarf.getAge()));
        handler.handleOption(EasyMock.eq(Dwarf.PROP_HOME_PAGE), (String)EasyMock.anyObject());
        handler.endSection();
        
        dwarf = dwarfs.getSleepy();
        handler.startSection(Dwarfs.PROP_SLEEPY);
        handler.handleOption(Dwarf.PROP_WEIGHT, String.valueOf(dwarf.getWeight()));
        handler.handleOption(Dwarf.PROP_HEIGHT, SLEEPY_HEIGHT);
        handler.handleOption(Dwarf.PROP_AGE, String.valueOf(dwarf.getAge()));
        handler.handleOption(Dwarf.PROP_HOME_PAGE, String.valueOf(dwarf.getHomePage()));
        handler.endSection();
        
        dwarf = dwarfs.getSneezy();
        handler.startSection(Dwarfs.PROP_SNEEZY);
        handler.handleOption(Dwarf.PROP_WEIGHT, String.valueOf(dwarf.getWeight()));
        handler.handleOption(Dwarf.PROP_HEIGHT, String.valueOf(dwarf.getHeight()));
        handler.handleOption(Dwarf.PROP_AGE, String.valueOf(dwarf.getAge()));
        handler.handleOption(Dwarf.PROP_HOME_PAGE, SNEEZY_HOME_PAGE);
        handler.endSection();
        
        dwarf = dwarfs.getHappy();
        handler.startSection(Dwarfs.PROP_HAPPY);
        handler.handleOption(Dwarf.PROP_HOME_PAGE, String.valueOf(dwarf.getHomePage()));
        handler.endSection();
        
        handler.endIni();
        
        return handler;        
    }
    
    public void testHandler() throws Exception
    {
        IniParser parser = new IniParser();
        IniHandler handler;
        
        handler = newHandler();
        EasyMock.replay(handler);
        parser.parse(getClass().getClassLoader().getResourceAsStream(DWARFS_INI), handler);
        EasyMock.verify(handler);
        
        handler = newHandler();
        EasyMock.replay(handler);
        parser.parseXML(getClass().getClassLoader().getResourceAsStream(DWARFS_XML), handler);
        EasyMock.verify(handler);
    }
}
