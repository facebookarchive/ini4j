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

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.HashMap;

public class IniBundle extends HashMap<String, Ini>
{
    protected static final String OPT_LOCATION = "location";
    private Config _defaultConfig = Config.getGlobal();

    public void setDefaultConfig(Config value)
    {
        _defaultConfig = value;
    }

    public void load(URL location) throws IOException, InvalidIniFormatException
    {
        Ini bundle = new Ini(location);

        for (Ini.Section section : bundle.values())
        {
            put(section.getName(), newIni(location, section));
        }
    }

    public void load(File location) throws InvalidIniFormatException, IOException
    {
        load(location.toURI().toURL());
    }

    protected Config getDefaultConfig()
    {
        return _defaultConfig;
    }

    protected Ini newIni(URL base, Ini.Section section) throws InvalidIniFormatException, IOException
    {
        Config cfg = getDefaultConfig().clone();

        section.to(cfg);
        Ini ini = new Ini();

        ini.setConfig(cfg);
        ini.load(new URL(base, section.get(OPT_LOCATION)));

        return ini;
    }
}
