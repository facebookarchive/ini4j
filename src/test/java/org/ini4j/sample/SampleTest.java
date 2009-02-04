package org.ini4j.sample;

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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SampleTest extends TestCase
{
    static
    {
        System.setProperty("java.util.prefs.PreferencesFactory", "org.ini4j.IniPreferencesFactory");
    }
    
    private static final String[] ARGS = {System.getProperty("basedir") + "/src/test/java/org/ini4j/sample/dwarfs.ini"};
    private static final String[] SAMPLES =
    {
        "ReadStringSample", "ReadPrimitiveSample", "WriteSample", "IniSample", "StreamSample", "DumpSample",
                "BeanSample","NoImportSample","ListenerSample"
    };
    private static final String SEPARATOR = "************";
    
    public SampleTest(String testName)
    {
        super(testName);
    }
    
    public static Test suite()
    {
        return  new TestSuite(SampleTest.class);
    }
    
    public void testSamples() throws Exception
    {
        String pkg = SampleTest.class.getPackage().getName() + '.';
        for(String name : SAMPLES)
        {
            String clazz = pkg + name;
            System.out.println(SEPARATOR + " " +  clazz + " " + SEPARATOR);
            Class.forName(clazz).getDeclaredMethod("main", String[].class).invoke((Object)null,(Object)ARGS);
        }
    }
}
