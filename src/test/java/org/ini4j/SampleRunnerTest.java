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

import org.ini4j.sample.BeanEventSample;
import org.ini4j.sample.BeanSample;
import org.ini4j.sample.DumpSample;
import org.ini4j.sample.FromSample;
import org.ini4j.sample.IniSample;
import org.ini4j.sample.ListenerSample;
import org.ini4j.sample.NoImportSample;
import org.ini4j.sample.PyReadSample;
import org.ini4j.sample.ReadPrimitiveSample;
import org.ini4j.sample.ReadStringSample;
import org.ini4j.sample.StreamSample;
import org.ini4j.sample.ToSample;

import org.ini4j.tutorial.BeanTutorial;
import org.ini4j.tutorial.IniTutorial;
import org.ini4j.tutorial.OptTutorial;
import org.ini4j.tutorial.PrefsTutorial;

import org.junit.BeforeClass;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.io.PrintWriter;

import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(Parameterized.class)
public class SampleRunnerTest
{
    private static final String SRC_PATH = "src/test/java";
    private static final String DOC_PATH = "target/generated-site/apt";
    private static final File _baseDir = new File(System.getProperty("basedir"));
    private static final String JAVA_SUFFIX = ".java";
    private static final String APT_SUFFIX = ".apt";
    private static File _sourceDir;
    private static File _documentDir;
    private final Class _clazz;
    private final File _sourceFile;

    public SampleRunnerTest(Class sampleClass)
    {
        _clazz = sampleClass;
        _sourceFile = new File(_sourceDir, _clazz.getName().replaceAll("\\.", "/") + JAVA_SUFFIX);
    }

    @BeforeClass public static void setUpClass() throws Exception
    {
        System.setProperty("java.util.prefs.PreferencesFactory", "org.ini4j.IniPreferencesFactory");
        _sourceDir = new File(_baseDir, SRC_PATH);
        _documentDir = new File(_baseDir, DOC_PATH);

        _documentDir.mkdirs();
    }

    @Parameters public static Collection data()
    {
        return Arrays.asList(
                new Object[][]
                {
                    { ReadStringSample.class },
                    { ReadPrimitiveSample.class },
                    { IniSample.class },
                    { StreamSample.class },
                    { DumpSample.class },
                    { NoImportSample.class },
                    { ListenerSample.class },
                    { BeanSample.class },
                    { BeanEventSample.class },
                    { FromSample.class },
                    { ToSample.class },
                    { PyReadSample.class },
                    { IniTutorial.class },
                    { BeanTutorial.class },
                    { OptTutorial.class },
                    { PrefsTutorial.class }
                });
    }

    @Test public void test() throws Exception
    {
        System.out.println("Executing " + _clazz.getName());
        PrintStream saved = System.out;
        File tmp = File.createTempFile(getClass().getSimpleName(), ".out");
        PrintStream out = new PrintStream(new FileOutputStream(tmp));

        System.setOut(out);
        try
        {
            execute();
        }
        finally
        {
            System.setOut(saved);
            out.flush();
        }

        document(_sourceFile, "//");
        if (tmp.length() > 0)
        {
            append(tmp);
        }

        tmp.delete();
    }

    private void append(File stdout) throws Exception
    {
        PrintWriter writer = new PrintWriter(new FileWriter(source2document(_sourceFile), true));

        writer.println("\n Standard output:\n\n+----+\n");
        LineNumberReader reader = new LineNumberReader(new FileReader(stdout));

        for (String line = reader.readLine(); line != null; line = reader.readLine())
        {
            writer.println(line);
        }

        writer.println("+----+\n");
        reader.close();
        writer.close();
    }

    private void document(File src, String comment) throws Exception
    {
        Pattern docPattern = Pattern.compile(String.format("^\\s*%s\\|(.*)$", comment));
        Pattern beginPattern = Pattern.compile(String.format("^\\s*%s\\{\\s*$", comment));
        Pattern endPattern = Pattern.compile(String.format("^\\s*%s\\}\\s*$", comment));
        LineNumberReader reader = new LineNumberReader(new FileReader(src));
        PrintWriter writer = new PrintWriter(new FileWriter(source2document(src)));
        boolean in = false;

        for (String line = reader.readLine(); line != null; line = reader.readLine())
        {
            if (in)
            {
                if (endPattern.matcher(line).matches())
                {
                    in = false;
                }
                else
                {
                    writer.println(line);
                }
            }
            else
            {
                if (beginPattern.matcher(line).matches())
                {
                    in = true;
                }
                else
                {
                    Matcher m = docPattern.matcher(line);

                    if (m.matches())
                    {
                        writer.println(m.group(1));
                    }
                }
            }
        }

        reader.close();
        writer.close();
    }

    @SuppressWarnings("unchecked")
    private void execute() throws Exception
    {
        Method main = _clazz.getMethod("main", String[].class);
        String[] args;

        try
        {
            File argument = new File(_sourceFile.getParentFile(), (String) _clazz.getField("FILENAME").get(null));

            document(argument, ";");
            args = new String[] { argument.getCanonicalPath() };
        }
        catch (NoSuchFieldException x)
        {
            args = new String[] {};
        }

        main.invoke(null, (Object) args);
    }

    private File source2document(File sourceFile) throws Exception
    {
        String name = sourceFile.getName();

        if (name.lastIndexOf('.') >= 0)
        {
            name = name.substring(0, name.lastIndexOf('.'));
        }

        File dir = new File(_documentDir, sourceFile.getParentFile().getName());

        dir.mkdir();

        return new File(dir, name + APT_SUFFIX);
    }
}
