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

import java.util.ArrayList;
import java.util.List;

import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.raml.parser.resolver.DefaultTupleHandler;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class UriParametersRule extends DefaultTupleRule<ScalarNode, MappingNode>
{

    private List<ValidationResult> errors;
    private ScalarNode keyNode;

    public UriParametersRule()
    {
        super("baseUriParameters", new DefaultScalarTupleHandler("baseUriParameters"));

        this.errors = new ArrayList<ValidationResult>();
    }

    @Override
    public List<ValidationResult> onRuleEnd()
    {
        return errors;
    }

    @Override
    public List<ValidationResult> validateKey(ScalarNode key)
    {
        List<ValidationResult> validationResults = new ArrayList<ValidationResult>();
        if (wasAlreadyDefined())
        {
            validationResults.add(ValidationResult.createErrorResult(getDuplicateRuleMessage("uriParameters"), key));
        }
        validationResults.addAll(super.validateKey(key));
        if (ValidationResult.areValid(validationResults))
        {
            setKeyNode(key);
        }
        return validationResults;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public TupleRule<?, ?> getRuleForTuple(NodeTuple nodeTuple)
    {
        Node keyNode = nodeTuple.getKeyNode();
        String paramName;
        if (keyNode instanceof ScalarNode)
        {
            paramName = ((ScalarNode) keyNode).getValue();
            if (paramName.equals("version"))
            {
                errors.add(ValidationResult.createErrorResult("'" + paramName + "'" + " can not be declared, it is a reserved URI parameter.", keyNode));
            }
            else if (getUriRule().getParameters().contains(paramName))
            {
                return new ParamRule(paramName, getNodeRuleFactory());
            }
            else
            {
                errors.add(ValidationResult.createErrorResult("Parameter '" + paramName + "' not declared in baseUri", keyNode));
            }
        }
        else
        {
            errors.add(ValidationResult.createErrorResult("Invalid element", keyNode));
        }

        return new DefaultTupleRule(keyNode.toString(), new DefaultTupleHandler(), getNodeRuleFactory());
    }

    public boolean wasAlreadyDefined()
    {
        return keyNode != null;
    }

    public void setKeyNode(ScalarNode rulePresent)
    {
        this.keyNode = rulePresent;
    }

    public BaseUriRule getUriRule()
    {
        return (BaseUriRule) getRootTupleRule().getRuleByFieldName("baseUri");
    }

}
