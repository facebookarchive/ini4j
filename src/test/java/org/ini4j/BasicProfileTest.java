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

import org.ini4j.test.DwarfsData;
import org.ini4j.test.DwarfsData.DwarfData;
import org.ini4j.test.Helper;

import static org.junit.Assert.*;

import org.junit.Test;

import java.net.URI;

public class BasicProfileTest
{
    private static final String SECTION = "section";

    @Test public void testAddPut()
    {
        Profile prof = new BasicProfile();

        prof.add(SECTION, Dwarf.PROP_AGE, DwarfsData.sneezy.age);
        prof.put(SECTION, Dwarf.PROP_HEIGHT, DwarfsData.sneezy.height);
        prof.add(SECTION, Dwarf.PROP_HOME_DIR, DwarfsData.sneezy.homeDir);
        prof.add(SECTION, Dwarf.PROP_WEIGHT, DwarfsData.sneezy.weight);
        prof.put(SECTION, Dwarf.PROP_HOME_PAGE, null);
        prof.put(SECTION, Dwarf.PROP_HOME_PAGE, DwarfsData.sneezy.homePage);
        prof.add(SECTION, Dwarf.PROP_FORTUNE_NUMBER, DwarfsData.sneezy.fortuneNumber[0]);
        prof.add(SECTION, Dwarf.PROP_FORTUNE_NUMBER, DwarfsData.sneezy.fortuneNumber[1]);
        prof.add(SECTION, Dwarf.PROP_FORTUNE_NUMBER, DwarfsData.sneezy.fortuneNumber[2]);
        prof.add(SECTION, Dwarf.PROP_FORTUNE_NUMBER, DwarfsData.sneezy.fortuneNumber[3]);
        Helper.assertEquals(DwarfsData.sneezy, prof.get(SECTION).as(Dwarf.class));
        assertNotNull(prof.remove(SECTION, Dwarf.PROP_FORTUNE_NUMBER));
        assertEquals(0, prof.get(SECTION).length(Dwarf.PROP_FORTUNE_NUMBER));
        assertNotNull(prof.remove(SECTION));
        assertNull(prof.remove(SECTION, Dwarf.PROP_FORTUNE_NUMBER));
    }

    @Test public void testFromToAs() throws Exception
    {
        BasicProfile prof = new BasicProfile();

        Helper.addDwarfs(prof);
        fromToAs(prof, DwarfsData.bashful);
        fromToAs(prof, DwarfsData.doc);
        fromToAs(prof, DwarfsData.dopey);
        fromToAs(prof, DwarfsData.grumpy);
        fromToAs(prof, DwarfsData.happy);
        fromToAs(prof, DwarfsData.sleepy);
        fromToAs(prof, DwarfsData.sneezy);

        //
        DwarfsRW dwarfs = prof.as(DwarfsRW.class);

        Helper.assertEquals(DwarfsData.bashful, dwarfs.getBashful());
        Helper.assertEquals(DwarfsData.doc, dwarfs.getDoc());
        Helper.assertEquals(DwarfsData.dopey, dwarfs.getDopey());
        Helper.assertEquals(DwarfsData.grumpy, dwarfs.getGrumpy());
        Helper.assertEquals(DwarfsData.happy, dwarfs.getHappy());
        Helper.assertEquals(DwarfsData.sleepy, dwarfs.getSleepy());
        Helper.assertEquals(DwarfsData.sneezy, dwarfs.getSneezy());

        //
        prof.remove(Dwarfs.PROP_BASHFUL);
//        assertNull(dwarfs.getBashful());
        dwarfs.setBashful(DwarfsData.dopey);
        // XXX     Helper.assertEquals(DwarfsData.dopey, dwarfs.getBashful());
    }

