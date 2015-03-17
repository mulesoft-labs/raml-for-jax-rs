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

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.raml.parser.loader.DefaultResourceLoader;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.rule.NodeRuleFactory;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.tagresolver.IncludeResolver;
import org.raml.parser.tagresolver.PojoValidatorTagResolver;
import org.raml.parser.tagresolver.TagResolver;
import org.yaml.snakeyaml.nodes.MappingNode;

/**
 * <p>RamlValidationService class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class RamlValidationService extends YamlValidationService
{

    private RamlDocumentValidator validator;

    /**
     * <p>Constructor for RamlValidationService.</p>
     *
     * @param resourceLoader a {@link org.raml.parser.loader.ResourceLoader} object.
     * @param ramlDocumentValidator a {@link org.raml.parser.visitor.RamlDocumentValidator} object.
     * @param tagResolvers a {@link org.raml.parser.tagresolver.TagResolver} object.
     */
    public RamlValidationService(ResourceLoader resourceLoader, RamlDocumentValidator ramlDocumentValidator, TagResolver... tagResolvers)
    {
        super(resourceLoader, ramlDocumentValidator, defaultResolver(tagResolvers));
        validator = ramlDocumentValidator;
        validator.setResourceLoader(resourceLoader);
    }

    private static TagResolver[] defaultResolver(TagResolver[] tagResolvers)
    {
        TagResolver[] defaultResolvers = new TagResolver[] {
                new IncludeResolver(),
                new PojoValidatorTagResolver()
        };
        return (TagResolver[]) ArrayUtils.addAll(defaultResolvers, tagResolvers);
    }

    
    /** {@inheritDoc} */
    protected List<ValidationResult> preValidation(MappingNode root)
    {
        List<ValidationResult> validationResults = validator.getTemplateResolver().init(root);
        validationResults.addAll(validator.getMediaTypeResolver().beforeDocumentStart(root));
        return validationResults;
    }

    /**
     * <p>createDefault.</p>
     *
     * @return a {@link org.raml.parser.visitor.RamlValidationService} object.
     */
    public static RamlValidationService createDefault()
    {
        return createDefault(new DefaultResourceLoader());
    }

    /**
     * <p>createDefault.</p>
     *
     * @param loader a {@link org.raml.parser.loader.ResourceLoader} object.
     * @param tagResolvers a {@link org.raml.parser.tagresolver.TagResolver} object.
     * @return a {@link org.raml.parser.visitor.RamlValidationService} object.
     */
    public static RamlValidationService createDefault(ResourceLoader loader, TagResolver... tagResolvers)
    {
        return createDefault(loader, new NodeRuleFactory(), tagResolvers);
    }

    /**
     * <p>createDefault.</p>
     *
     * @param loader a {@link org.raml.parser.loader.ResourceLoader} object.
     * @param nodeRuleFactory a {@link org.raml.parser.rule.NodeRuleFactory} object.
     * @param tagResolvers a {@link org.raml.parser.tagresolver.TagResolver} object.
     * @return a {@link org.raml.parser.visitor.RamlValidationService} object.
     */
    public static RamlValidationService createDefault(ResourceLoader loader, NodeRuleFactory nodeRuleFactory, TagResolver... tagResolvers)
    {
        return new RamlValidationService(loader, new RamlDocumentValidator(nodeRuleFactory), tagResolvers);
    }

}
