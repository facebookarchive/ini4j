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
package org.ini4j.test;

import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.Options;

import org.ini4j.sample.Dwarf;
import org.ini4j.sample.Dwarfs;

import org.ini4j.spi.IniFormatter;
import org.ini4j.spi.IniParser;

import org.junit.Assert;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.net.URL;

public class Helper
{
    private static final String RESOURCE_PREFIX = "org/ini4j/sample/";
    private static final File _sourceDir = new File(System.getProperty("basedir") + "/src/test/java/");
    private static final File _targetDir = new File(System.getProperty("basedir") + "/target");
    public static final String DWARFS_INI = RESOURCE_PREFIX + "dwarfs.ini";
    public static final String DWARFS_OPT = RESOURCE_PREFIX + "dwarfs.opt";
    public static final float DELTA = 0.00000001f;
    private static final String[] CONFIG_PROPERTIES =
        {
            Config.PROP_EMPTY_OPTION, Config.PROP_GLOBAL_SECTION, Config.PROP_GLOBAL_SECTION_NAME, Config.PROP_INCLUDE, Config.PROP_LOWER_CASE_OPTION,
            Config.PROP_LOWER_CASE_SECTION, Config.PROP_MULTI_OPTION, Config.PROP_MULTI_SECTION, Config.PROP_STRICT_OPERATOR,
            Config.PROP_UNNAMED_SECTION, Config.PROP_ESCAPE
        };
    private static final String[] FACTORY_PROPERTIES = { IniFormatter.class.getName(), IniParser.class.getName() };

    private Helper()
    {
    }

    public static File getBuildDirectory()
    {
        return _targetDir;
    }

    public static Reader getResourceReader(String path) throws Exception
    {
        return new InputStreamReader(getResourceURL(path).openStream());
    }

    public static InputStream getResourceStream(String path) throws Exception
    {
        return getResourceURL(path).openStream();
    }

    public static URL getResourceURL(String path) throws Exception
    {
        return Helper.class.getClassLoader().getResource(path);
    }

    public static File getSourceFile(String path) throws Exception
    {
        return new File(_sourceDir, path).getCanonicalFile();
    }

    public static void assertEquals(Dwarfs expected, Dwarfs actual) throws Exception
    {
        assertEquals(expected.getBashful(), actual.getBashful());
        assertEquals(expected.getDoc(), actual.getDoc());
        assertEquals(expected.getDopey(), actual.getDopey());
        assertEquals(expected.getGrumpy(), actual.getGrumpy());
        assertEquals(expected.getHappy(), actual.getHappy());
        assertEquals(expected.getSleepy(), actual.getSleepy());
        assertEquals(expected.getSneezy(), actual.getSneezy());
    }

    public static void assertEquals(Dwarf expected, Dwarf actual)
    {
        Assert.assertEquals(expected.getAge(), actual.getAge());
        Assert.assertEquals(expected.getHeight(), actual.getHeight(), DELTA);
        Assert.assertEquals(expected.getWeight(), actual.getWeight(), DELTA);
        Assert.assertEquals(expected.getHomePage().toString(), actual.getHomePage().toString());
        Assert.assertEquals(expected.getHomeDir().toString(), actual.getHomeDir().toString());
        Assert.assertEquals(expected.hasAge(), actual.hasAge());
        Assert.assertEquals(expected.hasHeight(), actual.hasHeight());
        Assert.assertEquals(expected.hasWeight(), actual.hasWeight());
        Assert.assertEquals(expected.hasHomePage(), actual.hasHomePage());
    }

    public static Ini loadDwarfsIni() throws Exception
    {
        return new Ini(Helper.class.getClassLoader().getResourceAsStream(DWARFS_INI));
    }

    public static Ini loadDwarfsIni(Config config) throws Exception
    {
        Ini ini = new Ini();

        ini.setConfig(config);
        ini.load(Helper.class.getClassLoader().getResourceAsStream(DWARFS_INI));

        return ini;
    }

    public static Options loadDwarfsOpt() throws Exception
    {
        return new Options(Helper.class.getClassLoader().getResourceAsStream(DWARFS_OPT));
    }

    public static Options loadDwarfsOpt(Config config) throws Exception
    {
        Options opt = new Options();

        opt.setConfig(config);
        opt.load(Helper.class.getClassLoader().getResourceAsStream(DWARFS_OPT));

        return opt;
    }

