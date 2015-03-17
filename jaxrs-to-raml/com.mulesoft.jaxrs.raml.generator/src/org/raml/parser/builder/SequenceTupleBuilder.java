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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.raml.parser.annotation.ExtraHandler;
import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.raml.parser.utils.ReflectionUtils;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.SequenceNode;

/**
 * <p>SequenceTupleBuilder class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class SequenceTupleBuilder extends DefaultTupleBuilder<Node, SequenceNode> implements SequenceBuilder
{


    private String fieldName;
    private Type itemType;
	private ExtraHandler additionalHandler;

    /**
     * <p>Constructor for SequenceTupleBuilder.</p>
     *
     * @param fieldName a {@link java.lang.String} object.
     * @param itemType a {@link java.lang.reflect.Type} object.
     * @param extraHandler a {@link java.lang.Class} object.
     */
    public SequenceTupleBuilder(String fieldName, Type itemType, Class<? extends ExtraHandler> extraHandler)
    {
        super(new DefaultScalarTupleHandler(fieldName));
        this.itemType = itemType;
        this.fieldName = fieldName;
        if (extraHandler!=null&&extraHandler!=ExtraHandler.class){
        	try{
        	this.additionalHandler=extraHandler.newInstance();
        	}catch (Exception e) {
        		throw new RuntimeException(e);
			}
        }
    }

    /**
     * <p>Getter for the field <code>fieldName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    protected String getFieldName()
    {
        return fieldName;
    }

    
    /**
     * <p>buildValue.</p>
     *
     * @param parent a {@link java.lang.Object} object.
     * @param node a {@link org.yaml.snakeyaml.nodes.SequenceNode} object.
     * @return a {@link java.lang.Object} object.
     */
    public Object buildValue(Object parent, SequenceNode node)
    {
        List<?> list = new ArrayList();
        if (additionalHandler!=null){
        	additionalHandler.handle(parent, node);
        }
        ReflectionUtils.setProperty(parent, fieldName, list);
        return list;
    }

    
    /**
     * <p>getItemBuilder.</p>
     *
     * @return a {@link org.raml.parser.builder.NodeBuilder} object.
     */
    public NodeBuilder getItemBuilder()
    {
        if (itemType instanceof Class<?>)
        {
            if (ReflectionUtils.isWrapperOrString((Class<?>) itemType))
            {
                //sequence of scalars
                return new ScalarTupleBuilder(fieldName, (Class<?>) itemType, null);
            }
            //sequence of pojos
            return new PojoTupleBuilder((Class<?>) itemType);
        }

        if (itemType instanceof ParameterizedType)
        {
            ParameterizedType pItemType = (ParameterizedType) itemType;
            if (Map.class.isAssignableFrom((Class<?>) pItemType.getRawType()))
            {
                //sequence of maps
                return new MapTupleBuilder((Class<?>) pItemType.getActualTypeArguments()[1]);
            }
        }
        throw new IllegalArgumentException("Sequence item type not supported: " + itemType);
    }

}
