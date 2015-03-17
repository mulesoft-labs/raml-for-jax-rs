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

import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

/**
 * <p>NodeHandler interface.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public interface NodeHandler
{

    /**
     * <p>onMappingNodeStart.</p>
     *
     * @param mappingNode a {@link org.yaml.snakeyaml.nodes.MappingNode} object.
     * @param tupleType a {@link org.raml.parser.visitor.TupleType} object.
     */
    void onMappingNodeStart(MappingNode mappingNode, TupleType tupleType);

    /**
     * <p>onMappingNodeEnd.</p>
     *
     * @param mappingNode a {@link org.yaml.snakeyaml.nodes.MappingNode} object.
     * @param tupleType a {@link org.raml.parser.visitor.TupleType} object.
     */
    void onMappingNodeEnd(MappingNode mappingNode, TupleType tupleType);

    /**
     * <p>onSequenceStart.</p>
     *
     * @param node a {@link org.yaml.snakeyaml.nodes.SequenceNode} object.
     * @param tupleType a {@link org.raml.parser.visitor.TupleType} object.
     */
    void onSequenceStart(SequenceNode node, TupleType tupleType);

    /**
     * <p>onSequenceEnd.</p>
     *
     * @param node a {@link org.yaml.snakeyaml.nodes.SequenceNode} object.
     * @param tupleType a {@link org.raml.parser.visitor.TupleType} object.
     */
    void onSequenceEnd(SequenceNode node, TupleType tupleType);

    /**
     * <p>onScalar.</p>
     *
     * @param node a {@link org.yaml.snakeyaml.nodes.ScalarNode} object.
     * @param tupleType a {@link org.raml.parser.visitor.TupleType} object.
     */
    void onScalar(ScalarNode node, TupleType tupleType);

    /**
     * <p>onDocumentStart.</p>
     *
     * @param node a {@link org.yaml.snakeyaml.nodes.MappingNode} object.
     */
    void onDocumentStart(MappingNode node);

    /**
     * <p>onDocumentEnd.</p>
     *
     * @param node a {@link org.yaml.snakeyaml.nodes.MappingNode} object.
     */
    void onDocumentEnd(MappingNode node);

    /**
     * <p>onTupleEnd.</p>
     *
     * @param nodeTuple a {@link org.yaml.snakeyaml.nodes.NodeTuple} object.
     */
    void onTupleEnd(NodeTuple nodeTuple);

    /**
     * <p>onTupleStart.</p>
     *
     * @param nodeTuple a {@link org.yaml.snakeyaml.nodes.NodeTuple} object.
     */
    void onTupleStart(NodeTuple nodeTuple);

    /**
     * <p>onSequenceElementStart.</p>
     *
     * @param sequenceNode a {@link org.yaml.snakeyaml.nodes.Node} object.
     */
    void onSequenceElementStart(Node sequenceNode);

    /**
     * <p>onSequenceElementEnd.</p>
     *
     * @param sequenceNode a {@link org.yaml.snakeyaml.nodes.Node} object.
     */
    void onSequenceElementEnd(Node sequenceNode);

    /**
     * <p>onCustomTagStart.</p>
     *
     * @param tag a {@link org.yaml.snakeyaml.nodes.Tag} object.
     * @param originalValueNode a {@link org.yaml.snakeyaml.nodes.Node} object.
     * @param node a {@link org.yaml.snakeyaml.nodes.Node} object.
     */
    void onCustomTagStart(Tag tag, Node originalValueNode, Node node);

    /**
     * <p>onCustomTagEnd.</p>
     *
     * @param tag a {@link org.yaml.snakeyaml.nodes.Tag} object.
     * @param originalValueNode a {@link org.yaml.snakeyaml.nodes.Node} object.
     * @param node a {@link org.yaml.snakeyaml.nodes.Node} object.
     */
    void onCustomTagEnd(Tag tag, Node originalValueNode, Node node);

    /**
     * <p>onCustomTagError.</p>
     *
     * @param tag a {@link org.yaml.snakeyaml.nodes.Tag} object.
     * @param node a {@link org.yaml.snakeyaml.nodes.Node} object.
     * @param message a {@link java.lang.String} object.
     */
    void onCustomTagError(Tag tag, Node node, String message);
}
