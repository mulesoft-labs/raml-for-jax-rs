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

/**
 * <p>MapTupleBuilder class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class MapTupleBuilder extends DefaultTupleBuilder<ScalarNode, Node>
{

    private Class valueClass;
    private String fieldName;

    /**
     * <p>Constructor for MapTupleBuilder.</p>
     *
     * @param valueClass a {@link java.lang.Class} object.
     */
    public MapTupleBuilder(Class<?> valueClass)
    {
        this(null, valueClass);
    }

    /**
     * <p>Constructor for MapTupleBuilder.</p>
     *
     * @param fieldName a {@link java.lang.String} object.
     * @param valueClass a {@link java.lang.Class} object.
     */
    public MapTupleBuilder(String fieldName, Class<?> valueClass)
    {
        super(new DefaultScalarTupleHandler(fieldName));
        this.fieldName = fieldName;
        this.valueClass = valueClass;
    }

    
    /** {@inheritDoc} */
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

    
    /** {@inheritDoc} */
    public Object buildValue(Object parent, Node node)
    {
        final HashMap<String, Object> map = new LinkedHashMap<String, Object>();
        ReflectionUtils.setProperty(parent, getFieldName(), map);
        return map;
    }


    /**
     * <p>Getter for the field <code>valueClass</code>.</p>
     *
     * @return a {@link java.lang.Class} object.
     */
    public Class getValueClass()
    {
        return valueClass;
    }

    /**
     * <p>Getter for the field <code>fieldName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getFieldName()
    {
        return fieldName;
    }

    
    /**
     * <p>toString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String toString()
    {
        return fieldName;
    }
}
