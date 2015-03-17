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

/**
 * <p>DefaultTupleRule class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class DefaultTupleRule<K extends Node, V extends Node> implements TupleRule<K, Node>
{

    protected Map<String, TupleRule<?, ?>> rules = new HashMap<String, TupleRule<?, ?>>();
    private TupleRule<?, ?> parent;
    private TupleHandler tupleHandler;
    private boolean required;
    private K key;
    private String name;
    private NodeRuleFactory nodeRuleFactory;


    /**
     * <p>Constructor for DefaultTupleRule.</p>
     */
    public DefaultTupleRule()
    {
    }

    /**
     * <p>Constructor for DefaultTupleRule.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param handler a {@link org.raml.parser.resolver.TupleHandler} object.
     * @param nodeRuleFactory a {@link org.raml.parser.rule.NodeRuleFactory} object.
     */
    public DefaultTupleRule(String name, TupleHandler handler, NodeRuleFactory nodeRuleFactory)
    {
        this(name, handler);
        this.setNodeRuleFactory(nodeRuleFactory);
    }

    /**
     * <p>Constructor for DefaultTupleRule.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param handler a {@link org.raml.parser.resolver.TupleHandler} object.
     */
    public DefaultTupleRule(String name, TupleHandler handler)
    {
        this.name = name;
        this.tupleHandler = handler;
    }

    /**
     * <p>isRequired.</p>
     *
     * @return a boolean.
     */
    public boolean isRequired()
    {
        return required;
    }

    /** {@inheritDoc} */
    public void setRequired(boolean required)
    {
        this.required = required;
    }

    
    /** {@inheritDoc} */
    public void setNodeRuleFactory(NodeRuleFactory nodeRuleFactory)
    {
        this.nodeRuleFactory = nodeRuleFactory;
    }

    
    /** {@inheritDoc} */
    public void setNestedRules(Map<String, TupleRule<?, ?>> rules)
    {
        this.rules = rules;
    }

    
    /** {@inheritDoc} */
    public void setHandler(TupleHandler tupleHandler)
    {
        this.tupleHandler = tupleHandler;
    }

    
    /**
     * <p>getHandler.</p>
     *
     * @return a {@link org.raml.parser.resolver.TupleHandler} object.
     */
    public TupleHandler getHandler()
    {
        return tupleHandler;
    }

    
    /**
     * <p>validateKey.</p>
     *
     * @param key a K object.
     * @return a {@link java.util.List} object.
     */
    public List<ValidationResult> validateKey(K key)
    {
        this.key = key;
        return new ArrayList<ValidationResult>();
    }

    
    /** {@inheritDoc} */
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

    /**
     * <p>doValidateValue.</p>
     *
     * @param value a V object.
     * @return a {@link java.util.List} object.
     */
    public List<ValidationResult> doValidateValue(V value)
    {
        return new ArrayList<ValidationResult>();
    }

    /**
     * <p>isValidValueNodeType.</p>
     *
     * @param valueNodeClass a {@link java.lang.Class} object.
     * @return a boolean.
     */
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

    /**
     * <p>getValueNodeType.</p>
     *
     * @return an array of {@link java.lang.Class} objects.
     */
    public Class<?>[] getValueNodeType()
    {
        return new Class[] {Node.class};
    }

    
    /**
     * <p>onRuleEnd.</p>
     *
     * @return a {@link java.util.List} object.
     */
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

    
    /**
     * <p>Getter for the field <code>key</code>.</p>
     *
     * @return a K object.
     */
    public K getKey()
    {
        return key;
    }

    
    /** {@inheritDoc} */
    public void setName(String name)
    {
        this.name = name;
    }

    
    /** {@inheritDoc} */
    public void setValueType(Type valueType)
    {
        //ignore
    }

    /**
     * <p>addRulesFor.</p>
     *
     * @param pojoClass a {@link java.lang.Class} object.
     */
    public void addRulesFor(Class<?> pojoClass)
    {
        nodeRuleFactory.addRulesTo(pojoClass, this);
    }

    /**
     * <p>Getter for the field <code>nodeRuleFactory</code>.</p>
     *
     * @return a {@link org.raml.parser.rule.NodeRuleFactory} object.
     */
    public NodeRuleFactory getNodeRuleFactory()
    {
        return nodeRuleFactory;
    }

    
    /** {@inheritDoc} */
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

    
    /** {@inheritDoc} */
    public void setParentTupleRule(TupleRule<?, ?> parent)
    {

        this.parent = parent;
    }

    
    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName()
    {
        return name;
    }

    
    /** {@inheritDoc} */
    public TupleRule<?, ?> getRuleByFieldName(String fieldName)
    {
        return rules.get(fieldName);
    }

    
    /**
     * <p>getParentTupleRule.</p>
     *
     * @return a {@link org.raml.parser.rule.TupleRule} object.
     */
    public TupleRule<?, ?> getParentTupleRule()
    {
        return parent;
    }

    
    /**
     * <p>getRootTupleRule.</p>
     *
     * @return a {@link org.raml.parser.rule.TupleRule} object.
     */
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
