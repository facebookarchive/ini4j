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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

public class ConfigTest
{
    @AfterClass public static void tearDownClass() throws Exception
    {
        Helper.resetConfig();
    }

    @Test public void testDefaults()
    {
        Config def = newDefaultConfig();

        assertEquals(def, new Config());
        assertEquals(def, Config.getGlobal());
        assertEquals(def, Config.getGlobal().clone());
    }

    @Test public void testSystemProperties()
    {
        Config exp = newInverseConfig();

        setBoolean(Config.PROP_EMPTY_OPTION, exp.isEmptyOption());
        setBoolean(Config.PROP_GLOBAL_SECTION, exp.isGlobalSection());
        setString(Config.PROP_GLOBAL_SECTION_NAME, exp.getGlobalSectionName());
        setBoolean(Config.PROP_INCLUDE, exp.isInclude());
        setBoolean(Config.PROP_LOWER_CASE_OPTION, exp.isLowerCaseOption());
        setBoolean(Config.PROP_LOWER_CASE_SECTION, exp.isLowerCaseSection());
        setBoolean(Config.PROP_MULTI_OPTION, exp.isMultiOption());
        setBoolean(Config.PROP_MULTI_SECTION, exp.isMultiSection());
        setBoolean(Config.PROP_STRICT_OPERATOR, exp.isStrictOperator());
        setBoolean(Config.PROP_UNNAMED_SECTION, exp.isUnnamedSection());
        setBoolean(Config.PROP_ESCAPE, exp.isEscape());
        Config cfg = new Config();

        assertEquals(exp, cfg);
    }

    private void setBoolean(String prop, boolean value)
    {
        System.setProperty(Config.KEY_PREFIX + prop, String.valueOf(value));
    }

    private void setString(String prop, String value)
    {
        System.setProperty(Config.KEY_PREFIX + prop, value);
    }

    private void assertEquals(Config exp, Config act)
    {
        Assert.assertEquals(exp.isEmptyOption(), act.isEmptyOption());
        Assert.assertEquals(exp.isEscape(), act.isEscape());
        Assert.assertEquals(exp.isGlobalSection(), act.isGlobalSection());
        Assert.assertEquals(exp.isInclude(), act.isInclude());
        Assert.assertEquals(exp.isLowerCaseOption(), act.isLowerCaseOption());
        Assert.assertEquals(exp.isLowerCaseSection(), act.isLowerCaseSection());
        Assert.assertEquals(exp.isMultiOption(), act.isMultiOption());
        Assert.assertEquals(exp.isMultiSection(), act.isMultiSection());
        Assert.assertEquals(exp.isStrictOperator(), act.isStrictOperator());
        Assert.assertEquals(exp.isUnnamedSection(), act.isUnnamedSection());
        Assert.assertEquals(exp.getGlobalSectionName(), act.getGlobalSectionName());
    }

    private Config newDefaultConfig()
    {
        Config cfg = new Config();

        cfg.setEmptyOption(false);
        cfg.setEscape(true);
        cfg.setGlobalSection(false);
        cfg.setGlobalSectionName("?");
        cfg.setInclude(false);
        cfg.setLowerCaseOption(false);
        cfg.setLowerCaseSection(false);
        cfg.setMultiSection(false);
        cfg.setMultiOption(true);
        cfg.setStrictOperator(false);
        cfg.setUnnamedSection(false);

        return cfg;
    }

    private Config newInverseConfig()
    {
        Config cfg = newDefaultConfig();

        cfg.setEmptyOption(!cfg.isEmptyOption());
        cfg.setEscape(!cfg.isEscape());
        cfg.setGlobalSection(!cfg.isGlobalSection());
        cfg.setGlobalSectionName("+");
        cfg.setInclude(!cfg.isInclude());
        cfg.setLowerCaseOption(!cfg.isLowerCaseOption());
        cfg.setLowerCaseSection(!cfg.isLowerCaseSection());
        cfg.setMultiSection(!cfg.isMultiSection());
        cfg.setMultiOption(!cfg.isMultiOption());
        cfg.setStrictOperator(!cfg.isStrictOperator());
        cfg.setUnnamedSection(!cfg.isUnnamedSection());

        return cfg;
    }
}
