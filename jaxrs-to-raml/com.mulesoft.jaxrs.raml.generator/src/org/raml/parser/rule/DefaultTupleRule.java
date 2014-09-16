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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.raml.parser.resolver.TupleHandler;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;

public class DefaultTupleRule<K extends Node, V extends Node> implements TupleRule<K, Node>
{

    protected Map<String, TupleRule<?, ?>> rules = new HashMap<String, TupleRule<?, ?>>();
    private TupleRule<?, ?> parent;
    private TupleHandler tupleHandler;
    private boolean required;
    private K key;
    private String name;
    private NodeRuleFactory nodeRuleFactory;


    public DefaultTupleRule()
    {
    }

    public DefaultTupleRule(String name, TupleHandler handler, NodeRuleFactory nodeRuleFactory)
    {
        this(name, handler);
        this.setNodeRuleFactory(nodeRuleFactory);
    }

    public DefaultTupleRule(String name, TupleHandler handler)
    {
        this.name = name;
        this.tupleHandler = handler;
    }

    public boolean isRequired()
    {
        return required;
    }

    public void setRequired(boolean required)
    {
        this.required = required;
    }

    
    public void setNodeRuleFactory(NodeRuleFactory nodeRuleFactory)
    {
        this.nodeRuleFactory = nodeRuleFactory;
    }

    
    public void setNestedRules(Map<String, TupleRule<?, ?>> rules)
    {
        this.rules = rules;
    }

    
    public void setHandler(TupleHandler tupleHandler)
    {
        this.tupleHandler = tupleHandler;
    }

    
    public TupleHandler getHandler()
    {
        return tupleHandler;
    }

    
    public List<ValidationResult> validateKey(K key)
    {
        this.key = key;
        return new ArrayList<ValidationResult>();
    }

    
    public final List<ValidationResult> validateValue(Node value)
    {
        ArrayList<ValidationResult> validationResults = new ArrayList<ValidationResult>();
        if (!Tag.NULL.equals(value.getTag()) && !isValidValueNodeType(value.getClass()))
        {
            validationResults.add(ValidationResult.createErrorResult("Invalid value type", value));
        }
        else
        {
            validationResults.addAll(doValidateValue((V) value));
        }
        return validationResults;
    }

    public List<ValidationResult> doValidateValue(V value)
    {
        return new ArrayList<ValidationResult>();
    }

    protected boolean isValidValueNodeType(Class valueNodeClass)
    {
        for (Class<?> clazz : getValueNodeType())
        {
            if (clazz.isAssignableFrom(valueNodeClass))
            {
                return true;
            }
        }
        return false;
    }

    public Class<?>[] getValueNodeType()
    {
        return new Class[] {Node.class};
    }

    
    public List<ValidationResult> onRuleEnd()
    {
        List<ValidationResult> result = new ArrayList<ValidationResult>();
        if (isRequired() && !wasAlreadyDefined())
        {
            result.add(ValidationResult.createErrorResult(ValidationMessage.getMissingRuleMessage(name)));
        }

        for (TupleRule<?, ?> rule : rules.values())
        {
            List<ValidationResult> onRuleEnd = rule.onRuleEnd();
            result.addAll(onRuleEnd);
        }
        return result;
    }

    private boolean wasAlreadyDefined()
    {
        return key != null;
    }

    
    public K getKey()
    {
        return key;
    }

    
    public void setName(String name)
    {
        this.name = name;
    }

    
    public void setValueType(Type valueType)
    {
        //ignore
    }

    public void addRulesFor(Class<?> pojoClass)
    {
        nodeRuleFactory.addRulesTo(pojoClass, this);
    }

    public NodeRuleFactory getNodeRuleFactory()
    {
        return nodeRuleFactory;
    }

    
    public TupleRule<?, ?> getRuleForTuple(NodeTuple nodeTuple)
    {
        for (TupleRule<?, ?> rule : rules.values())
        {
            if (rule.getHandler().handles(nodeTuple))
            {
                return rule;
            }
        }
        return new UnknownTupleRule<Node, Node>(nodeTuple.getKeyNode().toString());
    }

    
    public void setParentTupleRule(TupleRule<?, ?> parent)
    {

        this.parent = parent;
    }

    
    public String getName()
    {
        return name;
    }

    
    public TupleRule<?, ?> getRuleByFieldName(String fieldName)
    {
        return rules.get(fieldName);
    }

    
    public TupleRule<?, ?> getParentTupleRule()
    {
        return parent;
    }

    
    public TupleRule<?, ?> getRootTupleRule()
    {
        TupleRule<?, ?> parentTupleRule = getParentTupleRule();
        if (parentTupleRule == null)
        {
            return null;
        }
        while (parentTupleRule.getParentTupleRule() != null)
        {
            parentTupleRule = parentTupleRule.getParentTupleRule();
        }
        return parentTupleRule;
    }
}
