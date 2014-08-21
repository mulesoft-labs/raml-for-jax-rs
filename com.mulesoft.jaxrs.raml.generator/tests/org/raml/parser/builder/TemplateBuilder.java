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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public class TemplateBuilder extends SequenceTupleBuilder
{

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public TemplateBuilder(String fieldName)
    {
        super(fieldName, new ParameterizedType()
        {
            @Override
            public Type[] getActualTypeArguments()
            {
                return new Type[] {String.class, Template.class};
            }

            @Override
            public Type getRawType()
            {
                return Map.class;
            }

            @Override
            public Type getOwnerType()
            {
                return null;
            }
        }, null);
    }

    @Override
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

    @Override
    public NodeBuilder getItemBuilder()
    {
        return super.getItemBuilder();
    }

}
