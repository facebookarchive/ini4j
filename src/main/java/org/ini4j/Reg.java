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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import java.net.URL;

public class Reg extends Wini
{
    private static final long serialVersionUID = -1485602876922985912L;

    public static enum Type
    {
        REG_NONE(0, "hex(0)"),
        REG_SZ(1, ""),
        REG_EXPAND_SZ(2, "hex(2)"),
        REG_BINARY(3, "hex"),
        REG_DWORD(4, "dword"),
        REG_DWORD_LITTLE_ENDIAN(4, "dword"),
        REG_DWORD_BIG_ENDIAN(5, "hex(5)"),
        REG_LINK(6, "hex(6)"),
        REG_MULTI_SZ(7, "hex(7)"),
        REG_RESOURCE_LIST(8, "hex(8)"),
        REG_FULL_RESOURCE_DESCRIPTOR(9, "hex(9)"),
        REG_RESOURCE_REQUIREMENTS_LIST(10, "hex(a)"),
        REG_QWORD(11, "hex(b)"),
        REQ_QWORD_LITTLE_ENDIAN(11, "hex(b)");
        private final int _code;
        private final String _prefix;

        private Type(int code, String prefix)
        {
            _code = code;
            _prefix = prefix;
        }

        public int getCode()
        {
            return _code;
        }

        public String getPrefix()
        {
            return _prefix;
        }
    }

    public Reg()
    {
        getConfig().setStripOptionNameQuotes(true);
        getConfig().setStripOptionValueQuotes(false);
        getConfig().setStrictOperator(true);
    }

    public Reg(File input) throws IOException, InvalidFileFormatException
    {
        this();
        load(input);
    }

    public Reg(URL input) throws IOException, InvalidFileFormatException
    {
        this();
        load(input);
    }

    public Reg(InputStream input) throws IOException, InvalidFileFormatException
    {
        this();
        load(input);
    }

    public Reg(Reader input) throws IOException, InvalidFileFormatException
    {
        this();
        load(input);
    }

    public static Value parse(String str)
    {
        throw new UnsupportedOperationException();
    }

    public static class Value
    {
    }
}
