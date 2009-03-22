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

import org.ini4j.test.DwarfsData;
import org.ini4j.test.Helper;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import javax.naming.Name;

public class BasicOptionTreeTest
{
    private BasicOptionTree _tree;

    @Before public void setUp()
    {
        _tree = new BasicOptionTree();
    }

    @Test public void testLookup()
    {
        OptionTree exp = _tree.add("foo").add("bar");

        assertSame(exp, _tree.lookup("foo/bar"));
        assertSame(exp, _tree.lookup(_tree.name("foo", "bar")));
        assertNull(_tree.lookup("no/such/path"));
        assertNull(_tree.lookup(_tree.name("foo", "no", "such", "path")));
    }

    @Test public void testName()
    {
        Name name;

        name = _tree.name("foo/bar");
        assertEquals(2, name.size());
        assertEquals(name, _tree.name("foo", "bar"));
        name = _tree.name("one");
        assertEquals(1, name.size());
        name = _tree.name("foo\\bar");
        assertEquals(1, name.size());
        name = _tree.name("foo", "bar", "biz", "", "org");
        assertEquals(5, name.size());
        assertEquals("foo/bar/biz//org", name.toString());
    }

    @Test public void testOptions()
    {
        OptionMap exp = _tree.add("foo").add("bar").options();

        assertSame(exp, _tree.options("foo/bar"));
        assertSame(exp, _tree.options(_tree.name("foo", "bar")));
        assertNull(_tree.options("no/such/path"));
        assertNull(_tree.options(_tree.name("foo", "no", "such", "path")));
    }

    @Test public void testResolve() throws Exception
    {
        OptionTree dwarfs = _tree.add("dwarfs");

        Helper.addDwarf(dwarfs, DwarfsData.happy);
        OptionTree doc = Helper.addDwarf(dwarfs, DwarfsData.doc);
        StringBuilder buffer;
        String input;

        // other sections's value
        input = "${dwarfs/happy/weight}";
        buffer = new StringBuilder(input);

        _tree.resolve(buffer, doc);
        assertEquals(String.valueOf(DwarfsData.happy.weight), buffer.toString());

        // other sections's value
        input = "${dwarfs/happy/weight}";
        buffer = new StringBuilder(input);

        ((BasicOptionTree.OptionValuesImpl) dwarfs.options()).resolve(buffer);
        assertEquals(String.valueOf(DwarfsData.happy.weight), buffer.toString());

        // same sections's value
        input = "${height}";
        buffer = new StringBuilder(input);

        _tree.resolve(buffer, doc);
        assertEquals(String.valueOf(DwarfsData.doc.height), buffer.toString());

        // system property
        input = "${@prop/user.home}";
        buffer = new StringBuilder(input);

        _tree.resolve(buffer, doc);
        assertEquals(System.getProperty("user.home"), buffer.toString());

        // system environment
        input = "${@env/PATH}";
        buffer = new StringBuilder(input);
        try
        {
            _tree.resolve(buffer, doc);
            assertEquals(System.getenv("PATH"), buffer.toString());
        }
        catch (Error e)
        {
            // retroweaver + JDK 1.4 throws Error on getenv
        }

        // unknown variable
        input = "${no such name}";
        buffer = new StringBuilder(input);

        _tree.resolve(buffer, doc);
        assertEquals(input, buffer.toString());

        // unknown section's unknown variable
        input = "${no such section/no such name}";
        buffer = new StringBuilder(input);

        _tree.resolve(buffer, doc);
        assertEquals(input, buffer.toString());

        // other section's unknown variable
        input = "${happy/no such name}";
        buffer = new StringBuilder(input);

        _tree.resolve(buffer, doc);
        assertEquals(input, buffer.toString());

        // small input
        input = "${";
        buffer = new StringBuilder(input);

        _tree.resolve(buffer, doc);
        assertEquals(input, buffer.toString());

        // incorrect references
        input = "${dwarfs/doc/weight";
        buffer = new StringBuilder(input);

        _tree.resolve(buffer, doc);
        assertEquals(input, buffer.toString());

        // empty references
        input = "jim${}";
        buffer = new StringBuilder(input);

        _tree.resolve(buffer, doc);
        assertEquals(input, buffer.toString());

        // escaped references
        input = "${dwarfs/happy/weight}";
        buffer = new StringBuilder(input);

        _tree.resolve(buffer, doc);
        assertEquals("" + DwarfsData.happy.weight, buffer.toString());
        input = "\\" + input;
        buffer = new StringBuilder(input);

        _tree.resolve(buffer, doc);
        assertEquals(input, buffer.toString());
    }
}
