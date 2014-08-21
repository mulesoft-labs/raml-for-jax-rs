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

import java.util.List;

import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.raml.parser.utils.ReflectionUtils;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class MapWithListValueTupleRule extends DefaultTupleRule<ScalarNode, MappingNode>
{

    private final Class valueType;
    private String fieldName;


    public MapWithListValueTupleRule(String fieldName, Class<?> valueType, NodeRuleFactory nodeRuleFactory)
    {
        super(fieldName, new DefaultScalarTupleHandler(fieldName), nodeRuleFactory);
        this.valueType = valueType;
    }


    @Override
    public TupleRule<?, ?> getRuleForTuple(NodeTuple nodeTuple)
    {
        if (nodeTuple.getValueNode().getNodeId() == NodeId.sequence)
        {
            return new SequenceTupleRule(fieldName, valueType, getNodeRuleFactory());
        }
        else
        {
            //TODO add it to a list to invoke onRuleEnd on all the rules created
            if (ReflectionUtils.isPojo(valueType))
            {
                return new PojoTupleRule(fieldName, valueType, getNodeRuleFactory());
            }
            return new SimpleRule(fieldName, valueType);
        }
    }

    @Override
    public List<ValidationResult> validateKey(ScalarNode key)
    {
        fieldName = key.getValue();
        return super.validateKey(key);
    }
}
