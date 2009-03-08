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

import org.ini4j.sample.Dwarf;
import org.ini4j.sample.DwarfBean;
import org.ini4j.sample.Dwarfs;
import org.ini4j.sample.DwarfsBean;

import org.ini4j.spi.IniFormatter;

import org.junit.Assert;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.net.URI;
import java.net.URL;

import java.util.prefs.Preferences;

public class Helper
{
    private static final String RESOURCE_PREFIX = "org/ini4j/sample/";
    private static final File _sourceDir = new File(System.getProperty("basedir") + "/src/test/java/");
    private static final File _targetDir = new File(System.getProperty("basedir") + "/target");
    public static final String DWARFS_INI = RESOURCE_PREFIX + "dwarfs.ini";
    public static final String DWARFS_OPT = RESOURCE_PREFIX + "dwarfs.opt";
    public static final String DWARFS_XML = RESOURCE_PREFIX + "dwarfs.xml";
    public static final float DELTA = 0.00000001f;
    private static final String[] CONFIG_PROPERTIES =
        { Config.PROP_EMPTY_OPTION, Config.PROP_GLOBAL_SECTION, Config.PROP_GLOBAL_SECTION_NAME, Config.PROP_INCLUDE, Config.PROP_LOWER_CASE_OPTION, Config.PROP_LOWER_CASE_SECTION, Config.PROP_MULTI_OPTION, Config.PROP_MULTI_SECTION, Config.PROP_STRICT_OPERATOR, Config.PROP_UNNAMED_SECTION, Config.PROP_ESCAPE };
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

    public static void assertEquals(Dwarf expected, Dwarf actual)
    {
        Assert.assertEquals(expected.getAge(), actual.getAge());
        Assert.assertEquals(expected.getHeight(), actual.getHeight(), DELTA);
        Assert.assertEquals(expected.getWeight(), actual.getWeight(), DELTA);
        Assert.assertEquals(expected.getHomePage().toString(), actual.getHomePage().toString());
    }

    public static void assertEquals(Dwarf expected, OptionMap actual)
    {
        Assert.assertEquals("" + expected.getAge(), actual.fetch(Dwarf.PROP_AGE));
        Assert.assertEquals("" + expected.getHeight(), actual.fetch(Dwarf.PROP_HEIGHT));
        Assert.assertEquals("" + expected.getWeight(), actual.fetch(Dwarf.PROP_WEIGHT));
        Assert.assertEquals("" + expected.getHomePage(), actual.fetch(Dwarf.PROP_HOME_PAGE));
        Assert.assertEquals("" + expected.getHomeDir(), actual.fetch(Dwarf.PROP_HOME_DIR));
    }

    public static void assertEquals(Dwarf expected, Preferences actual)
    {
        Assert.assertEquals("" + expected.getAge(), actual.get(Dwarf.PROP_AGE, null));
        Assert.assertEquals("" + expected.getHeight(), actual.get(Dwarf.PROP_HEIGHT, null));
        Assert.assertEquals("" + expected.getWeight(), actual.get(Dwarf.PROP_WEIGHT, null));
        Assert.assertEquals("" + expected.getHomePage(), actual.get(Dwarf.PROP_HOME_PAGE, null));
        Assert.assertEquals("" + expected.getHomeDir(), actual.get(Dwarf.PROP_HOME_DIR, null));
    }

