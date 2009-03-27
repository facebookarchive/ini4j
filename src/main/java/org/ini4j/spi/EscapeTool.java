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

public class EscapeTool
{
    private static final String ESCAPE_LETTERS = "\\tnfbr";
    private static final String ESCAPEABLE_CHARS = "\\\t\n\f\b\r";
    public static final char ESCAPE_CHAR = '\\';
    protected static final char[] HEX = "0123456789abcdef".toCharArray();
    private static final EscapeTool INSTANCE = ServiceFinder.findService(EscapeTool.class);
    private static final char ASCII_MIN = 0x20;
    private static final char ASCII_MAX = 0x7e;
    protected static final int HEX_DIGIT_MASK = 0x0f;
    protected static final int HEX_DIGIT_3_OFFSET = 4;
    protected static final int HEX_DIGIT_2_OFFSET = 8;
    protected static final int HEX_DIGIT_1_OFFSET = 12;
    protected static final int HEX_RADIX = 16;
    private static final int UNICODE_HEX_DIGITS = 4;
    private final String _escapeableChars;
    private final String _escapeLetters;

    public EscapeTool()
    {
        this(ESCAPEABLE_CHARS, ESCAPE_LETTERS);
    }

    protected EscapeTool(String escapeableChars, String escapeLetters)
    {
        _escapeLetters = escapeLetters;
        _escapeableChars = escapeableChars;
    }

    public static EscapeTool getInstance()
    {
        return INSTANCE;
    }

    public String escape(String line)
    {
        int len = line.length();
        StringBuilder buffer = new StringBuilder(len * 2);

        for (int i = 0; i < len; i++)
        {
            char c = line.charAt(i);
            int idx = _escapeableChars.indexOf(c);

            if (idx >= 0)
            {
                buffer.append(ESCAPE_CHAR);
                buffer.append(_escapeLetters.charAt(idx));
            }
            else
            {
                if ((c < ASCII_MIN) || (c > ASCII_MAX))
                {
                    escapeBinary(buffer, c);
                }
                else
                {
                    buffer.append(c);
                }
            }
        }

        return buffer.toString();
    }

    public String unescape(String line)
    {
        int n = line.length();
        StringBuilder buffer = new StringBuilder(n);
        int i = 0;

        while (i < n)
        {
            char c = line.charAt(i++);

            if (c == ESCAPE_CHAR)
            {
                c = line.charAt(i++);
                int next = unescapeBinary(buffer, c, line, i);

                if (next == i)
                {
                    int idx = _escapeLetters.indexOf(c);

                    if (idx >= 0)
                    {
                        c = _escapeableChars.charAt(idx);
                    }

                    buffer.append(c);
                }
                else
                {
                    i = next;
                }
            }
            else
            {
                buffer.append(c);
            }
        }

        return buffer.toString();
    }

    protected void escapeBinary(StringBuilder buff, char c)
    {
        buff.append("\\u");
        buff.append(HEX[(c >>> HEX_DIGIT_1_OFFSET) & HEX_DIGIT_MASK]);
        buff.append(HEX[(c >>> HEX_DIGIT_2_OFFSET) & HEX_DIGIT_MASK]);
        buff.append(HEX[(c >>> HEX_DIGIT_3_OFFSET) & HEX_DIGIT_MASK]);
        buff.append(HEX[c & HEX_DIGIT_MASK]);
    }

    protected int unescapeBinary(StringBuilder buff, char escapeType, String line, int index)
    {
        int ret = index;

        if (escapeType == 'u')
        {
            try
            {
                buff.append((char) Integer.parseInt(line.substring(index, index + UNICODE_HEX_DIGITS), HEX_RADIX));
                ret = index + UNICODE_HEX_DIGITS;
            }
            catch (Exception x)
            {
                throw new IllegalArgumentException("Malformed \\uxxxx encoding.", x);
            }
        }

        return ret;
    }
}
