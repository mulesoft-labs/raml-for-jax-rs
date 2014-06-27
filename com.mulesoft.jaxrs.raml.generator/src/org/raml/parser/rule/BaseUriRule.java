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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.nodes.ScalarNode;

public class BaseUriRule extends SimpleRule
{

    public static final String URI_NOT_VALID_MESSAGE = "The baseUri element is not a valid URI";
    public static final String VERSION_NOT_PRESENT_MESSAGE = "version parameter must exist in the API definition";

    public static final String URI_PATTERN = "[.*]?\\{(\\w+)?\\}[.*]*";
    private String baseUri;
    private Set<String> parameters;
    private Pattern pattern;


    public BaseUriRule()
    {
        super("baseUri", String.class);

        parameters = new HashSet<String>();
        pattern = Pattern.compile(URI_PATTERN);

    }

    public String getBaseUri()
    {
        return baseUri;
    }

    public Set<String> getParameters()
    {
        return parameters;
    }


    @Override
    public List<ValidationResult> doValidateValue(ScalarNode node)
    {
        String value = node.getValue();
        Matcher matcher = pattern.matcher(value);
        List<ValidationResult> validationResults = new ArrayList<ValidationResult>(super.doValidateValue(node));
        while (matcher.find())
        {
            String paramValue = matcher.group(1);
            value = value.replace("{" + paramValue + "}", "temp");
            parameters.add(paramValue);
        }
        if (getVersionRule().getKeyNode() == null && parameters.contains(getVersionRule().getName()))
        {
            validationResults.add(ValidationResult.createErrorResult(VERSION_NOT_PRESENT_MESSAGE, node.getStartMark(), node.getEndMark()));
        }
        //validate uri only when no parameters are defined
        if (parameters.isEmpty() && !isValid(value))
        {
            validationResults.add(ValidationResult.createErrorResult(URI_NOT_VALID_MESSAGE, getKeyNode().getStartMark(), getKeyNode().getEndMark()));
        }
        if (ValidationResult.areValid(validationResults))
        {
            baseUri = node.getValue();
        }
        return validationResults;
    }

    private boolean isValid(String value)
    {
        try
        {
            new URL(value);
            return true;
        }
        catch (MalformedURLException e)
        {
            return false;
        }
    }

    public SimpleRule getVersionRule()
    {
        return (SimpleRule) getParentTupleRule().getRuleByFieldName("version");
    }


}
