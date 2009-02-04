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

import java.util.prefs.Preferences;
import junit.framework.Test;
import junit.framework.TestSuite;

///CLOVER:OFF

/**
 * JUnit test of PreferencesBean class.
 */
public class PreferencesBeanTest extends AbstractTestBase
{
    /**
     * Instantiate test.
     *
     * @param testName name of the test
     */
    public PreferencesBeanTest(String testName)
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
        return new TestSuite(PreferencesBeanTest.class);
    }

    static interface DwarfExt extends Dwarf
    {
        boolean hasDummy();
    }
    
    /**
     * Test of newInstance method.
     *
     * @throws Exception on error
     */
    public void testNewInstance() throws Exception
    {
        Preferences prefs = new IniPreferences(new Ini());
        DwarfExt doc = PreferencesBean.newInstance(DwarfExt.class, prefs.node("doc"));
        
        doc.setWeight(67.4);
        assertEquals(67.4, doc.getWeight());
        
        assertTrue(doc.hasWeight());
        assertFalse(doc.hasDummy());
    }
}
