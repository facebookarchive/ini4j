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
package org.ini4j.addon;

import java.util.NoSuchElementException;
import java.util.prefs.Preferences;

public class StrictPreferences extends PreferencesWrapper
{
    public StrictPreferences(Preferences peer)
    {
        super(peer);
    }

    public boolean getBoolean(String key) throws NoSuchElementException
    {
        return Boolean.valueOf(get(key));
    }

    public byte[] getByteArray(String key) throws NoSuchElementException
    {
        byte[] value = getByteArray(key, null);

        if (value == null)
        {
            throw new NoSuchElementException();
        }

        return value;
    }

    public double getDouble(String key) throws NoSuchElementException
    {
        return Double.parseDouble(get(key));
    }

    public float getFloat(String key) throws NoSuchElementException
    {
        return Float.parseFloat(get(key));
    }

    public int getInt(String key) throws NoSuchElementException
    {
        return Integer.parseInt(get(key));
    }

    public long getLong(String key) throws NoSuchElementException
    {
        return Long.parseLong(get(key));
    }

    public String get(String key) throws NoSuchElementException
    {
        String value = get(key, null);

        if (value == null)
        {
            throw new NoSuchElementException();
        }

        return value;
    }
}
