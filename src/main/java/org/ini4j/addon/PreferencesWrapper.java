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
package org.ini4j.addon;

import java.io.IOException;
import java.io.OutputStream;

import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

public class PreferencesWrapper extends Preferences
{
    protected Preferences peer;

    public PreferencesWrapper(Preferences impl)
    {
        peer = impl;
    }

    @Override public boolean getBoolean(String key, boolean def)
    {
        return peer.getBoolean(key, def);
    }

    @Override public byte[] getByteArray(String key, byte[] def)
    {
        return peer.getByteArray(key, def);
    }

    @Override public double getDouble(String key, double def)
    {
        return peer.getDouble(key, def);
    }

    @Override public boolean isUserNode()
    {
        return peer.isUserNode();
    }

    @Override public float getFloat(String key, float def)
    {
        return peer.getFloat(key, def);
    }

    @Override public int getInt(String key, int def)
    {
        return peer.getInt(key, def);
    }

    @Override public long getLong(String key, long def)
    {
        return peer.getLong(key, def);
    }

    @Override public String absolutePath()
    {
        return peer.absolutePath();
    }

    @Override public void addNodeChangeListener(NodeChangeListener ncl)
    {
        peer.addNodeChangeListener(ncl);
    }

    @Override public void addPreferenceChangeListener(PreferenceChangeListener pcl)
    {
        peer.addPreferenceChangeListener(pcl);
    }

    @Override public String[] childrenNames() throws BackingStoreException
    {
        return peer.childrenNames();
    }

    @Override public void clear() throws BackingStoreException
    {
        peer.clear();
    }

    @Override public void exportNode(OutputStream os) throws IOException, BackingStoreException
    {
        peer.exportNode(os);
    }

    @Override public void exportSubtree(OutputStream os) throws IOException, BackingStoreException
    {
        peer.exportSubtree(os);
    }

    @Override public void flush() throws BackingStoreException
    {
        peer.flush();
    }

    @Override public String get(String key, String def)
    {
        return peer.get(key, def);
    }

    @Override public String[] keys() throws BackingStoreException
    {
        return peer.keys();
    }

    @Override public String name()
    {
        return peer.name();
    }

    @Override public Preferences node(String pathName)
    {
        return peer.node(pathName);
    }

    @Override public boolean nodeExists(String pathName) throws BackingStoreException
    {
        return peer.nodeExists(pathName);
    }

    @Override public Preferences parent()
    {
        return peer.parent();
    }

    @Override public void put(String key, String value)
    {
        peer.put(key, value);
    }

    @Override public void putBoolean(String key, boolean value)
    {
        peer.putBoolean(key, value);
    }

    @Override public void putByteArray(String key, byte[] value)
    {
        peer.putByteArray(key, value);
    }

    @Override public void putDouble(String key, double value)
    {
        peer.putDouble(key, value);
    }

    @Override public void putFloat(String key, float value)
    {
        peer.putFloat(key, value);
    }

    @Override public void putInt(String key, int value)
    {
        peer.putInt(key, value);
    }

    @Override public void putLong(String key, long value)
    {
        peer.putLong(key, value);
    }

    @Override public void remove(String key)
    {
        peer.remove(key);
    }

    @Override public void removeNode() throws BackingStoreException
    {
        peer.removeNode();
    }

    @Override public void removeNodeChangeListener(NodeChangeListener ncl)
    {
        peer.removeNodeChangeListener(ncl);
    }

    @Override public void removePreferenceChangeListener(PreferenceChangeListener pcl)
    {
        peer.removePreferenceChangeListener(pcl);
    }

    @Override public void sync() throws BackingStoreException
    {
        peer.sync();
    }

    @Override public String toString()
    {
        return peer.toString();
    }
}
