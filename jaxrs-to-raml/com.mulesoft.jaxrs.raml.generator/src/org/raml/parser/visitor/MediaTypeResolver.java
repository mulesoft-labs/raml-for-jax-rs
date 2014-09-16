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

import static org.raml.parser.rule.ValidationResult.createErrorResult;
import static org.yaml.snakeyaml.nodes.NodeId.scalar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.raml.parser.rule.ValidationResult;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

public class MediaTypeResolver
{

    private static Set<String> MEDIA_TYPE_KEYS;
    private String mediaType;

    static
    {
        String[] keys = {"schema", "example", "formParameters"};
        MEDIA_TYPE_KEYS = new HashSet<String>(Arrays.asList(keys));
    }

    public List<ValidationResult> beforeDocumentStart(MappingNode rootNode)
    {
        List<ValidationResult> validationResults = new ArrayList<ValidationResult>();

        if (rootNode == null)
        {
            validationResults.add(createErrorResult("Invalid Root Node"));
            return validationResults;
        }

        for (NodeTuple tuple : rootNode.getValue())
        {
            if (tuple.getKeyNode().getNodeId() != scalar)
            {
                continue;
            }
            String key = ((ScalarNode) tuple.getKeyNode()).getValue();
            if (key.equals("mediaType"))
            {
                Node valueNode = tuple.getValueNode();
                if (valueNode.getNodeId() != scalar)
                {
                    validationResults.add(createErrorResult("Invalid mediaType", valueNode.getStartMark(), valueNode.getEndMark()));
                    break;
                }
                String value = ((ScalarNode) valueNode).getValue();
                if (!isValidMediaType(value))
                {
                    validationResults.add(createErrorResult("Invalid mediaType", valueNode.getStartMark(), valueNode.getEndMark()));
                    break;
                }
                mediaType = value;
                break;
            }
        }
        return validationResults;
    }

    private boolean isValidMediaType(String value)
    {
        return value.matches(".+/.+");
    }

    public List<ValidationResult> resolve(MappingNode bodyNode)
    {
        List<ValidationResult> validationResults = new ArrayList<ValidationResult>();
        if (mediaType == null)
        {
            return validationResults;
        }
        for (NodeTuple tuple : bodyNode.getValue())
        {
            if (!MEDIA_TYPE_KEYS.contains(((ScalarNode) tuple.getKeyNode()).getValue()))
            {
                return validationResults;
            }
        }
        List<NodeTuple> copy = new ArrayList<NodeTuple>(bodyNode.getValue());
        Node keyNode = new ScalarNode(Tag.STR, mediaType, null, null, null);
        Node valueNode = new MappingNode(Tag.MAP, copy, false);
        bodyNode.getValue().clear();
        bodyNode.getValue().add(new NodeTuple(keyNode, valueNode));
        return validationResults;
    }

}
