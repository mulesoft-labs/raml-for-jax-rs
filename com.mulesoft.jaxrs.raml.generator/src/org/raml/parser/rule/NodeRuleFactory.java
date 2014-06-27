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

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Scalar;
import org.raml.parser.annotation.Sequence;
import org.raml.parser.builder.AbastractFactory;
import org.raml.parser.resolver.DefaultTupleHandler;
import org.raml.parser.resolver.EnumHandler;
import org.raml.parser.resolver.TupleHandler;
import org.raml.parser.utils.ReflectionUtils;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class NodeRuleFactory extends AbastractFactory
{

    private NodeRuleFactoryExtension[] extensions;


    public NodeRuleFactory(NodeRuleFactoryExtension... extensions)
    {
        this.extensions = extensions;
    }

    public DefaultTupleRule<Node, MappingNode> createDocumentRule(Class<?> documentClass)
    {
        DefaultTupleRule<Node, MappingNode> documentRule = new DefaultTupleRule<Node, MappingNode>(null, new DefaultTupleHandler());
        documentRule.setNodeRuleFactory(this);
        documentRule.addRulesFor(documentClass);
        return documentRule;
    }


    public void addRulesTo(Class<?> pojoClass, TupleRule<?, ?> parent)
    {
        final List<Field> declaredFields = ReflectionUtils.getInheritedFields(pojoClass);
        final Map<String, TupleRule<?, ?>> innerBuilders = new HashMap<String, TupleRule<?, ?>>();
        for (Field declaredField : declaredFields)
        {
            Scalar scalar = declaredField.getAnnotation(Scalar.class);
            Mapping mapping = declaredField.getAnnotation(Mapping.class);
            Sequence sequence = declaredField.getAnnotation(Sequence.class);
            TupleRule<?, ?> tupleRule = null;
            TupleHandler tupleHandler = null;
            boolean required = false;
            if (scalar != null)
            {
                tupleRule = createScalarRule(declaredField, scalar);
                tupleHandler = createHandler(scalar.handler(), scalar.alias(), ScalarNode.class);
                required = scalar.required();
            }
            else if (mapping != null)
            {
                tupleRule = createMappingRule(declaredField, mapping);
                tupleHandler = createHandler(mapping.handler(), mapping.alias(), MappingNode.class);
                required = mapping.required();
            }
            else if (sequence != null)
            {
                tupleRule = createSequenceRule(declaredField, sequence);
                tupleHandler = createHandler(sequence.handler(), sequence.alias(), SequenceNode.class);
                required = sequence.required();
            }

            if (tupleRule != null)
            {
                if (tupleHandler != null)
                {
                    tupleRule.setHandler(tupleHandler);
                }
                tupleRule.setRequired(required);
                tupleRule.setParentTupleRule(parent);
                tupleRule.setNodeRuleFactory(this);
                innerBuilders.put(declaredField.getName(), tupleRule);
            }
        }
        parent.setNestedRules(innerBuilders);
    }

    private TupleRule<?, ?> createSequenceRule(Field declaredField, Sequence sequence)
    {
        TupleRule<?, ?> tupleRule = null;
        if (List.class.isAssignableFrom(declaredField.getType()))
        {
            Type type = declaredField.getGenericType();
            if (type instanceof ParameterizedType)
            {
                ParameterizedType pType = (ParameterizedType) type;
                Type itemType = pType.getActualTypeArguments()[0];
                if (sequence.rule() != TupleRule.class)
                {
                    tupleRule = createInstanceOfTupleRule(sequence.rule(), declaredField.getName(), itemType);
                }
                else
                {
                    tupleRule = new SequenceTupleRule(declaredField.getName(), itemType);
                }
            }
        }
        else
        {
            throw new RuntimeException("Only List can be sequence. Error on field " + declaredField.getName());
        }

        return tupleRule;
    }

    private TupleRule<?, ?> createMappingRule(Field declaredField, Mapping mapping)
    {
        TupleRule<?, ?> tupleRule = null;
        if (mapping.rule() != TupleRule.class)
        {
            tupleRule = createInstanceOf(mapping.rule());
        }
        else
        {
            if (Map.class.isAssignableFrom(declaredField.getType()))
            {
                Type type = declaredField.getGenericType();
                if (type instanceof ParameterizedType)
                {
                    ParameterizedType pType = (ParameterizedType) type;
                    Type keyType = pType.getActualTypeArguments()[0];
                    Type valueType = pType.getActualTypeArguments()[1];
                    if (keyType instanceof Class<?>)
                    {
                        Class<?> keyClass = (Class<?>) keyType;
                        if (valueType instanceof Class<?>)
                        {

                            if (mapping.implicit())
                            {
                                tupleRule = new ImplicitMapEntryRule(declaredField.getName(), (Class) valueType);
                            }
                            else
                            {
                                tupleRule = new MapTupleRule(declaredField.getName(), (Class) valueType);
                            }
                            if (keyClass.isEnum())
                            {
                                tupleRule.setHandler(new EnumHandler(MappingNode.class, (Class<? extends Enum>) keyClass));
                            }

                        }
                        else if (valueType instanceof ParameterizedType)
                        {
                            Type rawType = ((ParameterizedType) valueType).getRawType();
                            if (rawType instanceof Class && List.class.isAssignableFrom((Class<?>) rawType))
                            {
                                Type listType = ((ParameterizedType) valueType).getActualTypeArguments()[0];
                                if (listType instanceof Class)
                                {
                                    tupleRule = new MapWithListValueTupleRule(declaredField.getName(), (Class<?>) listType, this);
                                }
                            }
                        }

                    }
                }
            }
            else
            {
                tupleRule = new PojoTupleRule(declaredField.getName(), declaredField.getType());
            }
        }
        List<TupleRule> contributionRules = new ArrayList<TupleRule>();
        for (NodeRuleFactoryExtension extension : extensions)
        {
            if (extension.handles(declaredField, mapping))
            {
                TupleRule<?, ?> rule = extension.createRule(declaredField, mapping);
                contributionRules.add(rule);
            }
        }
        if (!contributionRules.isEmpty())
        {
            tupleRule = new ContributionTupleRule(tupleRule, contributionRules);
        }

        return tupleRule;
    }

    private TupleRule<?, ?> createScalarRule(Field declaredField, Scalar scalar)
    {
        TupleRule<?, ?> tupleRule;
        if (scalar.rule() != TupleRule.class)
        {
            tupleRule = createInstanceOfTupleRule(scalar.rule(), declaredField.getName(), declaredField.getType());
        }
        else
        {
            if (ReflectionUtils.isPojo(declaredField.getType()))
            {
                tupleRule = new PojoTupleRule(declaredField.getName(), declaredField.getType());
            }
            else
            {
                tupleRule = new SimpleRule(declaredField.getName(), declaredField.getType());
            }
        }
        return tupleRule;
    }

    private TupleRule<?, ?> createInstanceOfTupleRule(Class<? extends TupleRule> rule, String fieldName, Type valueType)
    {
        TupleRule tupleRule = createInstanceOf(rule);
        tupleRule.setName(fieldName);
        tupleRule.setValueType(valueType);
        tupleRule.setNodeRuleFactory(this);
        return tupleRule;
    }
}
