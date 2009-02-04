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
import java.io.FileReader;

public class IniSample
{
    public static void main(String[] args) throws Exception
    {
        String filename = args.length > 0 ? args[0] : "dwarfs.ini";
        Ini ini = new Ini(new FileReader(filename));

        for(String key : ini.get("sleepy").keySet())
        {
            System.out.println("sleepy/" + key + " = " + ini.get("sleepy").fetch(key));
        }
        
    }
}
