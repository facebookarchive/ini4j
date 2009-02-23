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
package org.ini4j.sample;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.FileOutputStream;
import java.io.PrintStream;

import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class SampleTest
{

    static
    {
        System.setProperty("java.util.prefs.PreferencesFactory", "org.ini4j.IniPreferencesFactory");
    }

    private static final String SAMPLE_DIR = System.getProperty("basedir") + "/src/test/java/org/ini4j/sample/";
    private static final String OUT_DIR = System.getProperty("basedir") + "/target/site/sample/";
    private final Class _sampleClass;

    public SampleTest(Class sampleClass)
    {
        _sampleClass = sampleClass;
    }

    @Parameters public static Collection data()
    {
        return Arrays.asList(
                new Object[][]
                {
                    { ReadStringSample.class },
                    { ReadPrimitiveSample.class },
                    { WriteSample.class },
                    { IniSample.class },
                    { StreamSample.class },
                    { DumpSample.class },
                    { NoImportSample.class },
                    { ListenerSample.class },
                    { FromSample.class },
                    { ToSample.class },
                    { PyReadSample.class }
                });
    }

    @SuppressWarnings("unchecked")
    @Test public void testMain() throws Exception
    {
        Method main = _sampleClass.getDeclaredMethod("main", String[].class);
        String[] args;

        try
        {
            String filename = (String) _sampleClass.getDeclaredField("FILENAME").get(null);

            args = new String[] { SAMPLE_DIR + filename };
        }
        catch (NoSuchFieldException x)
        {
            args = new String[] {};
        }

        System.out.println("Executing " + _sampleClass.getName());
        PrintStream saved = System.out;
        PrintStream out = new PrintStream(new FileOutputStream(OUT_DIR + _sampleClass.getSimpleName() + ".txt"));

        System.setOut(out);
        try
        {
            main.invoke((Object) null, (Object) args);
        }
        finally
        {
            System.setOut(saved);
            out.flush();
        }
    }
}
