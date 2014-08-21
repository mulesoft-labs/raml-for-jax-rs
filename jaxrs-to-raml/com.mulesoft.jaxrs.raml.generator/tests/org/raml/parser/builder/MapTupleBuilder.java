/*
 * Copyright (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.parser.builder;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.raml.parser.utils.ReflectionUtils;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class MapTupleBuilder extends DefaultTupleBuilder<ScalarNode, Node>
{

    private Class valueClass;
    private String fieldName;

    public MapTupleBuilder(Class<?> valueClass)
    {
        this(null, valueClass);
    }

    public MapTupleBuilder(String fieldName, Class<?> valueClass)
    {
        super(new DefaultScalarTupleHandler(fieldName));
        this.fieldName = fieldName;
        this.valueClass = valueClass;
    }

    @Override
    public TupleBuilder getBuilderForTuple(NodeTuple tuple)
    {
        if (ReflectionUtils.isPojo(getValueClass()))
        {
            return new PojoTupleBuilder(getValueClass());
        }
        else
        {
            return new ScalarTupleBuilder(null, getValueClass(),null);
        }
    }

    @Override
    public Object buildValue(Object parent, Node node)
    {
        final HashMap<String, Object> map = new LinkedHashMap<String, Object>();
        ReflectionUtils.setProperty(parent, getFieldName(), map);
        return map;
    }


    public Class getValueClass()
    {
        return valueClass;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    @Override
    public String toString()
    {
        return fieldName;
    }
}
