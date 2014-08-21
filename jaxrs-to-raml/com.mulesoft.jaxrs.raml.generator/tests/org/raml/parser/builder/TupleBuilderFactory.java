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

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.raml.parser.annotation.ExtraHandler;
import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Scalar;
import org.raml.parser.annotation.Sequence;
import org.raml.parser.resolver.EnumHandler;
import org.raml.parser.resolver.TupleHandler;
import org.raml.parser.utils.ReflectionUtils;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class TupleBuilderFactory extends AbastractFactory
{

    public void addBuildersTo(Class<?> pojoClass, TupleBuilder parent)
    {
        final List<Field> declaredFields = ReflectionUtils.getInheritedFields(pojoClass);
        final Map<String, TupleBuilder<?, ?>> innerBuilders = new HashMap<String, TupleBuilder<?, ?>>();
        for (Field declaredField : declaredFields)
        {
            Scalar scalar = declaredField.getAnnotation(Scalar.class);
            Mapping mapping = declaredField.getAnnotation(Mapping.class);
            Sequence sequence = declaredField.getAnnotation(Sequence.class);
            TupleBuilder<?, ?> tupleBuilder = null;
            TupleHandler tupleHandler = null;
            if (scalar != null)
            {
                tupleBuilder = createScalarBuilder(declaredField, scalar);
                tupleHandler = createHandler(scalar.handler(), scalar.alias(), ScalarNode.class);

            }
            else if (mapping != null)
            {
                tupleBuilder = createMappingBuilder(declaredField, mapping);
                tupleHandler = createHandler(mapping.handler(), mapping.alias(), MappingNode.class);
            }
            else if (sequence != null)
            {
                tupleBuilder = createSequenceBuilder(declaredField, sequence);
                tupleHandler = createHandler(sequence.handler(), sequence.alias(), SequenceNode.class);
            }

            if (tupleBuilder != null)
            {
                if (tupleHandler != null)
                {
                    tupleBuilder.setHandler(tupleHandler);
                }
                tupleBuilder.setParentNodeBuilder(parent);
                innerBuilders.put(declaredField.getName(), tupleBuilder);
            }
        }
        parent.setNestedBuilders(innerBuilders);
    }

    private TupleBuilder<?, ?> createSequenceBuilder(Field declaredField, Sequence sequence)
    {
        TupleBuilder<?, ?> tupleBuilder = null;
        if (sequence.builder() != TupleBuilder.class)
        {
            tupleBuilder = createInstanceOf(sequence.builder());
        }
        else
        {
            if (List.class.isAssignableFrom(declaredField.getType()))
            {
                Type type = declaredField.getGenericType();
                if (type instanceof ParameterizedType)
                {
                    ParameterizedType pType = (ParameterizedType) type;
                    Type itemType = pType.getActualTypeArguments()[0];
                    Class<? extends ExtraHandler> extraHandler = sequence.extraHandler();
                    tupleBuilder = new SequenceTupleBuilder(declaredField.getName(), itemType,extraHandler);
                }
            }
            else
            {
                throw new RuntimeException("Only List can be sequence. Error on field " + declaredField.getName());
            }
        }
        return tupleBuilder;
    }

    private TupleBuilder<?, ?> createScalarBuilder(Field declaredField, Scalar scalar)
    {
        TupleBuilder<?, ?> tupleBuilder;
        if (scalar.builder() != TupleBuilder.class)
        {
            tupleBuilder = createInstanceOf(scalar.builder());
        }
        else
        {
            if (ReflectionUtils.isPojo(declaredField.getType()))
            {
                tupleBuilder = new PojoTupleBuilder(declaredField.getName(), declaredField.getType());
            }
            else
            {
                tupleBuilder = new ScalarTupleBuilder(declaredField.getName(), declaredField.getType(),scalar.includeField());
            }
        }
        return tupleBuilder;
    }

    private TupleBuilder<?, ?> createMappingBuilder(Field declaredField, Mapping mapping)
    {
        TupleBuilder<?, ?> tupleBuilder = null;
        if (mapping.builder() != TupleBuilder.class)
        {
            tupleBuilder = createInstanceOf(mapping.builder());
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
                                tupleBuilder = new ImplicitMapEntryBuilder(declaredField.getName(), keyClass, (Class) valueType);
                            }
                            else
                            {
                                tupleBuilder = new MapTupleBuilder(declaredField.getName(), (Class) valueType);
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
                                    tupleBuilder = new MapWithListValueTupleBuilder(declaredField.getName(), (Class<?>) listType);
                                }
                            }
                        }
                        if (keyClass.isEnum())
                        {
                            tupleBuilder.setHandler(new EnumHandler(MappingNode.class, (Class<? extends Enum>) keyClass));
                        }
                    }
                }
            }
            else
            {
                tupleBuilder = new PojoTupleBuilder(declaredField.getName(), declaredField.getType());
            }
        }
        return tupleBuilder;
    }


}
