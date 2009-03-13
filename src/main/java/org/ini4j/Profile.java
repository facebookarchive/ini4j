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

public interface Profile<S extends Profile.Section> extends MultiMap<String, S>
{
    S add(String name);

    <T> T as(Class<T> clazz);

    String fetch(String section, String option);

    <T> T fetch(String section, String option, Class<T> clazz);

    <T> T fetch(String section, String option, int index, Class<T> clazz);

    String get(String section, String option);

    <T> T get(String section, String option, Class<T> clazz);

    <T> T get(String section, String option, int index, Class<T> clazz);

    String put(String section, String option, Object value);

    String put(String section, String option, int index, Object value);

    Section remove(S section);

    @Deprecated <T> T to(Class<T> clazz);

    interface Section extends OptionMap
    {
        String getName();

        @Deprecated <T> T to(Class<T> clazz);
    }
}
