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

import org.ini4j.Ini;

import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.io.FileInputStream;
import java.net.URI;

public class BeanEventSample
{
    static interface Dwarf
    {
        int getAge();
        void setAge(int age);
        
        double getWeight();
        void setWeight(double weight);
        
        double getHeight();
        void setHeight(double height) throws PropertyVetoException;
        
        void setHomePage(URI location);
        URI getHomePage();
        
        void addPropertyChangeListener(String property, PropertyChangeListener listener);
        void removePropertyChangeListener(String property, PropertyChangeListener listener);
        void addVetoableChangeListener(String property, VetoableChangeListener listener);
        void removeVetoableChangeListener(String property, VetoableChangeListener listener);
    }
    
    public static void main(String[] args) throws Exception
    {
        String filename = args.length > 0 ? args[0] : "dwarfs.ini";
        Ini ini = new Ini(new FileInputStream(filename));
        
        Dwarf sneezy = ini.get("sneezy").to(Dwarf.class);
        
        sneezy.addPropertyChangeListener("age", new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent event)
            {
                System.out.println("property " + event.getPropertyName() + " change");
                System.out.println(event.getOldValue() + " -> " + event.getNewValue());
            }
        });
        
        System.out.println("Sneezy's age: " + sneezy.getAge());
        
        sneezy.setAge(44);
        
        System.out.println("Sneezy's age: " + sneezy.getAge());
    }
    
}
