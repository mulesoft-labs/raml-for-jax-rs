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

import java.util.ArrayList;
import java.util.List;

import org.yaml.snakeyaml.nodes.ScalarNode;


public class EnumModifierRule extends SimpleRule
{

    private SimpleRule enumRule;
    private List<String> enumTypes;

    public EnumModifierRule(String ruleName, List<String> enumTypes, SimpleRule enumRule)
    {
        super(ruleName, Integer.class);
        this.enumTypes = enumTypes;
        this.enumRule = enumRule;
    }

    @Override
    public List<ValidationResult> validateKey(ScalarNode key)
    {
        List<ValidationResult> validationResults = new ArrayList<ValidationResult>();
        ScalarNode enumValueNode = enumRule.getValueNode();
        String messageTypes = generateMessageTypes();
        if (enumValueNode == null)
        {
            validationResults.add(ValidationResult.createErrorResult(enumRule.getName() + " must exist first, and it must be of type" + messageTypes, key.getStartMark(), key.getEndMark()));
        }
        if (enumValueNode != null && !enumTypes.contains(enumRule.getValueNode().getValue()))
        {
            validationResults.add(ValidationResult.createErrorResult(enumRule.getName() + " must be of type" + messageTypes, key.getStartMark(), key.getEndMark()));
        }
        validationResults.addAll(super.validateKey(key));
        if (ValidationResult.areValid(validationResults))
        {
            setKeyNode(key);
        }
        return validationResults;
    }

    private String generateMessageTypes()
    {
        StringBuilder types = new StringBuilder();
        for (int i = 0; i < enumTypes.size() - 1; i++)
        {
            types.append(" " + enumTypes.get(i) + " or");
        }
        types.append(" " + enumTypes.get(enumTypes.size() - 1));
        return types.toString();
    }

    @Override
    public List<ValidationResult> doValidateValue(ScalarNode value)
    {
        String valueNode = value.getValue();
        List<ValidationResult> validationResults = new ArrayList<ValidationResult>();
        try
        {
            Integer.parseInt(valueNode);
        }
        catch (NumberFormatException nfe)
        {
            validationResults.add(ValidationResult.createErrorResult(getName() + " can only contain integer values greater than zero", value.getStartMark(), value.getEndMark()));
        }
        validationResults.addAll(super.doValidateValue(value));
        return validationResults;
    }

}
