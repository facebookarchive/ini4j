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
import java.io.FileWriter;

import java.util.prefs.BackingStoreException;

public class IniFile extends IniPreferences
{
    public static enum Mode
    {
        RO,
        WO,
        RW
    }

    private final File _file;
    private final Mode _mode;

    public IniFile(File file) throws BackingStoreException
    {
        this(file, Mode.RO);
    }

    public IniFile(File file, Mode mode) throws BackingStoreException
    {
        super(new Ini());
        _file = file;
        _mode = mode;
        if ((_mode == Mode.RO) || ((_mode != Mode.WO) && _file.exists()))
        {
            sync();
        }
    }

    public File getFile()
    {
        return _file;
    }

    public Mode getMode()
    {
        return _mode;
    }

    @Override public void flush() throws BackingStoreException
    {
        if (_mode == Mode.RO)
        {
            throw new BackingStoreException("read only instance");
        }

        try
        {
            synchronized (lock)
            {
                FileWriter writer = new FileWriter(_file);

                try
                {
                    getIni().store(writer);
                }
                finally
                {
                    writer.close();
                }
            }
        }
        catch (Exception x)
        {
            throw new BackingStoreException(x);
        }
    }

    @Override public void sync() throws BackingStoreException
    {
        if (_mode == Mode.WO)
        {
            throw new BackingStoreException("write only instance");
        }

        try
        {
            synchronized (lock)
            {
                getIni().load(_file.toURI().toURL());
            }
        }
        catch (Exception x)
        {
            throw new BackingStoreException(x);
        }
    }
}
