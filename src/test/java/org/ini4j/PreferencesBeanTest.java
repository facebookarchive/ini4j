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

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.prefs.Preferences;

/**
 * JUnit test of PreferencesBean class.
 */
public class PreferencesBeanTest
{

    /**
     * Test of newInstance method.
     *
     * @throws Exception on error
     */
    @Test public void testNewInstance() throws Exception
    {
        Preferences prefs = new IniPreferences(new Ini());
        DwarfExt doc = PreferencesBean.newInstance(DwarfExt.class, prefs.node(Dwarfs.PROP_DOC));

        doc.setWeight(67.4);
        assertEquals(67.4, doc.getWeight(), Helper.DELTA);
        assertTrue(doc.hasWeight());
        assertFalse(doc.hasDummy());
    }

    static interface DwarfExt extends Dwarf
    {
        boolean hasDummy();
    }
}
