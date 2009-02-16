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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

public class XMLFormatter implements IniHandler
{
    private PrintWriter output;

    public static XMLFormatter newInstance(Writer out)
    {
        XMLFormatter instance = newInstance();

        instance.setOutput(new PrintWriter(out));

        return instance;
    }

    public static XMLFormatter newInstance(OutputStream out)
    {
        return newInstance(new OutputStreamWriter(out));
    }

    public void endIni()
    {
        getOutput().println("</ini>");
        getOutput().flush();
    }

    public void endSection()
    {
        getOutput().println(" </section>");
    }

    public void handleOption(String optionName, String optionValue)
    {
        getOutput().print("  <option key='");
        getOutput().print(optionName);
        getOutput().print("' value='");
        getOutput().print(optionValue);
        getOutput().println("'/>");
    }

    public void startIni()
    {
        getOutput().println("<ini version='1.0'>");
    }

    public void startSection(String sectionName)
    {
        getOutput().print(" <section key='");
        getOutput().print(sectionName);
        getOutput().println("'>");
    }

    protected static XMLFormatter newInstance()
    {
        return ServiceFinder.findService(XMLFormatter.class);
    }

    protected PrintWriter getOutput()
    {
        return output;
    }

    protected void setOutput(PrintWriter value)
    {
        output = value;
    }
}
