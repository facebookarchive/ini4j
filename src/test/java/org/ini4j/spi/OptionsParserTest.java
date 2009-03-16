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
package org.ini4j.spi;

import org.easymock.EasyMock;

import org.ini4j.Config;
import org.ini4j.InvalidFileFormatException;

import org.ini4j.sample.Dwarf;
import org.ini4j.sample.Dwarfs;

import org.ini4j.test.DwarfsData;
import org.ini4j.test.Helper;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.StringReader;

public class OptionsParserTest
{
    private static final String CFG_EMPTY_OPTION = "option\n";
    private static final String NONAME = "=value\n";
    private static final String OPTION = "option";

    @Test(expected = InvalidFileFormatException.class)
    public void testBad() throws Exception
    {
        OptionsParser parser = new OptionsParser();
        OptionsHandler handler = EasyMock.createNiceMock(OptionsHandler.class);

        parser.parse(new ByteArrayInputStream(NONAME.getBytes()), handler);
    }

    @Test public void testEmptyOption() throws Exception
    {
        OptionsParser parser = new OptionsParser();
        OptionsHandler handler = EasyMock.createMock(OptionsHandler.class);

        handler.startOptions();
        handler.handleOption(OPTION, null);
        handler.endOptions();
        EasyMock.replay(handler);
        Config cfg = new Config();

        cfg.setEmptyOption(true);
        parser.setConfig(cfg);
        parser.parse(new StringReader(CFG_EMPTY_OPTION), handler);
        EasyMock.verify(handler);
    }

    @Test public void testNewInstance() throws Exception
    {
        Config cfg = new Config();
        OptionsParser parser = OptionsParser.newInstance();

        assertEquals(OptionsParser.class, parser.getClass());
        parser = OptionsParser.newInstance(cfg);
        assertEquals(OptionsParser.class, parser.getClass());
        assertSame(cfg, parser.getConfig());
    }

