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
import org.ini4j.IniParser;

@Deprecated public class FancyIniParser extends IniParser
{
    public FancyIniParser()
    {
        Config cfg = getConfig().clone();

        cfg.setEmptyOption(true);
        cfg.setGlobalSection(true);
        cfg.setUnnamedSection(true);
        cfg.setGlobalSectionName("?");
        cfg.setInclude(true);
        super.setConfig(cfg);
    }

    @Deprecated public synchronized void setAllowEmptyOption(boolean flag)
    {
        getConfig().setEmptyOption(flag);
    }

    @Deprecated public synchronized void setAllowInclude(boolean flag)
    {
        getConfig().setInclude(flag);
    }

    @Deprecated public synchronized void setAllowMissingSection(boolean flag)
    {
        getConfig().setGlobalSection(flag);
    }

    @Deprecated public synchronized void setAllowOptionCaseConversion(boolean flag)
    {
        getConfig().setLowerCaseOption(flag);
    }

    @Deprecated public synchronized void setAllowSectionCaseConversion(boolean flag)
    {
        getConfig().setLowerCaseSection(flag);
    }

    @Deprecated public synchronized void setAllowUnnamedSection(boolean flag)
    {
        getConfig().setUnnamedSection(flag);
    }

    @Deprecated @Override public void setConfig(Config value)
    {
        assert true;
    }

    @Deprecated public synchronized boolean isAllowInclude()
    {
        return getConfig().isInclude();
    }

    @Deprecated public synchronized String getMissingSectionName()
    {
        return getConfig().getGlobalSectionName();
    }

    @Deprecated public synchronized void setMissingSectionName(String name)
    {
        getConfig().setGlobalSectionName(name);
    }

    @Deprecated public synchronized boolean isAllowEmptyOption()
    {
        return getConfig().isEmptyOption();
    }

    @Deprecated public synchronized boolean isAllowMissingSection()
    {
        return getConfig().isGlobalSection();
    }

    @Deprecated public synchronized boolean isAllowOptionCaseConversion()
    {
        return getConfig().isLowerCaseOption();
    }

    @Deprecated public synchronized boolean isAllowSectionCaseConversion()
    {
        return getConfig().isLowerCaseSection();
    }

    @Deprecated public synchronized boolean isAllowUnnamedSection()
    {
        return getConfig().isUnnamedSection();
    }
}
