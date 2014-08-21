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

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.raml.parser.resolver.TupleHandler;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;

public interface TupleRule<K extends Node, V extends Node> extends NodeRule<V>
{

    List<ValidationResult> validateKey(K key);

    TupleRule<?, ?> getRuleForTuple(NodeTuple nodeTuple);

    void setParentTupleRule(TupleRule<?, ?> parent);

    TupleRule<?, ?> getParentTupleRule();

    TupleRule<?, ?> getRootTupleRule();

    String getName();

    void setName(String name);

    TupleRule<?, ?> getRuleByFieldName(String fieldName);

    void setNestedRules(Map<String, TupleRule<?, ?>> innerBuilders);

    void setHandler(TupleHandler tupleHandler);

    TupleHandler getHandler();

    void setRequired(boolean required);

    void setNodeRuleFactory(NodeRuleFactory nodeRuleFactory);

    K getKey();

    void setValueType(Type valueType);
}
