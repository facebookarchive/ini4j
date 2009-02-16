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

public class BeanSample
{
    public static void main(String[] args) throws Exception
    {
        String filename = (args.length > 0) ? args[0] : "dwarfs.ini";
        Dwarfs dwarfs = new Ini(new FileInputStream(filename)).to(Dwarfs.class);
        Dwarf happy = dwarfs.getHappy();
        Dwarf doc = dwarfs.getDoc();

        System.out.println("Happy's age: " + happy.getAge());
        doc.setAge(44);
        System.out.println("Doc's age: " + doc.getAge());
    }

    static interface Dwarf
    {
        int getAge();

        void setAge(int age);

        double getHeight();

        void setHeight(double height);

        URI getHomePage();

        void setHomePage(URI location);

        double getWeight();

        void setWeight(double weight);
    }

    static interface Dwarfs
    {
        Dwarf getBashful();

        Dwarf getDoc();

        Dwarf getDopey();

        Dwarf getGrumpy();

        Dwarf getHappy();

        Dwarf getSleepy();

        Dwarf getSneezy();
    }
}
