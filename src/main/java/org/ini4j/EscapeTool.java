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

public class EscapeTool
{
    private static final char[] HEX = "0123456789abcdef".toCharArray();
    private static final EscapeTool _instance = ServiceFinder.findService(EscapeTool.class);

    public static EscapeTool getInstance()
    {
        return _instance;
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
                if ((c < 0x0020) || (c > 0x007e))
                {
                    buffer.append("\\u");
                    buffer.append(HEX[(c >>> 12) & 0x0f]);
                    buffer.append(HEX[(c >>> 8) & 0x0f]);
                    buffer.append(HEX[(c >>> 4) & 0x0f]);
                    buffer.append(HEX[c & 0x0f]);
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

        for (int i = 0; i < n;)
        {
            char c = line.charAt(i++);

            if (c == '\\')
            {
                c = line.charAt(i++);
                if (c == 'u')
                {
                    try
                    {
                        c = (char) Integer.parseInt(line.substring(i, i += 4), 16);
                    }
                    catch (RuntimeException x)
                    {
                        throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
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
