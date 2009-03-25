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
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import java.net.URL;

import java.nio.charset.Charset;

public class Reg extends Wini
{
    private static final long serialVersionUID = -1485602876922985912L;
    protected static final String DEFAULT_VERSION = "Windows Registry Editor Version 5.00";
    protected static final String DEFAULT_SUFFIX = ".reg";
    protected static final String TMP_PREFIX = "reg-";
    private String _version;

    public Reg()
    {
        getConfig().setStrictOperator(true);
        getConfig().setEmptySection(true);
        getConfig().setFileEncoding(Charset.forName("UnicodeLittle"));
        getConfig().setLineSeparator("\r\n");
    }

    public Reg(String registryKey) throws IOException, ReadException
    {
        this();
        read(registryKey);
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

    public String getVersion()
    {
        return _version;
    }

    public void setVersion(String value)
    {
        _version = value;
    }

    @Override public void load(InputStream input) throws IOException, InvalidFileFormatException
    {
        load(new InputStreamReader(input, getConfig().getFileEncoding()));
    }

    @Override public void load(URL input) throws IOException, InvalidFileFormatException
    {
        load(new InputStreamReader(input.openStream(), getConfig().getFileEncoding()));
    }

    @Override public void load(Reader input) throws IOException, InvalidFileFormatException
    {
        int newline = 2;
        StringBuilder buff = new StringBuilder();

        for (int c = input.read(); c != -1; c = input.read())
        {
            if (c == '\n')
            {
                newline--;
                if (newline == 0)
                {
                    break;
                }
            }
            else if ((c != '\r') && (newline != 1))
            {
                buff.append((char) c);
            }
        }

        if (buff.length() == 0)
        {
            throw new InvalidFileFormatException("Missing version header");
        }

        setVersion(buff.toString());
        super.load(input);
    }

    public void read(String registryKey) throws IOException, ReadException
    {
        File tmp = createTempFile();

        try
        {
            regExport(registryKey, tmp);
            load(tmp);
        }
        finally
        {
            tmp.delete();
        }
    }

    @Override public void store(OutputStream output) throws IOException
    {
        store(new OutputStreamWriter(output, getConfig().getFileEncoding()));
    }

    @Override public void store(Writer output) throws IOException
    {
        output.write((_version == null) ? DEFAULT_VERSION : _version);
        output.write(getConfig().getLineSeparator());
        output.write(getConfig().getLineSeparator());
        super.store(output);
    }

    public void write(String registryKey) throws IOException, WriteException
    {
        File tmp = createTempFile();

        try
        {
            regImport(tmp);
        }
        finally
        {
            tmp.delete();
        }
    }

    protected File createTempFile() throws IOException
    {
        File ret = File.createTempFile(TMP_PREFIX, DEFAULT_SUFFIX);

        ret.deleteOnExit();

        return ret;
    }

    protected int exec(String[] args) throws IOException
    {
        Process proc = Runtime.getRuntime().exec(args);
        int status;

        try
        {
            status = proc.waitFor();
        }
        catch (InterruptedException x)
        {
            throw (IOException) (new InterruptedIOException().initCause(x));
        }

        return status;
    }

    protected void regExport(String registryKey, File file) throws IOException, ReadException
    {
        int status = exec(new String[] { "cmd", "/c", "reg", "export", registryKey, file.getAbsolutePath() });

        if (status != 0)
        {
            throw new ReadException(registryKey);
        }
    }

    protected void regImport(File file) throws IOException, WriteException
    {
        int status = exec(new String[] { "cmd", "/c", "reg", "import", file.getAbsolutePath() });

        if (status != 0)
        {
            throw new IOException();
        }
    }

    public static class ReadException extends IOException
    {
        private static final long serialVersionUID = 9204800670442695605L;

        public ReadException(String key)
        {
            super(key);
        }
    }

    public static class WriteException extends IOException
    {
        private static final long serialVersionUID = 7004159918511996639L;

        public WriteException(String key)
        {
            super(key);
        }
    }

/*
     private static enum Type
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

 */
}
