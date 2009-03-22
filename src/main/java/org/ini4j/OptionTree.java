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

import javax.naming.Name;

public interface OptionTree extends CommentedMap<String, OptionTree>
{
    OptionTree add(String key);

    OptionTree lookup(Name path);

    OptionTree lookup(String path);

    Name name(String path);

    Name name(String... part);

    OptionMap options();

    OptionMap options(Name path);

    OptionMap options(String path);
}