    public static void doTestDwarfs(Dwarfs dwarfs) throws Exception
    {
        Dwarf d;

        d = dwarfs.getBashful();
        assertHasProperties(d);
        Assert.assertEquals(45.7, d.getWeight(), DELTA);
        Assert.assertEquals(98.8, d.getHeight(), DELTA);
        Assert.assertEquals(67, d.getAge());
        Assert.assertEquals("http://snowwhite.tale/~bashful", d.getHomePage().toString());
        d = dwarfs.getDoc();
        assertHasProperties(d);
        Assert.assertEquals(49.5, d.getWeight(), DELTA);
        Assert.assertEquals(87.7, d.getHeight(), DELTA);
        Assert.assertEquals(63, d.getAge());
        Assert.assertEquals("http://doc.dwarfs", d.getHomePage().toString());
        d = dwarfs.getDopey();
        assertHasProperties(d);
        Assert.assertEquals(dwarfs.getBashful().getWeight(), d.getWeight(), DELTA);
        Assert.assertEquals(dwarfs.getDoc().getHeight(), d.getHeight(), DELTA);
        Assert.assertEquals(23, d.getAge());
        Assert.assertEquals("http://dopey.snowwhite.tale/", d.getHomePage().toString());
        d = dwarfs.getGrumpy();
        assertHasProperties(d);
        Assert.assertEquals(65.3, d.getWeight(), DELTA);
        Assert.assertEquals(dwarfs.getDopey().getHeight(), d.getHeight(), DELTA);
        Assert.assertEquals(76, d.getAge());
        Assert.assertEquals("http://snowwhite.tale/~grumpy/", d.getHomePage().toString());
        d = dwarfs.getHappy();
        assertHasProperties(d);
        Assert.assertEquals(56.4, d.getWeight(), DELTA);
        Assert.assertEquals(77.66, d.getHeight(), DELTA);
        Assert.assertEquals(99, d.getAge());
        Assert.assertEquals("http://happy.smurf", d.getHomePage().toString());
        d = dwarfs.getSleepy();
        assertHasProperties(d);
        Assert.assertEquals(76.11, d.getWeight(), DELTA);
        Assert.assertEquals(87.78, d.getHeight(), DELTA);
        Assert.assertEquals(121, d.getAge());
        Assert.assertEquals("http://snowwhite.tale/~sleepy", d.getHomePage().toString());
        d = dwarfs.getSneezy();
        assertHasProperties(d);
        Assert.assertEquals(69.7, d.getWeight(), DELTA);
        Assert.assertEquals(76.88, d.getHeight(), DELTA);
        Assert.assertEquals(64, d.getAge());
        Assert.assertEquals(dwarfs.getHappy().getHomePage().toString() + "/~sneezy", d.getHomePage().toString());
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

    public static Dwarf newBashful() throws Exception
    {
        Dwarf d = new DwarfBean();

        d.setWeight(45.7);
        d.setHeight(98.8);
        d.setAge(67);
        d.setHomePage(new URI("http://snowwhite.tale/~bashful"));
        d.setHomeDir("/home/bashful");

        return d;
    }

    public static Dwarf newDoc() throws Exception
    {
        Dwarf d = new DwarfBean();

        d.setWeight(49.5);
        d.setHeight(87.7);
        d.setAge(63);
        d.setHomePage(new URI("http://doc.dwarfs"));
        d.setHomeDir("c:Documents and Settingsdoc");

        return d;
    }

    public static Dwarf newDopey() throws Exception
    {
        Dwarf d = new DwarfBean();

        d.setWeight(45.7);
        d.setHeight(87.7);
        d.setAge(23);
        d.setHomePage(new URI("http://dopey.snowwhite.tale/"));
        d.setHomeDir("c:\\Documents and Settings\\dopey");

        return d;
    }

    public static Dwarf newDwarf()
    {
        return new DwarfBean();
    }

    public static Dwarfs newDwarfs() throws Exception
    {
        DwarfsBean dwarfs = new DwarfsBean();

        dwarfs.setBashful(newBashful());
        dwarfs.setDoc(newDoc());
        dwarfs.setDopey(newDopey());
        dwarfs.setGrumpy(newGrumpy());
        dwarfs.setHappy(newHappy());
        dwarfs.setSleepy(newSleepy());
        dwarfs.setSneezy(newSneezy());

        return dwarfs;
    }

    public static Ini newDwarfsIni()
    {
        Ini ini = new Ini();
        Ini.Section s;

        s = ini.add(Dwarfs.PROP_BASHFUL);
        s.put(Dwarf.PROP_WEIGHT, "45.7");
        s.put(Dwarf.PROP_HEIGHT, "98.8");
        s.put(Dwarf.PROP_AGE, "67");
        s.put(Dwarf.PROP_HOME_PAGE, "http://snowwhite.tale/~bashful");
        s.put(Dwarf.PROP_HOME_DIR, "/home/bashful");
        s = ini.add(Dwarfs.PROP_DOC);
        s.put(Dwarf.PROP_WEIGHT, "49.5");
        s.put(Dwarf.PROP_HEIGHT, "87.7");
        s.put(Dwarf.PROP_AGE, "63");
        s.put(Dwarf.PROP_HOME_PAGE, "http://doc.dwarfs");
        s.put(Dwarf.PROP_HOME_DIR, "c:Documents and Settingsdoc");
        s = ini.add(Dwarfs.PROP_DOPEY);
        s.put(Dwarf.PROP_WEIGHT, "${bashful/weight}");
        s.put(Dwarf.PROP_HEIGHT, "${doc/height}");
        s.put(Dwarf.PROP_AGE, "23");
        s.put(Dwarf.PROP_HOME_PAGE, "http://dopey.snowwhite.tale/");
        s.put(Dwarf.PROP_HOME_DIR, "c:\\Documents and Settings\\dopey");
        s = ini.add(Dwarfs.PROP_GRUMPY);
        s.put(Dwarf.PROP_WEIGHT, "65.3");
        s.put(Dwarf.PROP_HEIGHT, "${dopey/height}");
        s.put(Dwarf.PROP_AGE, "76");
        s.put(Dwarf.PROP_HOME_PAGE, "http://snowwhite.tale/~grumpy/");
        s.put(Dwarf.PROP_HOME_DIR, "/home/grumpy");
        s = ini.add(Dwarfs.PROP_HAPPY);
        s.put(Dwarf.PROP_WEIGHT, "56.4");
        s.put(Dwarf.PROP_HEIGHT, "77.66");
        s.put(Dwarf.PROP_AGE, "99");
        s.put(Dwarf.PROP_HOME_PAGE, "dummy");
        s.put(Dwarf.PROP_HOME_DIR, "/home/happy");
        s.add(Dwarf.PROP_HOME_PAGE, "http://happy.smurf");
        s = ini.add(Dwarfs.PROP_SLEEPY);
        s.put(Dwarf.PROP_WEIGHT, "76.11");
        s.put(Dwarf.PROP_HEIGHT, "${doc/height}8");
        s.put(Dwarf.PROP_AGE, "121");
        s.put(Dwarf.PROP_HOME_PAGE, "http://snowwhite.tale/~sleepy");
        s.put(Dwarf.PROP_HOME_DIR, "/home/sleepy");
        s.put(Dwarf.PROP_FORTUNE_NUMBER, "99");
        s = ini.add(Dwarfs.PROP_SNEEZY);
        s.put(Dwarf.PROP_WEIGHT, "69.7");
        s.put(Dwarf.PROP_HEIGHT, "76.88");
        s.put(Dwarf.PROP_AGE, "64");
        s.put(Dwarf.PROP_HOME_PAGE, "${happy/homePage}/~sneezy");
        s.put(Dwarf.PROP_HOME_DIR, "/home/sneezy");
        s.put(Dwarf.PROP_FORTUNE_NUMBER, "11");
        s.put(Dwarf.PROP_FORTUNE_NUMBER, "22");
        s.put(Dwarf.PROP_FORTUNE_NUMBER, "33");
        s.put(Dwarf.PROP_FORTUNE_NUMBER, "44");

        return ini;
    }

    public static Dwarf newGrumpy() throws Exception
    {
        Dwarf d = new DwarfBean();

        d.setWeight(65.3);
        d.setHeight(87.7);
        d.setAge(76);
        d.setHomePage(new URI("http://snowwhite.tale/~grumpy/"));
        d.setHomeDir("/home/grumpy");

        return d;
    }

    public static Dwarf newHappy() throws Exception
    {
        Dwarf d = new DwarfBean();

        d.setWeight(56.4);
        d.setHeight(77.66);
        d.setAge(99);
        d.setHomePage(new URI("http://happy.smurf"));
        d.setHomeDir("/home/happy");

        return d;
    }

    public static Dwarf newSleepy() throws Exception
    {
        Dwarf d = new DwarfBean();

        d.setWeight(76.11);
        d.setHeight(87.78);
        d.setAge(121);
        d.setHomePage(new URI("http://snowwhite.tale/~sleepy"));
        d.setHomeDir("/home/sleepy");
        d.setFortuneNumber(new int[] { 99 });

        return d;
    }

    public static Dwarf newSneezy() throws Exception
    {
        Dwarf d = new DwarfBean();

        d.setWeight(69.7);
        d.setHeight(76.88);
        d.setAge(64);
        d.setHomePage(new URI("http://happy.smurf" + "/~sneezy"));
        d.setHomeDir("/home/sneezy");
        d.setFortuneNumber(new int[] { 11, 22, 33, 44 });

        return d;
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

    private static void assertHasProperties(Dwarf dwarf)
    {
        Assert.assertTrue(dwarf.hasWeight());
        Assert.assertTrue(dwarf.hasHeight());
        Assert.assertTrue(dwarf.hasAge());
        Assert.assertTrue(dwarf.hasHomePage());
    }
}