    @Test public void testParse() throws Exception
    {
        OptionsParser parser = new OptionsParser();
        OptionsHandler handler = EasyMock.createMock(OptionsHandler.class);
        Dwarf dwarf;
        String prefix;

        handler.startOptions();
        handler.handleComment((String) EasyMock.anyObject());
        handler.handleComment((String) EasyMock.anyObject());
        handler.handleComment((String) EasyMock.anyObject());
        dwarf = DwarfsData.dopey;
        handler.handleOption(Dwarf.PROP_WEIGHT, DwarfsData.DOPEY_WEIGHT.replace('/', '.'));
        handler.handleOption(Dwarf.PROP_HEIGHT, DwarfsData.DOPEY_HEIGHT.replace('/', '.'));
        handler.handleOption(Dwarf.PROP_AGE, String.valueOf(dwarf.getAge()));
        handler.handleOption(Dwarf.PROP_HOME_PAGE, String.valueOf(dwarf.getHomePage()));
        handler.handleOption(Dwarf.PROP_HOME_DIR, String.valueOf(dwarf.getHomeDir()));
        handler.handleOption(Dwarf.PROP_FORTUNE_NUMBER, "11");
        handler.handleOption(Dwarf.PROP_FORTUNE_NUMBER, "33");
        handler.handleOption(Dwarf.PROP_FORTUNE_NUMBER, "55");
//
        handler.handleComment((String) EasyMock.anyObject());

        //
        handler.handleComment(name2comment(Dwarfs.PROP_BASHFUL));
        dwarf = DwarfsData.bashful;
        prefix = Dwarfs.PROP_BASHFUL + ".";

        handler.handleOption(prefix + Dwarf.PROP_WEIGHT, String.valueOf(dwarf.getWeight()));
        handler.handleOption(prefix + Dwarf.PROP_HEIGHT, String.valueOf(dwarf.getHeight()));
        handler.handleOption(prefix + Dwarf.PROP_AGE, String.valueOf(dwarf.getAge()));
        handler.handleOption(prefix + Dwarf.PROP_HOME_PAGE, String.valueOf(dwarf.getHomePage()));
        handler.handleOption(prefix + Dwarf.PROP_HOME_DIR, String.valueOf(dwarf.getHomeDir()));
        handler.handleComment(name2comment(Dwarfs.PROP_DOC));
        dwarf = DwarfsData.doc;
        prefix = Dwarfs.PROP_DOC + ".";

        handler.handleOption(prefix + Dwarf.PROP_WEIGHT, String.valueOf(dwarf.getWeight()));
        handler.handleOption(prefix + Dwarf.PROP_HEIGHT, String.valueOf(dwarf.getHeight()));
        handler.handleOption(prefix + Dwarf.PROP_AGE, String.valueOf(dwarf.getAge()));
        handler.handleOption(prefix + Dwarf.PROP_HOME_PAGE, String.valueOf(dwarf.getHomePage()));
        handler.handleOption(prefix + Dwarf.PROP_HOME_DIR, String.valueOf(dwarf.getHomeDir()));
        handler.handleComment(name2comment(Dwarfs.PROP_DOPEY));
        dwarf = DwarfsData.dopey;
        prefix = Dwarfs.PROP_DOPEY + ".";

        handler.handleOption(prefix + Dwarf.PROP_WEIGHT, DwarfsData.DOPEY_WEIGHT.replace('/', '.'));
        handler.handleOption(prefix + Dwarf.PROP_HEIGHT, DwarfsData.DOPEY_HEIGHT.replace('/', '.'));
        handler.handleOption(prefix + Dwarf.PROP_AGE, String.valueOf(dwarf.getAge()));
        handler.handleOption(prefix + Dwarf.PROP_HOME_PAGE, String.valueOf(dwarf.getHomePage()));
        handler.handleOption(prefix + Dwarf.PROP_HOME_DIR, String.valueOf(dwarf.getHomeDir()));
        handler.handleComment(name2comment(Dwarfs.PROP_GRUMPY));
        dwarf = DwarfsData.grumpy;
        prefix = Dwarfs.PROP_GRUMPY + ".";

        handler.handleOption(prefix + Dwarf.PROP_WEIGHT, String.valueOf(dwarf.getWeight()));
        handler.handleOption(prefix + Dwarf.PROP_HEIGHT, DwarfsData.GRUMPY_HEIGHT.replace('/', '.'));
        handler.handleOption(prefix + Dwarf.PROP_AGE, String.valueOf(dwarf.getAge()));
        handler.handleOption(prefix + Dwarf.PROP_HOME_PAGE, String.valueOf(dwarf.getHomePage()));
        handler.handleOption(prefix + Dwarf.PROP_HOME_DIR, String.valueOf(dwarf.getHomeDir()));
        handler.handleComment(name2comment(Dwarfs.PROP_HAPPY));
        dwarf = DwarfsData.happy;
        prefix = Dwarfs.PROP_HAPPY + ".";

        handler.handleOption(prefix + Dwarf.PROP_WEIGHT, String.valueOf(dwarf.getWeight()));
        handler.handleOption(prefix + Dwarf.PROP_HEIGHT, String.valueOf(dwarf.getHeight()));
        handler.handleOption(prefix + Dwarf.PROP_AGE, String.valueOf(dwarf.getAge()));
        handler.handleOption(EasyMock.eq(prefix + Dwarf.PROP_HOME_PAGE), (String) EasyMock.anyObject());
        handler.handleOption(prefix + Dwarf.PROP_HOME_DIR, String.valueOf(dwarf.getHomeDir()));
        handler.handleComment(name2comment(Dwarfs.PROP_SLEEPY));
        dwarf = DwarfsData.sleepy;
        prefix = Dwarfs.PROP_SLEEPY + ".";

        handler.handleOption(prefix + Dwarf.PROP_WEIGHT, String.valueOf(dwarf.getWeight()));
        handler.handleOption(prefix + Dwarf.PROP_HEIGHT, DwarfsData.SLEEPY_HEIGHT.replace('/', '.'));
        handler.handleOption(prefix + Dwarf.PROP_AGE, String.valueOf(dwarf.getAge()));
        handler.handleOption(prefix + Dwarf.PROP_HOME_PAGE, String.valueOf(dwarf.getHomePage()));
        handler.handleOption(prefix + Dwarf.PROP_HOME_DIR, String.valueOf(dwarf.getHomeDir()));
        handler.handleOption(prefix + Dwarf.PROP_FORTUNE_NUMBER, String.valueOf(dwarf.getFortuneNumber()[0]));
        handler.handleComment(name2comment(Dwarfs.PROP_SNEEZY));
        dwarf = DwarfsData.sneezy;
        prefix = Dwarfs.PROP_SNEEZY + ".";

        handler.handleOption(prefix + Dwarf.PROP_WEIGHT, String.valueOf(dwarf.getWeight()));
        handler.handleOption(prefix + Dwarf.PROP_HEIGHT, String.valueOf(dwarf.getHeight()));
        handler.handleOption(prefix + Dwarf.PROP_AGE, String.valueOf(dwarf.getAge()));
        handler.handleOption(prefix + Dwarf.PROP_HOME_PAGE, DwarfsData.SNEEZY_HOME_PAGE.replace("/h", ".h"));
        handler.handleOption(prefix + Dwarf.PROP_HOME_DIR, String.valueOf(dwarf.getHomeDir()));
        handler.handleOption(prefix + Dwarf.PROP_FORTUNE_NUMBER, String.valueOf(dwarf.getFortuneNumber()[0]));
        handler.handleOption(prefix + Dwarf.PROP_FORTUNE_NUMBER, String.valueOf(dwarf.getFortuneNumber()[1]));
        handler.handleOption(prefix + Dwarf.PROP_FORTUNE_NUMBER, String.valueOf(dwarf.getFortuneNumber()[2]));
        handler.handleOption(prefix + Dwarf.PROP_FORTUNE_NUMBER, String.valueOf(dwarf.getFortuneNumber()[3]));
        handler.handleComment(name2comment(Dwarfs.PROP_HAPPY) + " again");
        dwarf = DwarfsData.happy;
        prefix = Dwarfs.PROP_HAPPY + ".";

        handler.handleOption(prefix + Dwarf.PROP_HOME_PAGE, String.valueOf(dwarf.getHomePage()));
        handler.handleComment("}");
        handler.endOptions();

        //
        EasyMock.replay(handler);
        parser.parse(Helper.getResourceURL(Helper.DWARFS_OPT), handler);
        EasyMock.verify(handler);
    }

    private String name2comment(String name)
    {
        StringBuilder buff = new StringBuilder(name.length() + 1);

        buff.append(' ');
        buff.append(Character.toUpperCase(name.charAt(0)));
        buff.append(name.substring(1));

        return buff.toString();
    }
}
