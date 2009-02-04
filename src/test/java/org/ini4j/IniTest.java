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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import junit.framework.Test;
import junit.framework.TestSuite;

///CLOVER:OFF
import org.mortbay.util.ByteArrayOutputStream2;

/**
 * JUnit test of Ini class.
 */
public class IniTest extends AbstractTestBase
{
    private static final String UNICODE_STRING = "áÁéÉíÍóÓöÖőŐúÚüÜűŰ-ÄÖÜäöü";
    
    /**
     * Instantiate test.
     *
     * @param testName name of the test
     */
    public IniTest(String testName)
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
        return new TestSuite(IniTest.class);
    }

    /**
     * Test of load method.
     *
     * @throws Exception on error
     */
    public void testLoad() throws Exception
    {
        Ini ini = loadDwarfs();
        doTestDwarfs(ini.to(Dwarfs.class));
        
        ini = new Ini(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(DWARFS_INI)));
	doTestDwarfs(ini.to(Dwarfs.class));
	
	ini = new Ini(getClass().getClassLoader().getResource(DWARFS_INI));
	doTestDwarfs(ini.to(Dwarfs.class));
    }
    
    /**
     * Test of loadFromXML method.
     *
     * @throws Exception on error
     */
    public void testLoadFromXML() throws Exception
    {
        Ini ini = new Ini();
        
        ini.loadFromXML(getClass().getClassLoader().getResourceAsStream(DWARFS_XML));
        doTestDwarfs(ini.to(Dwarfs.class));

        ini.loadFromXML(getClass().getClassLoader().getResource(DWARFS_XML));
        doTestDwarfs(ini.to(Dwarfs.class));
    }
    
    /**
     * Test of store method.
     *
     * @throws Exception on error
     */
    public void testStore() throws Exception
    {
        Ini ini = loadDwarfs();
        
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ini.store(buffer);
        
        Ini dup = new Ini();
        dup.load( new ByteArrayInputStream(buffer.toByteArray()));
        
        doTestDwarfs(dup.to(Dwarfs.class));
        
        buffer = new ByteArrayOutputStream();
        ini.store(new OutputStreamWriter(buffer));
        
        dup = new Ini();
        dup.load(new InputStreamReader(new ByteArrayInputStream(buffer.toByteArray())));
        
        doTestDwarfs(dup.to(Dwarfs.class));
        
    }

    /**
     * Test of storeToXML method.
     *
     * @throws Exception on error
     */
    public void testStoreToXML() throws Exception
    {
        Ini ini = loadDwarfs();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ini.storeToXML(buffer);

        Ini dup = new Ini();
        dup.loadFromXML(new ByteArrayInputStream(buffer.toByteArray()));

        doTestDwarfs(dup.to(Dwarfs.class));

        buffer = new ByteArrayOutputStream();
        ini.storeToXML(new OutputStreamWriter(buffer));

        dup = new Ini();
        dup.loadFromXML(new InputStreamReader(new ByteArrayInputStream(buffer.toByteArray())));
        
        doTestDwarfs(dup.to(Dwarfs.class));
    }
    
    /**
     * Test of resolve method.
     *
     * @throws Exception on error
     */
    public void testResolve() throws Exception
    {
        Ini ini = loadDwarfs();
        Ini.Section doc = ini.get("doc");
        Dwarfs dwarfs = ini.to(Dwarfs.class);
        
        StringBuilder buffer;
        String input;
        
        // other sections's value
        input = "${happy/weight}";
        buffer = new StringBuilder(input);
        ini.resolve(buffer, doc);
        assertEquals("" + dwarfs.getHappy().getWeight(), buffer.toString());
        
        // same sections's value
        input = "${height}";
        buffer = new StringBuilder(input);
        ini.resolve(buffer, doc);
        assertEquals("" + dwarfs.getDoc().getHeight(), buffer.toString());

        // system property
        input = "${@prop/user.home}";
        buffer = new StringBuilder(input);
        ini.resolve(buffer, doc);
        assertEquals(System.getProperty("user.home"), buffer.toString());

        // system environment
        input = "${@env/path}";
        buffer = new StringBuilder(input);
	try
	{
            ini.resolve(buffer, doc);
            assertEquals(System.getenv("path"), buffer.toString());
	}
	catch (Error e)
	{
	    // retroweaver + JDK 1.4 throws Error on getenv
	}

        // unknown variable
        input = "${no such name}";
        buffer = new StringBuilder(input);
        ini.resolve(buffer, doc);
        assertEquals(input, buffer.toString());

        // unknown section's unknown variable
        input = "${no such section/no such name}";
        buffer = new StringBuilder(input);
        ini.resolve(buffer, doc);
        assertEquals(input, buffer.toString());
        
        // other section's unknown variable
        input = "${happy/no such name}";
        buffer = new StringBuilder(input);
        ini.resolve(buffer, doc);
        assertEquals(input, buffer.toString());

        // small input
        input = "${";
        buffer = new StringBuilder(input);
        ini.resolve(buffer, doc);
        assertEquals(input, buffer.toString());

        // incorrect references
        input = "${doc/weight";
        buffer = new StringBuilder(input);
        ini.resolve(buffer, doc);
        assertEquals(input, buffer.toString());

        // empty references
        input = "jim${}";
        buffer = new StringBuilder(input);
        ini.resolve(buffer, doc);
        assertEquals(input, buffer.toString());
        
        // escaped references
        input = "${happy/weight}";
        buffer = new StringBuilder(input);
        ini.resolve(buffer, doc);
        assertEquals("" + dwarfs.getHappy().getWeight(), buffer.toString());
        input = "\\" + input;
        buffer = new StringBuilder(input);
        assertEquals(input, buffer.toString());
    }
    
    /**
     * Test of remove method.
     *
     * @throws Exception on error
     */
    public void testRemove() throws Exception
    {
        Ini ini = loadDwarfs();
        ini.remove(ini.get("doc"));
        assertNull(ini.get("doc"));
    }

    static interface Tale extends Dwarfs
    {
        static interface Snowwhite
        {
            String getEmail();
            void setEmail(String email);
        }
        
        Snowwhite getSnowwhite();
        void setSnowwhite(Snowwhite s);
        boolean hasSnowwhite();
    }

    /**
     * Test of bean related methods.
     *
     * @throws Exception on error
     */
    public void testBeanInterface() throws Exception
    {
        Ini ini = loadDwarfs();
        Ini.Section sec = ini.get("doc");
        
        Dwarfs dwarfs = ini.to(Dwarfs.class);
        Dwarf doc = sec.to(Dwarf.class);
        
        // repeated bean conversion should return same object
        assertSame(dwarfs, ini.to(Dwarfs.class));
        assertSame(doc, sec.to(Dwarf.class));
        
        // unknown properties
        Tale tale = ini.to(Tale.class);
        Tale.Snowwhite sw = sec.to(Tale.Snowwhite.class);
        
        assertNull(tale.getSnowwhite());
        assertFalse(tale.hasSnowwhite());
        assertNull(sw.getEmail());
        
        String email = "snowwhite@tale";
        sw.setEmail(email);
        assertSame(email, sw.getEmail());
        
        // set section property is invalid operation
        tale.setSnowwhite(sw);
    }
    
    public void testUnicode() throws Exception
    {
        Ini orig = new Ini();
        Ini.Section bashful = orig.add(Dwarfs.PROP_BASHFUL);
        
        bashful.put(Dwarf.PROP_HOME_PAGE, UNICODE_STRING);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        orig.store(out);
        
        Ini saved = new Ini(new ByteArrayInputStream(out.toByteArray()));
        Ini.Section bashfulSaved = saved.get(Dwarfs.PROP_BASHFUL);
        
        assertEquals(bashful.get(Dwarf.PROP_HOME_PAGE), bashfulSaved.get(Dwarf.PROP_HOME_PAGE));
    }
    
}
