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

import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.raml.parser.resolver.TupleHandler;
import org.yaml.snakeyaml.nodes.Node;

public class AbastractFactory
{

    protected TupleHandler createHandler(Class<? extends TupleHandler> handler, String alias,
                                         Class<? extends Node> nodeClass)
    {
        TupleHandler tupleHandler = null;
        if (handler != TupleHandler.class)
        {
            tupleHandler = createInstanceOf(handler);
        }
        else if (!alias.isEmpty())
        {
            tupleHandler = new DefaultScalarTupleHandler(alias);
        }
        return tupleHandler;
    }

    protected <T> T createInstanceOf(Class<? extends T> handler)
    {
        try
        {
            return handler.newInstance();
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
