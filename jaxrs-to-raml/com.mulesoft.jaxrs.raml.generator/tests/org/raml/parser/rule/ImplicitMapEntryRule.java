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

import static org.raml.parser.rule.ValidationMessage.getDuplicateRuleMessage;
import static org.raml.parser.rule.ValidationResult.createErrorResult;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class ImplicitMapEntryRule extends DefaultTupleRule<ScalarNode, MappingNode>
{

    private Class valueType;
    private final Set<String> keys = new HashSet<String>();

    public ImplicitMapEntryRule(String fieldName, Class valueType)
    {
        super(fieldName, new DefaultScalarTupleHandler(fieldName));
        this.valueType = valueType;

    }

    @Override
    public TupleRule<?, ?> getRuleForTuple(NodeTuple nodeTuple)
    {
        if (rules.isEmpty())
        {
            addRulesFor(valueType);
        }
        return super.getRuleForTuple(nodeTuple);
    }

    @Override
    public List<ValidationResult> onRuleEnd()
    {
        List<ValidationResult> validationResults = super.onRuleEnd();
        rules.clear();
        return validationResults;
    }

    @Override
    public Class<?>[] getValueNodeType()
    {
        return new Class[] {MappingNode.class};
    }

    @Override
    public void setValueType(Type valueType)
    {
        this.valueType = (Class) valueType;
    }

    @Override
    public List<ValidationResult> validateKey(ScalarNode key)
    {
        List<ValidationResult> validationResults = super.validateKey(key);
        if (keys.contains(key.getValue()))
        {
            validationResults.add(createErrorResult(getDuplicateRuleMessage(getName()), key));
        }
        else
        {
            keys.add(key.getValue());
        }
        return validationResults;
    }
}
