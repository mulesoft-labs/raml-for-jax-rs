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

import static org.raml.parser.utils.NodeUtils.getNodeValue;
import static org.raml.parser.utils.ReflectionUtils.isPojo;

import java.util.ArrayList;

import org.raml.parser.utils.ReflectionUtils;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class MapWithListValueTupleBuilder extends MapTupleBuilder
{


    public MapWithListValueTupleBuilder(String fieldName, Class<?> valueClass)
    {
        super(fieldName, valueClass);
    }

    @Override
    public TupleBuilder getBuilderForTuple(NodeTuple tuple)
    {
        final String fieldName = ((ScalarNode) tuple.getKeyNode()).getValue();
        if (tuple.getValueNode() instanceof SequenceNode)
        {

            return new SequenceTupleBuilder(fieldName, getValueClass(), null);
        }
        else
        {
            return new ListOfPojoTupleBuilder(fieldName, getValueClass());
        }
    }

    //make non sequence mapping node act as sequence
    private static class ListOfPojoTupleBuilder extends PojoTupleBuilder
    {

        public ListOfPojoTupleBuilder(String fieldName, Class<?> pojoClass)
        {
            super(fieldName, pojoClass);
        }

        @Override
        public Object buildValue(Object parent, Node node)
        {
            try
            {
                Object newValue;
                if (isPojo(getPojoClass()))
                {
                    newValue = getPojoClass().newInstance();
                }
                else
                {
                    newValue = getNodeValue(node);
                }
                ArrayList<Object> objects = new ArrayList<Object>();
                objects.add(newValue);
                ReflectionUtils.setProperty(parent, getFieldName(), objects);
                processPojoAnnotations(newValue, getFieldName(), parent);
                return newValue;
            }
            catch (InstantiationException e)
            {
                throw new RuntimeException(e);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

}
