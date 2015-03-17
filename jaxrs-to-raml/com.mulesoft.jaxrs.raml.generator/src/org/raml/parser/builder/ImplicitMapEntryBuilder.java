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

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.raml.parser.utils.ConvertUtils;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

/**
 * <p>ImplicitMapEntryBuilder class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class ImplicitMapEntryBuilder extends DefaultTupleBuilder<ScalarNode, Node>
{

    private String fieldName;

    private String keyValue;
    private Class<?> keyClass;
    private Class valueClass;


    /**
     * <p>Constructor for ImplicitMapEntryBuilder.</p>
     *
     * @param fieldName a {@link java.lang.String} object.
     * @param keyClass a {@link java.lang.Class} object.
     * @param valueClass a {@link java.lang.Class} object.
     */
    public ImplicitMapEntryBuilder(String fieldName, Class<?> keyClass, Class<?> valueClass)
    {
        super(new DefaultScalarTupleHandler(fieldName));
        this.fieldName = fieldName;
        this.keyClass = keyClass;
        this.valueClass = valueClass;
    }

    
    /** {@inheritDoc} */
    public NodeBuilder getBuilderForTuple(NodeTuple tuple)
    {
        if (builders.isEmpty())
        {
            addBuildersFor(valueClass);
        }
        return super.getBuilderForTuple(tuple);
    }

    
    /** {@inheritDoc} */
    public Object buildValue(Object parent, Node node)
    {

        Map actualParent;
        try
        {
            actualParent = (Map) new PropertyUtilsBean().getProperty(parent, fieldName);
            Object newValue = valueClass.newInstance();
            Object key = ConvertUtils.convertTo(keyValue, keyClass);
            actualParent.put(key, newValue);
            processPojoAnnotations(newValue, key, parent);
            return newValue;
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException(e);
        }

    }

    
    /**
     * <p>buildKey.</p>
     *
     * @param parent a {@link java.lang.Object} object.
     * @param tuple a {@link org.yaml.snakeyaml.nodes.ScalarNode} object.
     */
    public void buildKey(Object parent, ScalarNode tuple)
    {
        keyValue = tuple.getValue();
    }

    
    /**
     * <p>toString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String toString()
    {
        return keyValue;
    }
}
