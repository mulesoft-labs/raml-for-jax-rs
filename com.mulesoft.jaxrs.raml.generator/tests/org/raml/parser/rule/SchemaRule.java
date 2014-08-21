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

import static org.raml.parser.rule.ValidationResult.UNKNOWN;
import static org.raml.parser.rule.ValidationResult.createErrorResult;
import static org.raml.parser.tagresolver.IncludeResolver.INCLUDE_APPLIED_TAG;
import static org.raml.parser.tagresolver.IncludeResolver.IncludeScalarNode;
import static org.yaml.snakeyaml.nodes.Tag.STR;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.eel.kitchen.jsonschema.util.JsonLoader;
import org.raml.parser.visitor.IncludeInfo;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

public class SchemaRule extends SimpleRule
{

    public SchemaRule()
    {
        super("schema", String.class);
    }

    @Override
    public List<ValidationResult> doValidateValue(ScalarNode node)
    {
        String value = node.getValue();
        List<ValidationResult> validationResults = super.doValidateValue(node);

        IncludeInfo globaSchemaIncludeInfo = null;
        ScalarNode schemaNode = getGlobalSchemaNode(value);
        if (schemaNode == null)
        {
            schemaNode = node;
        }
        else
        {
            value = schemaNode.getValue();
            if (schemaNode.getTag().startsWith(INCLUDE_APPLIED_TAG))
            {
                globaSchemaIncludeInfo = new IncludeInfo(schemaNode.getTag());
            }
        }
        if (value == null || isCustomTag(schemaNode.getTag()))
        {
            return validationResults;
        }

        String mimeType = ((ScalarNode) getParentTupleRule().getKey()).getValue();
        if (mimeType.contains("json"))
        {
            try
            {
                JsonLoader.fromString(value);
            }
            catch (JsonParseException jpe)
            {
                String msg = "invalid JSON schema" + getSourceErrorDetail(node) + jpe.getOriginalMessage();
                JsonLocation loc = jpe.getLocation();
                validationResults.add(getErrorResult(msg, getLineOffset(schemaNode) + loc.getLineNr(), globaSchemaIncludeInfo));
            }
            catch (IOException e)
            {
                String prefix = "invalid JSON schema" + getSourceErrorDetail(node);
                validationResults.add(getErrorResult(prefix + e.getMessage(), UNKNOWN, globaSchemaIncludeInfo));
            }
        }
        else if (mimeType.contains("xml"))
        {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            try
            {
                factory.newSchema(new StreamSource(new StringReader(value)));
            }
            catch (SAXParseException e)
            {
                String msg = "invalid XML schema" + getSourceErrorDetail(node) + e.getMessage();
                validationResults.add(getErrorResult(msg, getLineOffset(schemaNode) + e.getLineNumber(), globaSchemaIncludeInfo));
            }
            catch (SAXException e)
            {
                String msg = "invalid XML schema" + getSourceErrorDetail(node);
                validationResults.add(getErrorResult(msg, getLineOffset(schemaNode), globaSchemaIncludeInfo));
            }
        }
        return validationResults;
    }

    private ValidationResult getErrorResult(String msg, int line, IncludeInfo globaSchemaIncludeInfo)
    {
        ValidationResult errorResult = createErrorResult(msg, line, UNKNOWN, UNKNOWN);
        if (globaSchemaIncludeInfo != null)
        {
            errorResult.getIncludeContext().push(globaSchemaIncludeInfo);
        }
        return errorResult;
    }

    private int getLineOffset(ScalarNode schemaNode)
    {
        boolean isInclude = schemaNode.getTag().startsWith(INCLUDE_APPLIED_TAG);
        return isInclude ? -1 : schemaNode.getStartMark().getLine();
    }

    private String getSourceErrorDetail(ScalarNode node)
    {
        String msg = "";
        if (node instanceof IncludeScalarNode)
        {
            msg = " (" + ((IncludeScalarNode) node).getIncludeName() + ")";
        }
        else if (node.getValue().matches("\\w.*"))
        {
            msg = " (" + node.getValue() + ")";
        }
        return msg + ": ";
    }

    private ScalarNode getGlobalSchemaNode(String key)
    {
        GlobalSchemasRule schemasRule = (GlobalSchemasRule) getRootTupleRule().getRuleByFieldName("schemas");
        return schemasRule.getSchema(key);
    }

    private boolean isCustomTag(Tag tag)
    {
        return tag != null && !STR.equals(tag) && !tag.startsWith(INCLUDE_APPLIED_TAG);
    }

}
