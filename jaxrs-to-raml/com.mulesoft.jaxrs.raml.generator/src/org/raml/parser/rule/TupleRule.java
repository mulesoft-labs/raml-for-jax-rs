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

/**
 * <p>TupleRule interface.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public interface TupleRule<K extends Node, V extends Node> extends NodeRule<V>
{

    /**
     * <p>validateKey.</p>
     *
     * @param key a K object.
     * @return a {@link java.util.List} object.
     */
    List<ValidationResult> validateKey(K key);

    /**
     * <p>getRuleForTuple.</p>
     *
     * @param nodeTuple a {@link org.yaml.snakeyaml.nodes.NodeTuple} object.
     * @return a {@link org.raml.parser.rule.TupleRule} object.
     */
    TupleRule<?, ?> getRuleForTuple(NodeTuple nodeTuple);

    /**
     * <p>setParentTupleRule.</p>
     *
     * @param parent a {@link org.raml.parser.rule.TupleRule} object.
     */
    void setParentTupleRule(TupleRule<?, ?> parent);

    /**
     * <p>getParentTupleRule.</p>
     *
     * @return a {@link org.raml.parser.rule.TupleRule} object.
     */
    TupleRule<?, ?> getParentTupleRule();

    /**
     * <p>getRootTupleRule.</p>
     *
     * @return a {@link org.raml.parser.rule.TupleRule} object.
     */
    TupleRule<?, ?> getRootTupleRule();

    /**
     * <p>getName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getName();

    /**
     * <p>setName.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    void setName(String name);

    /**
     * <p>getRuleByFieldName.</p>
     *
     * @param fieldName a {@link java.lang.String} object.
     * @return a {@link org.raml.parser.rule.TupleRule} object.
     */
    TupleRule<?, ?> getRuleByFieldName(String fieldName);

    /**
     * <p>setNestedRules.</p>
     *
     * @param innerBuilders a {@link java.util.Map} object.
     */
    void setNestedRules(Map<String, TupleRule<?, ?>> innerBuilders);

    /**
     * <p>setHandler.</p>
     *
     * @param tupleHandler a {@link org.raml.parser.resolver.TupleHandler} object.
     */
    void setHandler(TupleHandler tupleHandler);

    /**
     * <p>getHandler.</p>
     *
     * @return a {@link org.raml.parser.resolver.TupleHandler} object.
     */
    TupleHandler getHandler();

    /**
     * <p>setRequired.</p>
     *
     * @param required a boolean.
     */
    void setRequired(boolean required);

    /**
     * <p>setNodeRuleFactory.</p>
     *
     * @param nodeRuleFactory a {@link org.raml.parser.rule.NodeRuleFactory} object.
     */
    void setNodeRuleFactory(NodeRuleFactory nodeRuleFactory);

    /**
     * <p>getKey.</p>
     *
     * @return a K object.
     */
    K getKey();

    /**
     * <p>setValueType.</p>
     *
     * @param valueType a {@link java.lang.reflect.Type} object.
     */
    void setValueType(Type valueType);
}
