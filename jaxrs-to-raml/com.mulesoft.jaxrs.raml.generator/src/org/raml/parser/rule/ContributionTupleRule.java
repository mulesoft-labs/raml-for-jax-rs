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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.raml.parser.resolver.TupleHandler;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;

public class ContributionTupleRule implements TupleRule<Node, Node>
{

    private TupleRule rule;
    private List<TupleRule> contributionRules;

    public ContributionTupleRule(TupleRule rule, List<TupleRule> contributionRules)
    {
        this.rule = rule;
        this.contributionRules = contributionRules;
    }

    
    public List<ValidationResult> validateKey(Node key)
    {
        List<ValidationResult> result = new ArrayList<ValidationResult>();
        result.addAll(rule.validateKey(key));
        for (TupleRule contributionRule : contributionRules)
        {
            result.addAll(contributionRule.validateKey(key));
        }
        return result;
    }

    
    public TupleRule<?, ?> getRuleForTuple(NodeTuple nodeTuple)
    {
        return rule.getRuleForTuple(nodeTuple);
    }

    
    public void setParentTupleRule(TupleRule<?, ?> parent)
    {
        rule.setParentTupleRule(parent);
        for (TupleRule contributionRule : contributionRules)
        {
            contributionRule.setParentTupleRule(parent);
        }
    }

    
    public TupleRule<?, ?> getParentTupleRule()
    {
        return rule.getParentTupleRule();
    }

    
    public TupleRule<?, ?> getRootTupleRule()
    {
        return rule.getRootTupleRule();
    }

    
    public String getName()
    {
        return rule.getName();
    }

    
    public TupleRule<?, ?> getRuleByFieldName(String fieldName)
    {
        return rule.getRuleByFieldName(fieldName);
    }

    
    public void setNestedRules(Map<String, TupleRule<?, ?>> innerBuilders)
    {
        rule.setNestedRules(innerBuilders);
    }

    
    public void setHandler(TupleHandler tupleHandler)
    {
        rule.setHandler(tupleHandler);
    }

    
    public TupleHandler getHandler()
    {
        return rule.getHandler();
    }

    
    public void setRequired(boolean required)
    {
        rule.setRequired(required);
        for (TupleRule contributionRule : contributionRules)
        {
            contributionRule.setRequired(required);
        }
    }

    
    public void setNodeRuleFactory(NodeRuleFactory nodeRuleFactory)
    {
        rule.setNodeRuleFactory(nodeRuleFactory);
    }

    
    public Node getKey()
    {
        return rule.getKey();
    }

    
    public void setName(String name)
    {
        //ignore
    }

    
    public void setValueType(Type valueType)
    {
        //ignore
    }

    
    public List<ValidationResult> validateValue(Node value)
    {
        List<ValidationResult> result = new ArrayList<ValidationResult>();
        result.addAll(rule.validateValue(value));
        for (TupleRule contributionRule : contributionRules)
        {
            result.addAll(contributionRule.validateValue(value));
        }
        return result;
    }

    
    public List<ValidationResult> onRuleEnd()
    {
        List<ValidationResult> result = new ArrayList<ValidationResult>();
        result.addAll(rule.onRuleEnd());
        for (TupleRule contributionRule : contributionRules)
        {
            result.addAll(contributionRule.onRuleEnd());
        }
        return result;
    }

}
