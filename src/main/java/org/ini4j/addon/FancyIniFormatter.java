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

import org.ini4j.Config;

import org.ini4j.spi.IniFormatter;

@Deprecated public class FancyIniFormatter extends IniFormatter
{
    public FancyIniFormatter()
    {
        Config cfg = getConfig().clone();

        cfg.setEmptyOption(true);
        cfg.setStrictOperator(true);
        super.setConfig(cfg);
    }

    @Deprecated public synchronized void setAllowEmptyOption(boolean flag)
    {
        getConfig().setEmptyOption(flag);
    }

    @Deprecated public synchronized void setAllowStrictOperator(boolean flag)
    {
        getConfig().setStrictOperator(flag);
    }

    @Deprecated @Override public void setConfig(Config value)
    {
        assert true;
    }

    @Deprecated public synchronized boolean isAllowEmptyOption()
    {
        return getConfig().isEmptyOption();
    }

    @Deprecated public synchronized boolean isAllowStrictOperator()
    {
        return getConfig().isStrictOperator();
    }
}
