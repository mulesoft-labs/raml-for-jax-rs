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
package org.raml.parser.rule;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.raml.parser.utils.ReflectionUtils;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class SequenceTupleRule extends DefaultTupleRule<ScalarNode, SequenceNode> implements SequenceRule
{

    private Type itemType;

    public SequenceTupleRule()
    {
    }

    public SequenceTupleRule(String fieldName, Type itemType)
    {
        this(fieldName, itemType, null);
    }

    public SequenceTupleRule(String fieldName, Type itemType, NodeRuleFactory nodeRuleFactory)
    {
        super(fieldName, new DefaultScalarTupleHandler(fieldName), nodeRuleFactory);
        this.itemType = itemType;

    }

    @Override
    public NodeRule<?> getItemRule()
    {
        if (itemType instanceof Class<?>)
        {
            //TODO add it to a list to invoke onRuleEnd on all the rules created
            if (!ReflectionUtils.isPojo((Class) itemType))
            {
                return getScalarRule();
            }
            return new PojoTupleRule("", (Class<?>) itemType, getNodeRuleFactory());
        }

        if (itemType instanceof ParameterizedType)
        {
            ParameterizedType pItemType = (ParameterizedType) itemType;
            if (Map.class.isAssignableFrom((Class<?>) pItemType.getRawType()))
            {
                //sequence of maps
                return new MapTupleRule((Class<?>) pItemType.getActualTypeArguments()[1], getNodeRuleFactory());
            }
        }
        throw new IllegalArgumentException("Sequence item type not supported: " + itemType);
    }

    protected DefaultTupleRule getScalarRule()
    {
        return new SimpleRule(getName(), (Class<?>) itemType);
    }

    @Override
    public void setValueType(Type valueType)
    {
        itemType = valueType;
    }

    protected Type getItemType()
    {
        return itemType;
    }
}
