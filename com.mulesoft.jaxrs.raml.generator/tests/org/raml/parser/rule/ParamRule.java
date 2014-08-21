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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.raml.model.parameter.UriParameter;

public class ParamRule extends PojoTupleRule
{


    public ParamRule(String fieldName, NodeRuleFactory nodeRuleFactory)
    {
        super(fieldName, UriParameter.class);
        setNodeRuleFactory(nodeRuleFactory);
    }

    @Override
    public void addRulesFor(Class<?> pojoClass)
    {
        super.addRulesFor(pojoClass);
        SimpleRule typeRule = (SimpleRule) getRuleByFieldName("type");

        rules.put("minLength", new EnumModifierRule("minLength", Arrays.asList("string"), typeRule));
        rules.put("maxLength", new EnumModifierRule("maxLength", Arrays.asList("string"), typeRule));
        rules.put("minimum", new EnumModifierRule("minimum", Arrays.asList("integer", "number"), typeRule));
        rules.put("maximum", new EnumModifierRule("maximum", Arrays.asList("integer", "number"), typeRule));
    }

    @Override
    public List<ValidationResult> onRuleEnd()
    {
        return Collections.emptyList();
    }

}
