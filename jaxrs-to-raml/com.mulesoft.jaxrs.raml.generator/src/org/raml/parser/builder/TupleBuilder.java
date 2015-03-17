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

import java.util.Map;

import org.raml.parser.resolver.TupleHandler;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;

/**
 * <p>TupleBuilder interface.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public interface TupleBuilder<K extends Node, V extends Node> extends NodeBuilder<V>
{

    /**
     * Returns the
     *
     * @param tuple a {@link org.yaml.snakeyaml.nodes.NodeTuple} object.
     * @return a {@link org.raml.parser.builder.NodeBuilder} object.
     */
    NodeBuilder getBuilderForTuple(NodeTuple tuple);

    /**
     * <p>buildKey.</p>
     *
     * @param parent a {@link java.lang.Object} object.
     * @param tuple a K object.
     */
    void buildKey(Object parent, K tuple);

    /**
     * <p>setHandler.</p>
     *
     * @param handler a {@link org.raml.parser.resolver.TupleHandler} object.
     */
    void setHandler(TupleHandler handler);

    /**
     * <p>getHandler.</p>
     *
     * @return a {@link org.raml.parser.resolver.TupleHandler} object.
     */
    TupleHandler getHandler();

    /**
     * <p>setNestedBuilders.</p>
     *
     * @param nestedBuilders a {@link java.util.Map} object.
     */
    void setNestedBuilders(Map<String, TupleBuilder<?,?>> nestedBuilders);

}
