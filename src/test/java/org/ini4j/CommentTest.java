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
import org.ini4j.sample.Dwarfs;

import org.ini4j.test.DwarfsData;
import org.ini4j.test.Helper;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

public class CommentTest
{
    private static final String NOTICE_FILE = "NOTICE.txt";
    private static final String COMMENT_ONLY = "# first line\n# second line\n";
    private static final String COMMENT_ONLY_VALUE = " first line\n second line";
    private static final String OPTIONS_ONE_HEADER = COMMENT_ONLY + "\n\nkey=value\n";
    private static final String INI_ONE_HEADER = COMMENT_ONLY + "\n\n[section]\nkey=value\n";

    @Test public void testCommentOnly() throws Exception
    {
        Ini ini = new Ini(new StringReader(COMMENT_ONLY));
        Options opt = new Options(new StringReader(COMMENT_ONLY));

        assertEquals(COMMENT_ONLY_VALUE, ini.getComment());
        assertEquals(COMMENT_ONLY_VALUE, opt.getComment());
    }

    @Test public void testOneHeaderOnly() throws Exception
    {
        Ini ini = new Ini(new StringReader(INI_ONE_HEADER));
        Options opt = new Options(new StringReader(OPTIONS_ONE_HEADER));

        assertEquals(COMMENT_ONLY_VALUE, ini.getComment());
        assertEquals(COMMENT_ONLY_VALUE, opt.getComment());
    }

    @Test public void testSamples() throws Exception
    {
        String header = readHeader();
        Ini ini = Helper.loadDwarfsIni();
        Options opt = Helper.loadDwarfsOpt();

        assertEquals(header, ini.getComment());
        assertEquals(header, opt.getComment());
        for (String name : DwarfsData.dwarfNames)
        {
            String exp = " " + name;

            if (name.equals(Dwarfs.PROP_HAPPY))
            {
                exp += " again";
            }

            assertEquals(exp, ini.getComment(name).toLowerCase());
        }

        for (String name : DwarfsData.dwarfNames)
        {
            String exp = " " + name;

            assertEquals(exp, opt.getComment(name + '.' + Dwarf.PROP_WEIGHT).toLowerCase());
        }
    }

    private String readHeader() throws IOException
    {
        StringBuilder buff = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(NOTICE_FILE));

        for (String line = reader.readLine(); line != null; line = reader.readLine())
        {
            if (line.length() != 0)
            {
                buff.append(' ');
                buff.append(line);
            }

            buff.append('\n');
        }

        reader.close();
        if (buff.charAt(buff.length() - 1) == '\n')
        {
            buff.deleteCharAt(buff.length() - 1);
        }

        return buff.toString();
    }
}
