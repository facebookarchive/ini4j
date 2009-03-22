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

import org.ini4j.test.Helper;

import static org.junit.Assert.*;

import org.junit.Test;

public class SettingsTest
{
    @Test public void testLoad() throws Exception
    {
        Settings settings = Helper.loadTaleIni();

        assertEquals(1, settings.size());
        assertEquals(7, settings.get("dwarfs").size());
        settings.store(System.out);
    }
}
