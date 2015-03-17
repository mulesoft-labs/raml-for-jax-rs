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


/**
 * <p>SimpleRule class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class SimpleRule extends DefaultTupleRule<ScalarNode, ScalarNode>
{

    private ScalarNode keyNode;
    private ScalarNode valueNode;
    private Class<?> fieldClass;

    /**
     * <p>Constructor for SimpleRule.</p>
     *
     * @param fieldName a {@link java.lang.String} object.
     * @param fieldClass a {@link java.lang.Class} object.
     */
    public SimpleRule(String fieldName, Class<?> fieldClass)
    {
        super(fieldName, new DefaultScalarTupleHandler(fieldName));
        this.setFieldClass(fieldClass);
    }

    
    /**
     * <p>validateKey.</p>
     *
     * @param key a {@link org.yaml.snakeyaml.nodes.ScalarNode} object.
     * @return a {@link java.util.List} object.
     */
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

    
    /**
     * <p>doValidateValue.</p>
     *
     * @param node a {@link org.yaml.snakeyaml.nodes.ScalarNode} object.
     * @return a {@link java.util.List} object.
     */
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

    
    /**
     * <p>getValueNodeType.</p>
     *
     * @return an array of {@link java.lang.Class} objects.
     */
    public Class<?>[] getValueNodeType()
    {
        return new Class[] {ScalarNode.class};
    }

    /**
     * <p>wasAlreadyDefined.</p>
     *
     * @return a boolean.
     */
    public boolean wasAlreadyDefined()
    {
        return keyNode != null;
    }

    /**
     * <p>Setter for the field <code>keyNode</code>.</p>
     *
     * @param rulePresent a {@link org.yaml.snakeyaml.nodes.ScalarNode} object.
     */
    public void setKeyNode(ScalarNode rulePresent)
    {
        this.keyNode = rulePresent;
    }

    /**
     * <p>Getter for the field <code>keyNode</code>.</p>
     *
     * @return a {@link org.yaml.snakeyaml.nodes.ScalarNode} object.
     */
    public ScalarNode getKeyNode()
    {
        return keyNode;
    }

    /**
     * <p>Getter for the field <code>valueNode</code>.</p>
     *
     * @return a {@link org.yaml.snakeyaml.nodes.ScalarNode} object.
     */
    public ScalarNode getValueNode()
    {
        return valueNode;
    }

    /**
     * <p>Setter for the field <code>valueNode</code>.</p>
     *
     * @param valueNode a {@link org.yaml.snakeyaml.nodes.ScalarNode} object.
     */
    public void setValueNode(ScalarNode valueNode)
    {
        this.valueNode = valueNode;
    }

    /**
     * <p>Getter for the field <code>fieldClass</code>.</p>
     *
     * @return a {@link java.lang.Class} object.
     */
    public Class<?> getFieldClass()
    {
        return fieldClass;
    }

    /**
     * <p>Setter for the field <code>fieldClass</code>.</p>
     *
     * @param fieldClass a {@link java.lang.Class} object.
     */
    public void setFieldClass(Class<?> fieldClass)
    {
        this.fieldClass = fieldClass;
    }
}
