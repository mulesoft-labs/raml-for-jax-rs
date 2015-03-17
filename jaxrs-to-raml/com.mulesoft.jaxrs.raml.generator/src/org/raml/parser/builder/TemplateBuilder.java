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

import org.raml.model.Template;
import org.raml.parser.utils.ReflectionUtils;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

/**
 * <p>TemplateBuilder class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class TemplateBuilder extends SequenceTupleBuilder
{

    //protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * <p>Constructor for TemplateBuilder.</p>
     *
     * @param fieldName a {@link java.lang.String} object.
     */
    public TemplateBuilder(String fieldName)
    {
        super(fieldName, new ParameterizedType()
        {
            
            public Type[] getActualTypeArguments()
            {
                return new Type[] {String.class, Template.class};
            }

            
            public Type getRawType()
            {
                return Map.class;
            }

            
            public Type getOwnerType()
            {
                return null;
            }
        }, null);
    }

    
    /**
     * <p>buildValue.</p>
     *
     * @param parent a {@link java.lang.Object} object.
     * @param sequenceNode a {@link org.yaml.snakeyaml.nodes.SequenceNode} object.
     * @return a {@link java.lang.Object} object.
     */
    public Object buildValue(Object parent, SequenceNode sequenceNode)
    {
        List<?> list = new ArrayList();
        ReflectionUtils.setProperty(parent, getFieldName(), list);
        int initialSize = sequenceNode.getValue().size();
        for (int i = 0; i < initialSize; i++)
        {
            MappingNode mapping = (MappingNode) sequenceNode.getValue().remove(0);
            for (NodeTuple tuple : mapping.getValue())
            {
                sequenceNode.getValue().add(getFakeTemplateNode(tuple.getKeyNode()));
            }
        }
        return list;
    }

    private Node getFakeTemplateNode(Node keyNode)
    {
        List<NodeTuple> innerTuples = new ArrayList<NodeTuple>();
        innerTuples.add(new NodeTuple(new ScalarNode(Tag.STR, "description", null, null, null), keyNode));
        MappingNode innerNode = new MappingNode(Tag.MAP, innerTuples, false);
        List<NodeTuple> outerTuples = new ArrayList<NodeTuple>();
        outerTuples.add(new NodeTuple(keyNode, innerNode));
        return new MappingNode(Tag.MAP, outerTuples, false);
    }

    
    /**
     * <p>getItemBuilder.</p>
     *
     * @return a {@link org.raml.parser.builder.NodeBuilder} object.
     */
    public NodeBuilder getItemBuilder()
    {
        return super.getItemBuilder();
    }

}
