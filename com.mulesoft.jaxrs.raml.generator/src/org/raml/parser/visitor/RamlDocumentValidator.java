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
package org.raml.parser.visitor;

import static org.raml.parser.visitor.TupleType.KEY;

import java.lang.reflect.Field;
import java.util.List;

import org.raml.model.Raml;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.rule.DefaultTupleRule;
import org.raml.parser.rule.ImplicitMapEntryRule;
import org.raml.parser.rule.NodeRule;
import org.raml.parser.rule.NodeRuleFactory;
import org.raml.parser.rule.ValidationResult;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class RamlDocumentValidator extends YamlDocumentValidator
{

    private TemplateResolver templateResolver;
    private MediaTypeResolver mediaTypeResolver = new MediaTypeResolver();
    private ResourceLoader resourceLoader;

    public RamlDocumentValidator()
    {
        super(Raml.class);
    }

    public RamlDocumentValidator(NodeRuleFactory nodeRuleFactory)
    {
        super(Raml.class, nodeRuleFactory);
    }

    public TemplateResolver getTemplateResolver()
    {
        if (templateResolver == null)
        {
            templateResolver = new TemplateResolver(resourceLoader, this, true);
        }
        return templateResolver;
    }

    public MediaTypeResolver getMediaTypeResolver()
    {
        return mediaTypeResolver;
    }

    @Override
    public void onMappingNodeStart(MappingNode mappingNode, TupleType tupleType)
    {
        super.onMappingNodeStart(mappingNode, tupleType);
        if (tupleType == KEY)
        {
            return;
        }
        NodeRule<?> rule = getRuleContext().peek();
        if (isResourceRule(rule))
        {
            List<ValidationResult> templateValidations = getTemplateResolver().resolve(
                    mappingNode, getResourceUri(rule), getFullUri(rule));
            getMessages().addAll(templateValidations);
        }
        else if (isBodyRule(rule))
        {
            List<ValidationResult> mediaTypeValidations = getMediaTypeResolver().resolve(mappingNode);
            getMessages().addAll(mediaTypeValidations);
        }
    }

    private String getResourceUri(NodeRule<?> resourceRule)
    {
        Node keyNode = ((DefaultTupleRule) resourceRule).getKey();
        return ((ScalarNode) keyNode).getValue();
    }

    private String getFullUri(NodeRule<?> resourceRule)
    {
        String fullUri = "";
        while (resourceRule instanceof ImplicitMapEntryRule)
        {
            Node keyNode = ((DefaultTupleRule) resourceRule).getKey();
            fullUri = ((ScalarNode) keyNode).getValue() + fullUri;
            resourceRule = ((DefaultTupleRule) resourceRule).getParentTupleRule();
        }
        return fullUri;
    }

    private boolean isBodyRule(NodeRule<?> rule)
    {
        try
        {
            Field valueType = rule.getClass().getDeclaredField("valueType");
            valueType.setAccessible(true);
            return ((Class) valueType.get(rule)).getName().equals("org.raml.model.MimeType");
        }
        catch (NoSuchFieldException e)
        {
            return false;
        }
        catch (IllegalAccessException e)
        {
            return false;
        }
    }

    private boolean isResourceRule(NodeRule<?> rule)
    {
        try
        {
            Field valueType = rule.getClass().getDeclaredField("valueType");
            valueType.setAccessible(true);
            return ((Class) valueType.get(rule)).getName().equals("org.raml.model.Resource");
        }
        catch (NoSuchFieldException e)
        {
            return false;
        }
        catch (IllegalAccessException e)
        {
            return false;
        }
    }

    public void setResourceLoader(ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }
}
