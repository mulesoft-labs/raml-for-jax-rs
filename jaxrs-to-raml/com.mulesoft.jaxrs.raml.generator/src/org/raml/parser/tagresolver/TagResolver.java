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

import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.visitor.NodeHandler;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

/**
 * <p>TagResolver interface.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public interface TagResolver
{

    /**
     * <p>handles.</p>
     *
     * @param tag a {@link org.yaml.snakeyaml.nodes.Tag} object.
     * @return a boolean.
     */
    boolean handles(Tag tag);

    /**
     * <p>resolve.</p>
     *
     * @param valueNode a {@link org.yaml.snakeyaml.nodes.Node} object.
     * @param resourceLoader a {@link org.raml.parser.loader.ResourceLoader} object.
     * @param nodeHandler a {@link org.raml.parser.visitor.NodeHandler} object.
     * @return a {@link org.yaml.snakeyaml.nodes.Node} object.
     */
    Node resolve(Node valueNode, ResourceLoader resourceLoader, NodeHandler nodeHandler);

}
