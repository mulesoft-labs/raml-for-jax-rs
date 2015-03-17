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

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.raml.parser.utils.ReflectionUtils;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

/**
 * <p>MapTupleRule class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class MapTupleRule extends DefaultTupleRule<ScalarNode, MappingNode>
{

    private Class valueType;
    private String fieldName;
    private final Set<String> keys = new HashSet<String>();

    /**
     * <p>Constructor for MapTupleRule.</p>
     *
     * @param fieldName a {@link java.lang.String} object.
     * @param valueType a {@link java.lang.Class} object.
     */
    public MapTupleRule(String fieldName, Class valueType)
    {
        super(fieldName, new DefaultScalarTupleHandler(fieldName));
        this.valueType = valueType;

    }

    /**
     * <p>Constructor for MapTupleRule.</p>
     *
     * @param valueType a {@link java.lang.Class} object.
     * @param nodeRuleFactory a {@link org.raml.parser.rule.NodeRuleFactory} object.
     */
    public MapTupleRule(Class<?> valueType, NodeRuleFactory nodeRuleFactory)
    {
        this(null, valueType);
        setNodeRuleFactory(nodeRuleFactory);
    }


    
    /** {@inheritDoc} */
    public TupleRule<?, ?> getRuleForTuple(NodeTuple nodeTuple)
    {
        TupleRule<?, ?> tupleRule;
        if (ReflectionUtils.isPojo(valueType))
        {
            tupleRule = new PojoTupleRule(fieldName, valueType, getNodeRuleFactory());
        }
        else
        {
            tupleRule = getScalarRule();
        }

        tupleRule.setParentTupleRule(this);
        return tupleRule;
    }

    /**
     * <p>getScalarRule.</p>
     *
     * @return a {@link org.raml.parser.rule.DefaultTupleRule} object.
     */
    protected DefaultTupleRule getScalarRule()
    {
        return new SimpleRule(getFieldName(), getValueType());
    }

    /**
     * <p>Getter for the field <code>valueType</code>.</p>
     *
     * @return a {@link java.lang.Class} object.
     */
    protected Class getValueType()
    {
        return valueType;
    }

    /**
     * <p>Getter for the field <code>fieldName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getFieldName()
    {
        return fieldName;
    }

    
    /** {@inheritDoc} */
    public void setValueType(Type valueType)
    {
        this.valueType = (Class) valueType;
    }

    
    /**
     * <p>validateKey.</p>
     *
     * @param key a {@link org.yaml.snakeyaml.nodes.ScalarNode} object.
     * @return a {@link java.util.List} object.
     */
    public List<ValidationResult> validateKey(ScalarNode key)
    {
        fieldName = key.getValue();
        return super.validateKey(key);
    }

    /**
     * <p>checkDuplicate.</p>
     *
     * @param key a {@link org.yaml.snakeyaml.nodes.ScalarNode} object.
     * @param validationResults a {@link java.util.List} object.
     */
    public void checkDuplicate(ScalarNode key, List<ValidationResult> validationResults)
    {
        if (keys.contains(key.getValue()))
        {
            validationResults.add(ValidationResult.createErrorResult(getDuplicateRuleMessage(getName()), key));
        }
        else
        {
            keys.add(key.getValue());
        }
    }
}
