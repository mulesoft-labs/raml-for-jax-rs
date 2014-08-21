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

import static org.raml.parser.rule.ValidationMessage.getRuleTypeMisMatch;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.raml.parser.utils.ConvertUtils;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class SimpleNullAllowedRule extends SimpleRule
{

    public SimpleNullAllowedRule(String fieldName, Class<?> fieldClass)
    {
        super(fieldName, fieldClass);
    }

    @Override
    public List<ValidationResult> doValidateValue(ScalarNode node)
    {
        String value = node.getValue();
        List<ValidationResult> validationResults = new ArrayList<ValidationResult>();
        if (!StringUtils.isEmpty(value) && !ConvertUtils.canBeConverted(value, getFieldClass()))
        {
            validationResults.add(ValidationResult.createErrorResult(getRuleTypeMisMatch(getName(), getFieldClass().getSimpleName()), node));
        }
        setValueNode(node);
        return validationResults;
    }
}
