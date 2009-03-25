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

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class RegTest
{
    @Test public void testLoadSave() throws Exception
    {
        Reg reg = new Reg();

        reg.load(getClass().getResource("mozilla.reg"));
        File tmp = File.createTempFile(RegTest.class.getSimpleName(), ".reg");

        tmp.deleteOnExit();
        reg.store(new FileOutputStream(tmp));
        assertArrayEquals(read(getClass().getResourceAsStream("mozilla.reg")), read(new FileInputStream(tmp)));
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
