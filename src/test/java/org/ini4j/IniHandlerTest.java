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

import org.easymock.classextension.EasyMock;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * JUnit test of IniParser class.
 */
public class IniHandlerTest
{
    private static final String DOPEY_WEIGHT = "${bashful/weight}";
    private static final String DOPEY_HEIGHT = "${doc/height}";
    private static final String GRUMPY_HEIGHT = "${dopey/height}";
    private static final String SLEEPY_HEIGHT = "${doc/height}8";
    private static final String SNEEZY_HOME_PAGE = "${happy/homePage}/~sneezy";

    @Test public void testHandler() throws Exception
    {
        IniParser parser = new IniParser();
        IniHandler handler;

        handler = newHandler();
        EasyMock.replay(handler);
        parser.parse(getClass().getClassLoader().getResourceAsStream(Helper.DWARFS_INI), handler);
        EasyMock.verify(handler);
        handler = newHandler();
        EasyMock.replay(handler);
        parser.parseXML(getClass().getClassLoader().getResourceAsStream(Helper.DWARFS_XML), handler);
        EasyMock.verify(handler);
    }

    protected IniHandler newHandler() throws Exception
    {
        IniHandler handler = EasyMock.createMock(IniHandler.class);
        Dwarfs dwarfs = Helper.newDwarfs();
        Dwarf dwarf;

        handler.startIni();
        dwarf = dwarfs.getBashful();
        handler.startSection(Dwarfs.PROP_BASHFUL);
        handler.handleOption(Dwarf.PROP_WEIGHT, String.valueOf(dwarf.getWeight()));
        handler.handleOption(Dwarf.PROP_HEIGHT, String.valueOf(dwarf.getHeight()));
        handler.handleOption(Dwarf.PROP_AGE, String.valueOf(dwarf.getAge()));
        handler.handleOption(Dwarf.PROP_HOME_PAGE, String.valueOf(dwarf.getHomePage()));
        handler.handleOption(Dwarf.PROP_HOME_DIR, String.valueOf(dwarf.getHomeDir()));
        handler.endSection();
        dwarf = dwarfs.getDoc();
        handler.startSection(Dwarfs.PROP_DOC);
        handler.handleOption(Dwarf.PROP_WEIGHT, String.valueOf(dwarf.getWeight()));
        handler.handleOption(Dwarf.PROP_HEIGHT, String.valueOf(dwarf.getHeight()));
        handler.handleOption(Dwarf.PROP_AGE, String.valueOf(dwarf.getAge()));
        handler.handleOption(Dwarf.PROP_HOME_PAGE, String.valueOf(dwarf.getHomePage()));
        handler.handleOption(Dwarf.PROP_HOME_DIR, String.valueOf(dwarf.getHomeDir()));
        handler.endSection();
        dwarf = dwarfs.getDopey();
        handler.startSection(Dwarfs.PROP_DOPEY);
        handler.handleOption(Dwarf.PROP_WEIGHT, DOPEY_WEIGHT);
        handler.handleOption(Dwarf.PROP_HEIGHT, DOPEY_HEIGHT);
        handler.handleOption(Dwarf.PROP_AGE, String.valueOf(dwarf.getAge()));
        handler.handleOption(Dwarf.PROP_HOME_PAGE, String.valueOf(dwarf.getHomePage()));
        handler.handleOption(Dwarf.PROP_HOME_DIR, String.valueOf(dwarf.getHomeDir()));
        handler.endSection();
        dwarf = dwarfs.getGrumpy();
        handler.startSection(Dwarfs.PROP_GRUMPY);
        handler.handleOption(Dwarf.PROP_WEIGHT, String.valueOf(dwarf.getWeight()));
        handler.handleOption(Dwarf.PROP_HEIGHT, GRUMPY_HEIGHT);
        handler.handleOption(Dwarf.PROP_AGE, String.valueOf(dwarf.getAge()));
        handler.handleOption(Dwarf.PROP_HOME_PAGE, String.valueOf(dwarf.getHomePage()));
        handler.handleOption(Dwarf.PROP_HOME_DIR, String.valueOf(dwarf.getHomeDir()));
        handler.endSection();
        dwarf = dwarfs.getHappy();
        handler.startSection(Dwarfs.PROP_HAPPY);
        handler.handleOption(Dwarf.PROP_WEIGHT, String.valueOf(dwarf.getWeight()));
        handler.handleOption(Dwarf.PROP_HEIGHT, String.valueOf(dwarf.getHeight()));
        handler.handleOption(Dwarf.PROP_AGE, String.valueOf(dwarf.getAge()));
        handler.handleOption(EasyMock.eq(Dwarf.PROP_HOME_PAGE), (String) EasyMock.anyObject());
        handler.handleOption(Dwarf.PROP_HOME_DIR, String.valueOf(dwarf.getHomeDir()));
        handler.endSection();
        dwarf = dwarfs.getSleepy();
        handler.startSection(Dwarfs.PROP_SLEEPY);
        handler.handleOption(Dwarf.PROP_WEIGHT, String.valueOf(dwarf.getWeight()));
        handler.handleOption(Dwarf.PROP_HEIGHT, SLEEPY_HEIGHT);
        handler.handleOption(Dwarf.PROP_AGE, String.valueOf(dwarf.getAge()));
        handler.handleOption(Dwarf.PROP_HOME_PAGE, String.valueOf(dwarf.getHomePage()));
        handler.handleOption(Dwarf.PROP_HOME_DIR, String.valueOf(dwarf.getHomeDir()));
        handler.handleOption(Dwarf.PROP_FORTUNE_NUMBER, String.valueOf(dwarf.getFortuneNumber()[0]));
        handler.endSection();
        dwarf = dwarfs.getSneezy();
        handler.startSection(Dwarfs.PROP_SNEEZY);
        handler.handleOption(Dwarf.PROP_WEIGHT, String.valueOf(dwarf.getWeight()));
        handler.handleOption(Dwarf.PROP_HEIGHT, String.valueOf(dwarf.getHeight()));
        handler.handleOption(Dwarf.PROP_AGE, String.valueOf(dwarf.getAge()));
        handler.handleOption(Dwarf.PROP_HOME_PAGE, SNEEZY_HOME_PAGE);
        handler.handleOption(Dwarf.PROP_HOME_DIR, String.valueOf(dwarf.getHomeDir()));
        handler.handleOption(Dwarf.PROP_FORTUNE_NUMBER, String.valueOf(dwarf.getFortuneNumber()[0]));
        handler.handleOption(Dwarf.PROP_FORTUNE_NUMBER, String.valueOf(dwarf.getFortuneNumber()[1]));
        handler.handleOption(Dwarf.PROP_FORTUNE_NUMBER, String.valueOf(dwarf.getFortuneNumber()[2]));
        handler.handleOption(Dwarf.PROP_FORTUNE_NUMBER, String.valueOf(dwarf.getFortuneNumber()[3]));
        handler.endSection();
        dwarf = dwarfs.getHappy();
        handler.startSection(Dwarfs.PROP_HAPPY);
        handler.handleOption(Dwarf.PROP_HOME_PAGE, String.valueOf(dwarf.getHomePage()));
        handler.endSection();
        handler.endIni();

        return handler;
    }
}
