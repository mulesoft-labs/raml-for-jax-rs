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
package org.raml.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Value;
import org.raml.parser.resolver.MatchAllHandler;

/**
 * <p>SecurityReference class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class SecurityReference
{


    private String name;

    @Mapping(handler = MatchAllHandler.class)
    private Map<String, List<String>> parameters = new HashMap<String, List<String>>();


    /**
     * <p>Constructor for SecurityReference.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public SecurityReference(@Value String name)
    {
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName()
    {
        return name;
    }

    /**
     * <p>Setter for the field <code>name</code>.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>parameters</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, List<String>> getParameters()
    {
        return parameters;
    }

    /**
     * <p>Setter for the field <code>parameters</code>.</p>
     *
     * @param parameters a {@link java.util.Map} object.
     */
    public void setParameters(Map<String, List<String>> parameters)
    {
        this.parameters = parameters;
    }
}
