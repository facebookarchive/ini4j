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

import org.ini4j.Ini;

import java.io.FileInputStream;

import java.net.URI;

public class ToSample
{
    public static final String FILENAME = "dwarfs.ini";

    public static void main(String[] args) throws Exception
    {
        String filename = (args.length > 0) ? args[0] : FILENAME;
        Ini ini = new Ini(new FileInputStream(filename));
        DwarfBean happy = new DwarfBean();

        ini.get("happy").to(happy);
        System.out.println("Happy's age: " + happy.getAge());
        System.out.println("Happy's homePage: " + happy.getHomePage());
    }

    public static class DwarfBean
    {
        private int _age;
        private double _height;
        private URI _homePage;
        private double _weight;

        public int getAge()
        {
            return _age;
        }

        public void setAge(int value)
        {
            _age = value;
        }

        public double getHeight()
        {
            return _height;
        }

        public void setHeight(double value)
        {
            _height = value;
        }

        public URI getHomePage()
        {
            return _homePage;
        }

        public void setHomePage(URI value)
        {
            _homePage = value;
        }

        public double getWeight()
        {
            return _weight;
        }

        public void setWeight(double value)
        {
            _weight = value;
        }
    }
}
