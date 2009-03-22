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

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.Name;

public class BasicOptionTree extends BasicCommentedMap<String, OptionTree> implements OptionTree
{
    private static final long serialVersionUID = -7314986212870089511L;
    protected static final char JNDI_PATH_SEPARATOR = '/';
    private static final String SECTION_SYSTEM_PROPERTIES = "@prop";
    private static final String SECTION_ENVIRONMENT = "@env";
    private static final Pattern EXPRESSION = Pattern.compile("(?<!\\\\)\\$\\{(([^\\[]+)/)?([^\\[^/]+)(\\[(([0-9]+))\\])?\\}");
    private static final int G_PATH = 2;
    private static final int G_OPTION = 3;
    private static final int G_OPTION_IDX = 5;
    private OptionMap _options;
    private BasicOptionTree _parent;

    @Override public OptionTree add(String key)
    {
        OptionTree ret = newNode();

        put(key, ret);

        return ret;
    }

    @Override public OptionTree lookup(Name path)
    {
        int n = path.size();
        OptionTree node = this;

        for (int i = 0; (i < n) && (node != null); i++)
        {
            node = node.get(path.get(i));
        }

        return node;
    }

    @Override public OptionTree lookup(String path)
    {
        return lookup(name(path));
    }

    @Override public Name name(String... parts)
    {
        StringBuilder buff = new StringBuilder();

        for (String part : parts)
        {
            if (buff.length() != 0)
            {
                buff.append(JNDI_PATH_SEPARATOR);
            }

            buff.append(part);
        }

        return name(buff.toString());
    }

    @Override public Name name(String value)
    {
        Name ret;

        try
        {
            ret = new CompositeName(value);
        }
        catch (InvalidNameException ex)
        {
            ret = null;
        }

        return ret;
    }

    @Override public OptionMap options()
    {
        if (_options == null)
        {
            _options = newOptions();
        }

        return _options;
    }

    @Override public OptionMap options(String path)
    {
        OptionTree node = lookup(path);

        return (node == null) ? null : node.options();
    }

    @Override public OptionMap options(Name path)
    {
        OptionTree node = lookup(path);

        return (node == null) ? null : node.options();
    }

    @Override public OptionTree put(String key, OptionTree value)
    {
        fixParent(value);

        return super.put(key, value);
    }

    @Override public void putAll(Map<? extends String, ? extends OptionTree> map)
    {
        for (OptionTree node : map.values())
        {
            fixParent(node);
        }

        super.putAll(map);
    }

    @Override public OptionTree remove(Object key)
    {
        OptionTree ret = super.remove(key);

        if ((ret != null) && (ret instanceof BasicOptionTree))
        {
            ((BasicOptionTree) ret).setParent(null);
        }

        return ret;
    }

    protected BasicOptionTree getParent()
    {
        return _parent;
    }

    protected void setParent(BasicOptionTree value)
    {
        _parent = value;
    }

    protected OptionTree newNode()
    {
        return new BasicOptionTree();
    }

    protected OptionMap newOptions()
    {
        return new OptionValuesImpl();
    }

    protected void resolve(StringBuilder buffer, OptionTree owner)
    {
        Matcher m = EXPRESSION.matcher(buffer);

        while (m.find())
        {
            String path = m.group(G_PATH);
            String optionName = m.group(G_OPTION);
            int optionIndex = parseOptionIndex(m);
            OptionTree node = parsePath(m, owner);
            String value = null;

            if (SECTION_ENVIRONMENT.equals(path))
            {
                value = System.getenv(optionName);
            }
            else if (SECTION_SYSTEM_PROPERTIES.equals(path))
            {
                value = System.getProperty(optionName);
            }
            else if (node != null)
            {
                value = (optionIndex == -1) ? node.options().fetch(optionName) : node.options().fetch(optionName, optionIndex);
            }

            if (value != null)
            {
                buffer.replace(m.start(), m.end(), value);
                m.reset(buffer);
            }
        }
    }

    private void fixParent(OptionTree child)
    {
        if ((child != null) && (child instanceof BasicOptionTree))
        {
            ((BasicOptionTree) child).setParent(this);
        }
    }

    private int parseOptionIndex(Matcher m)
    {
        return (m.group(G_OPTION_IDX) == null) ? -1 : Integer.parseInt(m.group(G_OPTION_IDX));
    }

    private OptionTree parsePath(Matcher m, OptionTree owner)
    {
        String path = m.group(G_PATH);

        return (path == null) ? owner : lookup(path);
    }

    class OptionValuesImpl extends BasicOptionMap
    {
        private static final long serialVersionUID = -4140422842143183777L;

        @Override protected void resolve(StringBuilder buff)
        {
            BasicOptionTree root = BasicOptionTree.this;

            while (root.getParent() != null)
            {
                root = root.getParent();
            }

            root.resolve(buff, BasicOptionTree.this);
        }
    }
}