    public static Ini newDwarfsIni()
    {
        Ini ini = new Ini();
        Ini.Section s;

        addSection(ini, Dwarfs.PROP_BASHFUL, DwarfsData.bashful);

        //
        addSection(ini, Dwarfs.PROP_DOC, DwarfsData.doc);

        //
        s = addSection(ini, Dwarfs.PROP_DOPEY, DwarfsData.dopey);
        s.put(Dwarf.PROP_WEIGHT, DwarfsData.INI_DOPEY_WEIGHT, 0);
        s.put(Dwarf.PROP_HEIGHT, DwarfsData.INI_DOPEY_HEIGHT, 0);

        //
        s = addSection(ini, Dwarfs.PROP_GRUMPY, DwarfsData.grumpy);
        s.put(Dwarf.PROP_HEIGHT, DwarfsData.INI_GRUMPY_HEIGHT, 0);

        //
        addSection(ini, Dwarfs.PROP_HAPPY, DwarfsData.happy);

        //
        s = addSection(ini, Dwarfs.PROP_SLEEPY, DwarfsData.sleepy);
        s.put(Dwarf.PROP_HEIGHT, DwarfsData.INI_SLEEPY_HEIGHT, 0);

        //
        s = addSection(ini, Dwarfs.PROP_SNEEZY, DwarfsData.sneezy);
        s.put(Dwarf.PROP_HOME_PAGE, DwarfsData.INI_SNEEZY_HOME_PAGE, 0);

        return ini;
    }

    public static Options newDwarfsOpt()
    {
        Options opts = new Options();

        addPrefixed(opts, null, DwarfsData.dopey);
        opts.put(Dwarf.PROP_WEIGHT, DwarfsData.OPT_DOPEY_WEIGHT, 0);
        opts.put(Dwarf.PROP_HEIGHT, DwarfsData.OPT_DOPEY_HEIGHT, 0);

        //
        addPrefixed(opts, Dwarfs.PROP_BASHFUL, DwarfsData.bashful);

        //
        addPrefixed(opts, Dwarfs.PROP_DOC, DwarfsData.doc);

        //
        addPrefixed(opts, Dwarfs.PROP_DOPEY, DwarfsData.dopey);
        opts.put(Dwarfs.PROP_DOPEY + '.' + Dwarf.PROP_WEIGHT, DwarfsData.OPT_DOPEY_WEIGHT, 0);
        opts.put(Dwarfs.PROP_DOPEY + '.' + Dwarf.PROP_HEIGHT, DwarfsData.OPT_DOPEY_HEIGHT, 0);

        //
        addPrefixed(opts, Dwarfs.PROP_GRUMPY, DwarfsData.grumpy);
        opts.put(Dwarfs.PROP_GRUMPY + '.' + Dwarf.PROP_HEIGHT, DwarfsData.OPT_GRUMPY_HEIGHT, 0);

        //
        addPrefixed(opts, Dwarfs.PROP_HAPPY, DwarfsData.happy);

        //
        addPrefixed(opts, Dwarfs.PROP_SLEEPY, DwarfsData.sleepy);
        opts.put(Dwarfs.PROP_SLEEPY + '.' + Dwarf.PROP_HEIGHT, DwarfsData.OPT_SLEEPY_HEIGHT, 0);

        //
        addPrefixed(opts, Dwarfs.PROP_SNEEZY, DwarfsData.sneezy);
        opts.put(Dwarfs.PROP_SNEEZY + '.' + Dwarf.PROP_HOME_PAGE, DwarfsData.OPT_SNEEZY_HOME_PAGE, 0);

        return opts;
    }

    public static void resetConfig() throws Exception
    {
        for (String name : CONFIG_PROPERTIES)
        {
            System.clearProperty(Config.KEY_PREFIX + name);
        }

        for (String name : FACTORY_PROPERTIES)
        {
            System.clearProperty(name);
        }
    }

    private static void addPrefixed(Options opts, String name, Dwarf dwarf)
    {
        String prefix = (name == null) ? "" : (name + '.');

        opts.put(prefix + Dwarf.PROP_WEIGHT, String.valueOf(dwarf.getWeight()));
        opts.put(prefix + Dwarf.PROP_HEIGHT, String.valueOf(dwarf.getHeight()));
        opts.put(prefix + Dwarf.PROP_AGE, String.valueOf(dwarf.getAge()));
        opts.put(prefix + Dwarf.PROP_HOME_PAGE, dwarf.getHomePage().toString());
        opts.put(prefix + Dwarf.PROP_HOME_DIR, dwarf.getHomeDir());
        int[] numbers = dwarf.getFortuneNumber();

        if ((numbers != null) && (numbers.length > 0))
        {
            for (int i = 0; i < numbers.length; i++)
            {
                opts.add(prefix + Dwarf.PROP_FORTUNE_NUMBER, String.valueOf(numbers[i]));
            }
        }
    }

    private static Ini.Section addSection(Ini ini, String name, Dwarf dwarf)
    {
        Ini.Section s = ini.add(name);

        s.put(Dwarf.PROP_WEIGHT, String.valueOf(dwarf.getWeight()));
        s.put(Dwarf.PROP_HEIGHT, String.valueOf(dwarf.getHeight()));
        s.put(Dwarf.PROP_AGE, String.valueOf(dwarf.getAge()));
        s.put(Dwarf.PROP_HOME_PAGE, dwarf.getHomePage().toString());
        s.put(Dwarf.PROP_HOME_DIR, dwarf.getHomeDir());
        int[] numbers = dwarf.getFortuneNumber();

        if ((numbers != null) && (numbers.length > 0))
        {
            for (int i = 0; i < numbers.length; i++)
            {
                s.add(Dwarf.PROP_FORTUNE_NUMBER, String.valueOf(numbers[i]));
            }
        }

        return s;
    }
}
