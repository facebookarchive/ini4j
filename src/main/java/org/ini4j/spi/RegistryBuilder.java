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
import org.ini4j.Registry;

import org.ini4j.Registry.Key;
import org.ini4j.Registry.Type;

import java.nio.charset.Charset;

import java.util.Arrays;

public class RegistryBuilder extends ProfileBuilder
{
    private static final char DOUBLE_QUOTE = '"';
    protected static final Charset HEX_CHARSET = Charset.forName("UTF-16LE");

    public RegistryBuilder(Profile profile, Config config)
    {
        super(profile, config);
    }

    @Override public void handleOption(String rawName, String rawValue)
    {
        String name = unquote(rawName);
        String value = unquote(rawValue);

        if (rawValue.charAt(0) == DOUBLE_QUOTE)
        {
            super.handleOption(name, value);
        }
        else
        {
            Type type = parseType(value);

            if (type == null)
            {
                super.handleOption(name, value);
            }
            else
            {
                handleOption(name, value.substring(type.toString().length() + 1), type);
            }
        }
    }

    private byte[] binary(String value)
    {
        byte[] bytes = new byte[value.length()];
        int idx = 0;
        int shift = 4;

        for (int i = 0; i < value.length(); i++)
        {
            char c = value.charAt(i);

            if (Character.isWhitespace(c))
            {
                continue;
            }

            if (c == ',')
            {
                idx++;
                shift = 4;
            }
            else
            {
                int digit = Character.digit(c, 16);

                if (digit >= 0)
                {
                    bytes[idx] |= digit << shift;
                    shift = 0;
                }
            }
        }

        return Arrays.copyOfRange(bytes, 0, idx + 1);
    }

    private void handleOption(String name, String value, Type type)
    {
        ((Key) getCurrentSection()).putType(name, type);
        String converted = value;

        switch (type)
        {

            case REG_EXPAND_SZ:
            case REG_MULTI_SZ:
                byte[] bytes = binary(value);

                converted = new String(bytes, 0, bytes.length - 2, HEX_CHARSET);
                break;

            case REG_DWORD:
                converted = String.valueOf(Long.parseLong(value, 16));
                break;
        }

        if (type == Type.REG_MULTI_SZ)
        {
            int start = 0;
            int len = converted.length();

            for (int end = converted.indexOf(0, start); end >= 0; end = converted.indexOf(0, start))
            {
                super.handleOption(name, converted.substring(start, end));
                start = end + 1;
                if (start >= len)
                {
                    break;
                }
            }
        }
        else
        {
            super.handleOption(name, converted);
        }
    }

    private Type parseType(String value)
    {
        int idx = value.indexOf(Registry.TYPE_SEPARATOR);

        return (idx < 0) ? Type.REG_SZ : Type.fromString(value.substring(0, idx));
    }

    private String unquote(String value)
    {
        if (value.charAt(0) != DOUBLE_QUOTE)
        {
            return value;
        }

        StringBuilder buff = new StringBuilder();
        boolean escape = false;

        for (int i = 1; i < (value.length() - 1); i++)
        {
            char c = value.charAt(i);

            if (c == Registry.ESCAPE_CHAR)
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
