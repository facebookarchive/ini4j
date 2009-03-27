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
package org.ini4j;

import org.ini4j.Profile.Section;

import org.ini4j.spi.IniHandler;

import java.nio.charset.Charset;

public class BasicRegistry extends BasicProfile implements Registry
{
    private static final long serialVersionUID = -6432826330714504802L;
    private static final char DOUBLE_QUOTE = '"';
    protected static final Charset HEX_CHARSET = Charset.forName("UTF-16LE");
    private String _version;

    public BasicRegistry()
    {
        _version = VERSION;
    }

    @Override public String getVersion()
    {
        return _version;
    }

    @Override public void setVersion(String value)
    {
        _version = value;
    }

    @Override public Key add(String name)
    {
        return (Key) super.add(name);
    }

    @Override public Key get(Object key)
    {
        return (Key) super.get(key);
    }

    @Override public Key get(Object key, int index)
    {
        return (Key) super.get(key, index);
    }

    @Override public Key put(String key, Section value)
    {
        return (Key) super.put(key, value);
    }

    @Override public Key put(String key, Section value, int index)
    {
        return (Key) super.put(key, value, index);
    }

    @Override public Key remove(Section section)
    {
        return (Key) super.remove(section);
    }

    @Override public Key remove(Object key)
    {
        return (Key) super.remove(key);
    }

    @Override public Key remove(Object key, int index)
    {
        return (Key) super.remove(key, index);
    }

    @Override protected Key newSection(String name)
    {
        return new BasicRegistryKey(this, name);
    }

    @Override protected void store(IniHandler formatter, Section section, String option)
    {
        store(formatter, section.getComment(option));
        Type type = ((Key) section).getType(option, Type.REG_SZ);
        String rawName = option.equals(Key.DEFAULT_NAME) ? option : quote(option);
        String rawValue;

        if (section.length(option) == 0)
        {
            rawValue = null;
        }
        else
        {
            rawValue = (type == Type.REG_SZ) ? quote(section.get(option)) : raw(section, option, type);
        }

        formatter.handleOption(rawName, rawValue);
    }

    private String hexadecimal(String value)
    {
        StringBuilder buff = new StringBuilder();

        if (value != null)
        {
            byte[] bytes = value.getBytes(HEX_CHARSET);

            for (int i = 0; i < bytes.length; i++)
            {
                buff.append(Character.forDigit((bytes[i] & 0xf0) >> 4, 16));
                buff.append(Character.forDigit(bytes[i] & 0x0f, 16));
                buff.append(',');
            }

            buff.append("00,00");
        }

        return buff.toString();
    }

    private String quote(String value)
    {
        String ret = value;

        if ((value != null) && (value.length() != 0))
        {
            StringBuilder buff = new StringBuilder();

            buff.append(DOUBLE_QUOTE);
            for (int i = 0; i < value.length(); i++)
            {
                char c = value.charAt(i);

                if ((c == ESCAPE_CHAR) || (c == DOUBLE_QUOTE))
                {
                    buff.append(ESCAPE_CHAR);
                }

                buff.append(c);
            }

            buff.append(DOUBLE_QUOTE);
            ret = buff.toString();
        }

        return ret;
    }

    private String raw(Section section, String option, Type type)
    {
        StringBuilder buff = new StringBuilder();

        buff.append(type.toString());
        buff.append(Type.SEPARATOR_CHAR);
        switch (type)
        {

            case REG_EXPAND_SZ:
                buff.append(hexadecimal(section.get(option)));
                break;

            case REG_DWORD:
                buff.append(String.format("%08x", Long.parseLong(section.get(option))));
                break;

            case REG_MULTI_SZ:
                int n = section.length(option);

                for (int i = 0; i < n; i++)
                {
                    buff.append(hexadecimal(section.get(option, i)));
                    buff.append(',');
                }

                buff.append("00,00");
                break;

            default:
                buff.append(section.get(option));
                break;
        }

        return buff.toString();
    }
}
