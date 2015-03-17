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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.raml.parser.annotation.Key;
import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Parent;
import org.raml.parser.annotation.Scalar;
import org.raml.parser.annotation.Sequence;
import org.raml.parser.annotation.TransformHandler;
import org.raml.parser.resolver.DefaultTupleHandler;
import org.raml.parser.resolver.ITransformHandler;
import org.raml.parser.resolver.TupleHandler;
import org.raml.parser.utils.ReflectionUtils;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;

/**
 * <p>DefaultTupleBuilder class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class DefaultTupleBuilder<K extends Node, V extends Node> implements TupleBuilder<K, V>
{

    protected Map<String, TupleBuilder<?, ?>> builders;
    private NodeBuilder<?> parent;
    private TupleHandler handler;

    //protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * <p>Constructor for DefaultTupleBuilder.</p>
     *
     * @param tupleHandler a {@link org.raml.parser.resolver.TupleHandler} object.
     */
    public DefaultTupleBuilder(TupleHandler tupleHandler)
    {
        builders = new HashMap<String, TupleBuilder<?, ?>>();
        this.setHandler(tupleHandler);
    }

    
    /** {@inheritDoc} */
    public NodeBuilder getBuilderForTuple(NodeTuple tuple)
    {
        if (builders == null || builders.isEmpty())
        {
            return new DefaultTupleBuilder(new DefaultTupleHandler());
        }
        for (TupleBuilder tupleBuilder : builders.values())
        {
            if (tupleBuilder.getHandler().handles(tuple))
            {
                return tupleBuilder;
            }
        }
        throw new RuntimeException("Builder not found for " + tuple);
    }

    
    /**
     * <p>buildValue.</p>
     *
     * @param parent a {@link java.lang.Object} object.
     * @param node a V object.
     * @return a {@link java.lang.Object} object.
     */
    public Object buildValue(Object parent, V node)
    {
        return parent;
    }

    /** {@inheritDoc} */
    public void setHandler(TupleHandler handler)
    {
        this.handler = handler;
    }

    
    /**
     * <p>Getter for the field <code>handler</code>.</p>
     *
     * @return a {@link org.raml.parser.resolver.TupleHandler} object.
     */
    public TupleHandler getHandler()
    {
        return handler;
    }

    
    /**
     * <p>buildKey.</p>
     *
     * @param parent a {@link java.lang.Object} object.
     * @param tuple a K object.
     */
    public void buildKey(Object parent, K tuple)
    {

    }

    
    /** {@inheritDoc} */
    public void setParentNodeBuilder(NodeBuilder parentBuilder)
    {
        parent = parentBuilder;
    }

    
    /** {@inheritDoc} */
    public void setNestedBuilders(Map<String, TupleBuilder<?, ?>> nestedBuilders)
    {
        builders = nestedBuilders;
    }


    /**
     * <p>addBuildersFor.</p>
     *
     * @param documentClass a {@link java.lang.Class} object.
     */
    public void addBuildersFor(Class<?> documentClass)
    {
        new TupleBuilderFactory().addBuildersTo(documentClass, this);
    }


    /**
     * <p>Getter for the field <code>parent</code>.</p>
     *
     * @return a {@link org.raml.parser.builder.NodeBuilder} object.
     */
    public NodeBuilder getParent()
    {
        return parent;
    }

    //TODO rethink location
    /**
     * <p>unalias.</p>
     *
     * @param pojo a {@link java.lang.Object} object.
     * @param fieldName a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    protected String unalias(Object pojo, String fieldName)
    {
        List<Field> declaredFields = ReflectionUtils.getInheritedFields(pojo.getClass());
        for (Field declaredField : declaredFields)
        {
            Scalar scalar = declaredField.getAnnotation(Scalar.class);
            Mapping mapping = declaredField.getAnnotation(Mapping.class);
            Sequence sequence = declaredField.getAnnotation(Sequence.class);
            if ((scalar != null && scalar.alias() != null && scalar.alias().equals(fieldName)) ||
                (mapping != null && mapping.alias() != null && mapping.alias().equals(fieldName)) ||
                (sequence != null && sequence.alias() != null && sequence.alias().equals(fieldName)))
            {
                return declaredField.getName();
            }
        }
        return fieldName;
    }

    /**
     * <p>processPojoAnnotations.</p>
     *
     * @param pojo a {@link java.lang.Object} object.
     * @param keyFieldName a {@link java.lang.Object} object.
     * @param parent a {@link java.lang.Object} object.
     */
    protected void processPojoAnnotations(Object pojo, Object keyFieldName, Object parent)
    {
        List<Field> declaredFields = ReflectionUtils.getInheritedFields(pojo.getClass());
        for (Field declaredField : declaredFields)
        {
            Key keyAnnotation = declaredField.getAnnotation(Key.class);
            Parent parentAnnotation = declaredField.getAnnotation(Parent.class);
            if (keyAnnotation != null)
            {
            	TransformHandler annotation = declaredField.getAnnotation(TransformHandler.class);
				if (annotation!=null){
					try{
					ITransformHandler newInstance = annotation.value().newInstance();
					keyFieldName=newInstance.handle(keyFieldName, pojo);
					}catch (Exception e) {
						throw new RuntimeException(e);
					}
            	}
                ReflectionUtils.setProperty(pojo, declaredField.getName(), keyFieldName);
            }
            if (parentAnnotation != null)
            {
                Object value = parent;
                if (!parentAnnotation.property().isEmpty())
                {
                    try
                    {
                        value = PropertyUtils.getProperty(parent, parentAnnotation.property());
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
                }
                if (declaredField.getType().isAssignableFrom(value.getClass()))
                {
                    ReflectionUtils.setProperty(pojo, declaredField.getName(), value);
                }
                else
                {
                    /*logger.info(String.format("parent reference field '%s' could not be set with %s onto %s",
                                              declaredField.getName(), value.getClass(), pojo.getClass()));*/
                }
            }
        }
    }
}
