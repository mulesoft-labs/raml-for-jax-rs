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
package org.raml.parser.tagresolver;

import static org.yaml.snakeyaml.nodes.NodeId.scalar;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.visitor.NodeHandler;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

public class IncludeResolver implements TagResolver
{

    public static final Tag INCLUDE_TAG = new Tag("!include");
    public static final String SEPARATOR = "_";
    public static final String INCLUDE_APPLIED_TAG = "!include-applied" + SEPARATOR;

    @Override
    public boolean handles(Tag tag)
    {
        return INCLUDE_TAG.equals(tag) || tag.startsWith(INCLUDE_APPLIED_TAG);
    }

    @Override
    public Node resolve(Node node, ResourceLoader resourceLoader, NodeHandler nodeHandler)
    {
        if (node.getTag().startsWith(INCLUDE_APPLIED_TAG))
        {
            //already resolved
            return node;
        }

        Node includeNode;
        InputStream inputStream = null;
        try
        {
            if (node.getNodeId() != scalar)
            {
                nodeHandler.onCustomTagError(INCLUDE_TAG, node, "Include cannot be non-scalar");
                return mockInclude(node);
            }
            ScalarNode scalarNode = (ScalarNode) node;            
            String resourceName = scalarNode.getValue();
            inputStream = resourceLoader.fetchResource(resourceName);

            if (inputStream == null)
            {
                nodeHandler.onCustomTagError(INCLUDE_TAG, node, "Include cannot be resolved " + resourceName);
                return mockInclude(node);
            }
            else if (resourceName.endsWith(".raml") || resourceName.endsWith(".yaml") || resourceName.endsWith(".yml"))
            {
                Yaml yamlParser = new Yaml();
                includeNode = yamlParser.compose(new InputStreamReader(inputStream));
            }
            else //scalar value
            {
                String newValue = IOUtils.toString(inputStream, "UTF-8");
                includeNode = new IncludeScalarNode(resourceName, newValue, scalarNode);
            }
            if (includeNode == null)
            {
                nodeHandler.onCustomTagError(INCLUDE_TAG, node, "Include file is empty " + resourceName);
                return mockInclude(node);
            }
            //retag node with included resource info
            String markInfo = node.getStartMark().getLine() + SEPARATOR + node.getStartMark().getColumn()
                              + SEPARATOR + node.getEndMark().getColumn();
            includeNode.setTag(new Tag(INCLUDE_APPLIED_TAG + resourceName + SEPARATOR + markInfo));
            return includeNode;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            try
            {
                if (inputStream != null)
                {
                    inputStream.close();
                }
            }
            catch (IOException e)
            {
                //ignore
            }
        }
    }

    private Node mockInclude(Node node)
    {
        return new ScalarNode(Tag.STR, "invalid", node.getStartMark(), node.getEndMark(), null);
    }

    public static class IncludeScalarNode extends ScalarNode
    {
        private String includeName;

        public IncludeScalarNode(String includeName, String value, ScalarNode node)
        {
            super(Tag.STR, value, node.getStartMark(), node.getEndMark(), node.getStyle());
            this.includeName = includeName;
        }

        public String getIncludeName()
        {
            return includeName;
        }
    }
}
