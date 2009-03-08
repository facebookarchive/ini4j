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
    private static final char[] HEX = "0123456789abcdef".toCharArray();
    private static final EscapeTool INSTANCE = ServiceFinder.findService(EscapeTool.class);
    private static final char ASCII_MIN = 0x20;
    private static final char ASCII_MAX = 0x7e;
    private static final int HEX_DIGIT_MASK = 0x0f;
    private static final int DIGIT_3_OFFSET = 4;
    private static final int DIGIT_2_OFFSET = 8;
    private static final int DIGIT_1_OFFSET = 12;
    private static final int HEX_RADIX = 16;
    private static final int UNICODE_HEX_DIGITS = 4;

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
            int idx = "\\\t\n\f".indexOf(c);

            if (idx >= 0)
            {
                buffer.append('\\');
                buffer.append("\\tnf".charAt(idx));
            }
            else
            {
                if ((c < ASCII_MIN) || (c > ASCII_MAX))
                {
                    buffer.append("\\u");
                    buffer.append(HEX[(c >>> DIGIT_1_OFFSET) & HEX_DIGIT_MASK]);
                    buffer.append(HEX[(c >>> DIGIT_2_OFFSET) & HEX_DIGIT_MASK]);
                    buffer.append(HEX[(c >>> DIGIT_3_OFFSET) & HEX_DIGIT_MASK]);
                    buffer.append(HEX[c & HEX_DIGIT_MASK]);
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

            if (c == '\\')
            {
                c = line.charAt(i++);
                if (c == 'u')
                {
                    try
                    {
                        c = (char) Integer.parseInt(line.substring(i, i + UNICODE_HEX_DIGITS), HEX_RADIX);
                        i += UNICODE_HEX_DIGITS;
                    }
                    catch (Exception x)
                    {
                        throw new IllegalArgumentException("Malformed \\uxxxx encoding.", x);
                    }
                }
                else
                {
                    int idx = "\\tnf".indexOf(c);

                    if (idx >= 0)
                    {
                        c = "\\\t\n\f".charAt(idx);
                    }
                }
            }

            buffer.append(c);
        }

        return buffer.toString();
    }
}
