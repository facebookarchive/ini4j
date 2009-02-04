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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

public class IniFormatter implements IniHandler
{
    public static final String SERVICE_ID = "org.ini4j.IniFormatter";
    public static final String DEFAULT_SERVICE = SERVICE_ID;
    private static final String OPERATOR = " " + IniParser.OPERATOR + " ";
    private PrintWriter output;

    public static IniFormatter newInstance(Writer out)
    {
        IniFormatter instance = newInstance();

        instance.setOutput(new PrintWriter(out));

        return instance;
    }

    public static IniFormatter newInstance(OutputStream out)
    {
        return newInstance(new OutputStreamWriter(out));
    }

    public void endIni()
    {
        getOutput().flush();
    }

    public void endSection()
    {
        getOutput().println();
    }

    public void handleOption(String optionName, String optionValue)
    {
        if (optionValue != null)
        {
            getOutput().print(escape(optionName));
            getOutput().print(OPERATOR);
            getOutput().println(escape(optionValue));
        }
    }

    @SuppressWarnings("empty-statement")
    public void startIni()
    {
        ;
    }

    public void startSection(String sectionName)
    {
        getOutput().print(IniParser.SECTION_BEGIN);
        getOutput().print(escape(sectionName));
        getOutput().println(IniParser.SECTION_END);
    }

    protected static IniFormatter newInstance()
    {
        return (IniFormatter) ServiceFinder.findService(SERVICE_ID, DEFAULT_SERVICE);
    }

    protected PrintWriter getOutput()
    {
        return output;
    }

    protected void setOutput(PrintWriter value)
    {
        output = value;
    }

    protected String escape(String input)
    {
        return Convert.escape(input);
    }
}
