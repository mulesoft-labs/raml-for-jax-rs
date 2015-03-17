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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.raml.model.Resource;
import org.raml.parser.annotation.Scalar;
import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.raml.parser.resolver.TupleHandler;
import org.raml.parser.tagresolver.IncludeResolver.IncludeScalarNode;
import org.raml.parser.utils.ConvertUtils;
import org.raml.parser.utils.ReflectionUtils;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;


/**
 * <p>ScalarTupleBuilder class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class ScalarTupleBuilder extends DefaultTupleBuilder<ScalarNode, ScalarNode>
{

    private String fieldName;
    private Class<?> type;
    private String includeField;

    /**
     * <p>Constructor for ScalarTupleBuilder.</p>
     *
     * @param field a {@link java.lang.String} object.
     * @param type a {@link java.lang.Class} object.
     * @param includeField a {@link java.lang.String} object.
     */
    public ScalarTupleBuilder(String field, Class<?> type,String includeField)
    {
        super(new DefaultScalarTupleHandler(field));
        this.type = type;
        this.includeField=includeField;
    }

    
    /**
     * <p>getHandler.</p>
     *
     * @return a {@link org.raml.parser.resolver.TupleHandler} object.
     */
    public TupleHandler getHandler() {
    	return super.getHandler();
    }
    

    
    /**
     * <p>buildValue.</p>
     *
     * @param parent a {@link java.lang.Object} object.
     * @param node a {@link org.yaml.snakeyaml.nodes.ScalarNode} object.
     * @return a {@link java.lang.Object} object.
     */
    public Object buildValue(Object parent, ScalarNode node)
    {

        final String value = node.getValue();
        final Object converted = ConvertUtils.convertTo(value, type);
        String unalias = unalias(parent, fieldName);
        ReflectionUtils.setProperty(parent, unalias, converted);
        if (includeField!=null&&includeField.length()>0){
        	if (node instanceof IncludeScalarNode)
        	{
        		IncludeScalarNode sc=(IncludeScalarNode) node;
        		ReflectionUtils.setProperty(parent, includeField, sc.getIncludeName());
        	}
        }
        if (fieldName!=null&&fieldName.equals("type")){
        	if (parent instanceof Resource){
        	List<Node> nm=new ArrayList<Node>();
        	nm.add(node);
			//FIXME;
        	new TypeExtraHandler().handle(parent,new SequenceNode(Tag.BOOL, nm, null));
        	}
        }
        return parent;
    }

    
    /**
     * <p>buildKey.</p>
     *
     * @param parent a {@link java.lang.Object} object.
     * @param tuple a {@link org.yaml.snakeyaml.nodes.ScalarNode} object.
     */
    public void buildKey(Object parent, ScalarNode tuple)
    {
        fieldName = tuple.getValue();
    }
}
