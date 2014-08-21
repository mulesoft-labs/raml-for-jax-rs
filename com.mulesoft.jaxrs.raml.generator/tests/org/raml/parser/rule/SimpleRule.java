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
import static org.raml.parser.rule.ValidationMessage.getRuleEmptyMessage;
import static org.raml.parser.rule.ValidationMessage.getRuleTypeMisMatch;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.raml.parser.utils.ConvertUtils;
import org.yaml.snakeyaml.nodes.ScalarNode;


public class SimpleRule extends DefaultTupleRule<ScalarNode, ScalarNode>
{

    private ScalarNode keyNode;
    private ScalarNode valueNode;
    private Class<?> fieldClass;

    public SimpleRule(String fieldName, Class<?> fieldClass)
    {
        super(fieldName, new DefaultScalarTupleHandler(fieldName));
        this.setFieldClass(fieldClass);
    }

    @Override
    public List<ValidationResult> validateKey(ScalarNode key)
    {
        List<ValidationResult> validationResults = super.validateKey(key);
        if (wasAlreadyDefined())
        {
            validationResults.add(ValidationResult.createErrorResult(getDuplicateRuleMessage(getName()), key));
        }
        setKeyNode(key);

        return validationResults;
    }

    @Override
    public List<ValidationResult> doValidateValue(ScalarNode node)
    {
        String value = node.getValue();
        List<ValidationResult> validationResults = new ArrayList<ValidationResult>();
        if (StringUtils.isEmpty(value))
        {
            validationResults.add(ValidationResult.createErrorResult(getRuleEmptyMessage(getName()), keyNode != null ? keyNode : node));
        }
        if (!ConvertUtils.canBeConverted(value, getFieldClass()))
        {
            validationResults.add(ValidationResult.createErrorResult(getRuleTypeMisMatch(getName(), getFieldClass().getSimpleName()), node));
        }
        setValueNode(node);
        return validationResults;
    }

    @Override
    public Class<?>[] getValueNodeType()
    {
        return new Class[] {ScalarNode.class};
    }

    public boolean wasAlreadyDefined()
    {
        return keyNode != null;
    }

    public void setKeyNode(ScalarNode rulePresent)
    {
        this.keyNode = rulePresent;
    }

    public ScalarNode getKeyNode()
    {
        return keyNode;
    }

    public ScalarNode getValueNode()
    {
        return valueNode;
    }

    public void setValueNode(ScalarNode valueNode)
    {
        this.valueNode = valueNode;
    }

    public Class<?> getFieldClass()
    {
        return fieldClass;
    }

    public void setFieldClass(Class<?> fieldClass)
    {
        this.fieldClass = fieldClass;
    }
}
