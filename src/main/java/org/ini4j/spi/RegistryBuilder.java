/*
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
package org.ini4j.spi;

import org.ini4j.Config;
import org.ini4j.Profile;

import org.ini4j.Registry.Key;
import org.ini4j.Registry.Type;

public class RegistryBuilder extends ProfileBuilder
{
    public RegistryBuilder(Profile profile, Config config)
    {
        super(profile, config);
    }

    @Override public void handleOption(String rawName, String rawValue)
    {
        String name = unquote(rawName);
        String value = unquote(rawValue);

        if (rawValue.charAt(0) != '"')
        {
            int idx = rawValue.indexOf(':');

            if (idx > 0)
            {
                Type type = Type.fromString(rawValue.substring(0, idx));

                if (type != null)
                {
                    ((Key) getCurrentSection()).putType(name, type);
                    value = value.substring(idx + 1);
                }
            }
        }

        super.handleOption(name, value);
    }

    protected String unquote(String value)
    {
        if (value.charAt(0) != '"')
        {
            return value;
        }

        StringBuilder buff = new StringBuilder();
        boolean escape = false;

        for (int i = 1; i < (value.length() - 1); i++)
        {
            char c = value.charAt(i);

            if (c == '\\')
            {
                if (!escape)
                {
                    escape = true;

                    continue;
                }

                escape = false;
            }

            buff.append(c);
        }

        return buff.toString();
    }
}
