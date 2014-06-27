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
package org.raml.parser.builder;

import org.yaml.snakeyaml.nodes.Node;

/**
 * Created with IntelliJ IDEA.
 * User: santiagovacas
 * Date: 6/28/13
 * Time: 5:48 PM
 * To change this template use File | Settings | File Templates.
 */
public interface NodeBuilder<V extends Node>
{

    /**
     * Builds the java model for the given node and set it to the parent object
     *
     * @param parent The parent object
     * @param node   The node to build the model from
     * @return The model
     */
    Object buildValue(Object parent, V node);

    /**
     * Sets the parent builder
     * @param parentBuilder
     */
    void setParentNodeBuilder(NodeBuilder parentBuilder);

}
