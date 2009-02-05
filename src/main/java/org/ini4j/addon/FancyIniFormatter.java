/*
 * Copyright 2005 [ini4j] Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ini4j.addon;

import org.ini4j.IniFormatter;
import org.ini4j.IniParser;

public class FancyIniFormatter extends IniFormatter
{
    private boolean _allowEmptyOption = true;
    private boolean _allowStrictOperator = true;

    public synchronized void setAllowEmptyOption(boolean flag)
    {
        _allowEmptyOption = flag;
    }

    public synchronized void setAllowStrictOperator(boolean flag)
    {
        _allowStrictOperator = flag;
    }

    public synchronized boolean isAllowEmptyOption()
    {
        return _allowEmptyOption;
    }

    public synchronized boolean isAllowStrictOperator()
    {
        return _allowStrictOperator;
    }

    @Override public void handleOption(String optionName, String optionValue)
    {
        if (isAllowStrictOperator())
        {
            if (isAllowEmptyOption() || (optionValue != null))
            {
                getOutput().print(escape(optionName));
                getOutput().print(IniParser.OPERATOR);
            }

            if (optionValue != null)
            {
                getOutput().print(escape(optionValue));
            }

            if (isAllowEmptyOption() || (optionValue != null))
            {
                getOutput().println();
            }
        }
        else
        {
            super.handleOption(optionName, ((optionValue == null) && isAllowEmptyOption()) ? "" : optionValue);
        }
    }
}