    @Test public void testIniGetFetch()
    {
        Profile prof = new BasicProfile();
        Profile.Section sec = Helper.addDwarf(prof, DwarfsData.dopey);

        Helper.addDwarf(prof, DwarfsData.bashful);
        Helper.addDwarf(prof, DwarfsData.doc);
        assertEquals(sec.get(Dwarf.PROP_AGE), prof.get(Dwarfs.PROP_DOPEY, Dwarf.PROP_AGE));
        assertEquals(DwarfsData.dopey.age, (int) prof.get(Dwarfs.PROP_DOPEY, Dwarf.PROP_AGE, int.class));
        assertEquals(sec.get(Dwarf.PROP_WEIGHT), prof.get(Dwarfs.PROP_DOPEY, Dwarf.PROP_WEIGHT));
        assertEquals(DwarfsData.dopey.weight, prof.fetch(Dwarfs.PROP_DOPEY, Dwarf.PROP_WEIGHT, double.class), Helper.DELTA);
        assertEquals(sec.fetch(Dwarf.PROP_HEIGHT), prof.fetch(Dwarfs.PROP_DOPEY, Dwarf.PROP_HEIGHT));
        assertEquals(DwarfsData.dopey.weight, prof.fetch(Dwarfs.PROP_DOPEY, Dwarf.PROP_WEIGHT, double.class), Helper.DELTA);
        assertEquals(sec.fetch(Dwarf.PROP_HOME_PAGE), prof.fetch(Dwarfs.PROP_DOPEY, Dwarf.PROP_HOME_PAGE));
        assertEquals(DwarfsData.dopey.homePage, prof.fetch(Dwarfs.PROP_DOPEY, Dwarf.PROP_HOME_PAGE, URI.class));

        // nulls
        assertNull(prof.get(SECTION, Dwarf.PROP_AGE));
        assertEquals(0, (int) prof.get(SECTION, Dwarf.PROP_AGE, int.class));
        assertNull(prof.get(SECTION, Dwarf.PROP_WEIGHT));
        assertEquals(0.0, prof.fetch(SECTION, Dwarf.PROP_WEIGHT, double.class), Helper.DELTA);
        assertNull(prof.fetch(SECTION, Dwarf.PROP_HEIGHT));
        assertEquals(0.0, prof.fetch(SECTION, Dwarf.PROP_WEIGHT, double.class), Helper.DELTA);
        assertNull(prof.fetch(SECTION, Dwarf.PROP_HOME_PAGE));
        assertNull(prof.fetch(SECTION, Dwarf.PROP_HOME_PAGE, URI.class));
    }

    @Test public void testResolve() throws Exception
    {
        BasicProfile prof = new BasicProfile();

        Helper.addDwarf(prof, DwarfsData.happy);
        Profile.Section doc = Helper.addDwarf(prof, DwarfsData.doc);
        StringBuilder buffer;
        String input;

        // other sections's value
        input = "${happy/weight}";
        buffer = new StringBuilder(input);

        prof.resolve(buffer, doc);
        assertEquals(String.valueOf(DwarfsData.happy.weight), buffer.toString());

        // same sections's value
        input = "${height}";
        buffer = new StringBuilder(input);

        prof.resolve(buffer, doc);
        assertEquals(String.valueOf(DwarfsData.doc.height), buffer.toString());

        // system property
        input = "${@prop/user.home}";
        buffer = new StringBuilder(input);

        prof.resolve(buffer, doc);
        assertEquals(System.getProperty("user.home"), buffer.toString());

        // system environment
        input = "${@env/PATH}";
        buffer = new StringBuilder(input);
        try
        {
            prof.resolve(buffer, doc);
            assertEquals(System.getenv("PATH"), buffer.toString());
        }
        catch (Error e)
        {
            // retroweaver + JDK 1.4 throws Error on getenv
        }

        // unknown variable
        input = "${no such name}";
        buffer = new StringBuilder(input);

        prof.resolve(buffer, doc);
        assertEquals(input, buffer.toString());

        // unknown section's unknown variable
        input = "${no such section/no such name}";
        buffer = new StringBuilder(input);

        prof.resolve(buffer, doc);
        assertEquals(input, buffer.toString());

        // other section's unknown variable
        input = "${happy/no such name}";
        buffer = new StringBuilder(input);

        prof.resolve(buffer, doc);
        assertEquals(input, buffer.toString());

        // small input
        input = "${";
        buffer = new StringBuilder(input);

        prof.resolve(buffer, doc);
        assertEquals(input, buffer.toString());

        // incorrect references
        input = "${doc/weight";
        buffer = new StringBuilder(input);

        prof.resolve(buffer, doc);
        assertEquals(input, buffer.toString());

        // empty references
        input = "jim${}";
        buffer = new StringBuilder(input);

        prof.resolve(buffer, doc);
        assertEquals(input, buffer.toString());

        // escaped references
        input = "${happy/weight}";
        buffer = new StringBuilder(input);

        prof.resolve(buffer, doc);
        assertEquals("" + DwarfsData.happy.weight, buffer.toString());
        input = "\\" + input;
        buffer = new StringBuilder(input);

        assertEquals(input, buffer.toString());
    }

    private void fromToAs(BasicProfile prof, DwarfData dwarf)
    {
        Profile.Section sec = prof.get(dwarf.name);
        Profile.Section dup = prof.new SectionImpl(SECTION);
        DwarfBean bean = new DwarfBean();

        sec.to(bean);
        Helper.assertEquals(dwarf, bean);
        dup.from(bean);
        bean = new DwarfBean();
        dup.to(bean);
        Helper.assertEquals(dwarf, bean);
        Dwarf proxy = dup.as(Dwarf.class);

        Helper.assertEquals(dwarf, proxy);
        dup.clear();
        sec.to(proxy);
        Helper.assertEquals(dwarf, proxy);
        prof.remove(dup);
    }

    public static interface DwarfsRW extends Dwarfs
    {
        void setBashful(Dwarf value);
    }
}
