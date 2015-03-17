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

import static java.lang.System.currentTimeMillis;
import static org.raml.parser.rule.ValidationResult.createErrorResult;
import static org.yaml.snakeyaml.nodes.NodeId.mapping;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.tagresolver.TagResolver;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.MarkedYAMLException;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;

/**
 * <p>YamlValidationService class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class YamlValidationService
{

    //protected final Logger logger = LoggerFactory.getLogger(getClass());

    private List<ValidationResult> errorMessage;
    private YamlValidator yamlValidator;
    private ResourceLoader resourceLoader;
    private TagResolver[] tagResolvers;

    /**
     * <p>Constructor for YamlValidationService.</p>
     *
     * @param resourceLoader a {@link org.raml.parser.loader.ResourceLoader} object.
     * @param yamlValidator a {@link org.raml.parser.visitor.YamlValidator} object.
     * @param tagResolvers an array of {@link org.raml.parser.tagresolver.TagResolver} objects.
     */
    protected YamlValidationService(ResourceLoader resourceLoader, YamlValidator yamlValidator, TagResolver[] tagResolvers)
    {
        this.resourceLoader = resourceLoader;
        this.yamlValidator = yamlValidator;
        this.errorMessage = new ArrayList<ValidationResult>();
        this.tagResolvers = tagResolvers;
    }

    /**
     * <p>validate.</p>
     *
     * @param root a {@link org.yaml.snakeyaml.nodes.MappingNode} object.
     * @return a {@link java.util.List} object.
     */
    public List<ValidationResult> validate(MappingNode root)
    {
        NodeVisitor nodeVisitor = new NodeVisitor(yamlValidator, resourceLoader, tagResolvers);
        errorMessage.addAll(preValidation(root));
        nodeVisitor.visitDocument(root);
        return errorMessage;
    }

    /**
     * <p>validate.</p>
     *
     * @param content a {@link java.io.InputStream} object.
     * @return a {@link java.util.List} object.
     */
    public List<ValidationResult> validate(InputStream content)
    {
        return validate(new InputStreamReader(content));
    }

    /**
     * <p>validate.</p>
     *
     * @param content a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    public List<ValidationResult> validate(String content)
    {
        return validate(new StringReader(content));
    }

    /**
     * <p>validate.</p>
     *
     * @param content a {@link java.io.Reader} object.
     * @return a {@link java.util.List} object.
     */
    public List<ValidationResult> validate(Reader content)
    {
        long startTime = currentTimeMillis();

        Yaml yamlParser = new Yaml();

        try
        {
            Node root = yamlParser.compose(content);
            if (root != null && root.getNodeId() == mapping)
            {
                validate((MappingNode) root);
            }
            else
            {
                errorMessage.add(createErrorResult("Invalid RAML"));
            }
        }
        catch (MarkedYAMLException mye)
        {
            errorMessage.add(createErrorResult(mye.getProblem(), mye.getProblemMark(), mye.getProblemMark()));
        }
        catch (YAMLException ex)
        {
            errorMessage.add(createErrorResult(ex.getMessage()));
        }

        errorMessage.addAll(yamlValidator.getMessages());

        /*if (logger.isDebugEnabled())
        {
            logger.debug("validation time: " + (currentTimeMillis() - startTime) + "ms.");
        }*/

        return errorMessage;
    }

    /**
     * <p>preValidation.</p>
     *
     * @param root a {@link org.yaml.snakeyaml.nodes.MappingNode} object.
     * @return a {@link java.util.List} object.
     */
    protected List<ValidationResult> preValidation(MappingNode root)
    {
        //template method
        return new ArrayList<ValidationResult>();
    }

}
