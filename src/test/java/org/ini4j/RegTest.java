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

import org.ini4j.test.Helper;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.Charset;

public class RegTest
{
    @Test public void testLoadSave() throws Exception
    {
        Reg reg = new Reg(Helper.getResourceURL(Helper.TEST_REG));

        checkLoadSave(Helper.TEST_REG, reg);
    }

    public void testReadException() throws Exception
    {
        if (!isWindows("testReadException"))
        {
            try
            {
                new Reg(Reg.Hive.HKEY_CURRENT_USER.toString());
                fail("missing UnsupportedOperationException");
            }
            catch (UnsupportedOperationException x)
            {
                assert true;
            }
        }
        else
        {
            try
            {
                new Reg("no such key");
                fail("missing IOException");
            }
            catch (IOException x)
            {
                assert true;
            }
        }
    }

    @Test public void testReadWrite() throws Exception
    {
        if (!isWindows("testReadWrite"))
        {
            return;
        }

        Reg reg = Helper.loadDwarfsReg();

        checkLoadSave(Helper.DWARFS_REG, reg);
        reg.write(Helper.DWARFS_REG_PATH);
        Reg dup = new Reg(Helper.DWARFS_REG_PATH);

        checkEquals(reg.get(Helper.DWARFS_REG_PATH), dup.get(Helper.DWARFS_REG_PATH));
    }

    @Test public void testTypes() throws Exception
    {
        Reg reg = Helper.loadDwarfsReg();
        Reg.Section dwarfs = reg.get(Helper.DWARFS_REG_PATH + "\\dwarfs");

        reg.getConfig().setFileEncoding(Charset.forName("UTF-8"));
        assertNotNull(dwarfs);
        assertEquals(7, dwarfs.childrenNames().length);
    }

    private boolean isWindows()
    {
        String family = System.getProperty("os.family");

        return (family != null) && family.equals("windows");
    }

    private boolean isWindows(String testName)
    {
        boolean ret = isWindows();

        if (!ret)
        {
            System.out.println("Skipping " + getClass().getName() + '#' + testName);
        }

        return ret;
    }

    private void checkEquals(Registry.Key exp, Registry.Key act) throws Exception
    {
        assertNotNull(exp);
        assertEquals(exp.size(), act.size());
        for (String child : exp.childrenNames())
        {
            checkEquals(exp.getChild(child), act.getChild(child));
        }

        for (String name : exp.keySet())
        {
            assertEquals(exp.get(name), act.get(name));
        }
    }

    private void checkLoadSave(String path, Reg reg) throws Exception
    {
        File tmp = File.createTempFile(Reg.TMP_PREFIX, Reg.DEFAULT_SUFFIX);

        tmp.deleteOnExit();
        reg.store(new FileOutputStream(tmp));
        assertArrayEquals(read(Helper.getResourceStream(path)), read(new FileInputStream(tmp)));
    }

    private byte[] read(InputStream input) throws Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buff = new byte[81912];
        int n;

        while ((n = input.read(buff)) >= 0)
        {
            out.write(buff, 0, n);
        }

        return out.toByteArray();
    }
}
