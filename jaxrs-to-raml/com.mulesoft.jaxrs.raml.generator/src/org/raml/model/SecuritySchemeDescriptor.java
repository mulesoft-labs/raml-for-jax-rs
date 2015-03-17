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
import java.util.Map;

import org.raml.model.parameter.Header;
import org.raml.model.parameter.QueryParameter;
import org.raml.parser.annotation.Mapping;

/**
 * <p>SecuritySchemeDescriptor class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class SecuritySchemeDescriptor
{

    @Mapping
    private Map<String, Header> headers = new HashMap<String, Header>();

    @Mapping
    private Map<String, QueryParameter> queryParameters = new HashMap<String, QueryParameter>();

    @Mapping
    private Map<String, Response> responses = new HashMap<String, Response>();

    /**
     * <p>Getter for the field <code>headers</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, Header> getHeaders()
    {
        return headers;
    }

    /**
     * <p>Setter for the field <code>headers</code>.</p>
     *
     * @param headers a {@link java.util.Map} object.
     */
    public void setHeaders(Map<String, Header> headers)
    {
        this.headers = headers;
    }

    /**
     * <p>Getter for the field <code>queryParameters</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, QueryParameter> getQueryParameters()
    {
        return queryParameters;
    }

    /**
     * <p>Setter for the field <code>queryParameters</code>.</p>
     *
     * @param queryParameters a {@link java.util.Map} object.
     */
    public void setQueryParameters(Map<String, QueryParameter> queryParameters)
    {
        this.queryParameters = queryParameters;
    }

    /**
     * <p>Getter for the field <code>responses</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, Response> getResponses()
    {
        return responses;
    }

    /**
     * <p>Setter for the field <code>responses</code>.</p>
     *
     * @param responses a {@link java.util.Map} object.
     */
    public void setResponses(Map<String, Response> responses)
    {
        this.responses = responses;
    }
}
