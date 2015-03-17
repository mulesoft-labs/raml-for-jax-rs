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
import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Scalar;

/**
 * <p>Response class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class Response
{

    @Scalar
    private String description;

    @Mapping
    private Map<String, MimeType> body = new HashMap<String, MimeType>();

    @Mapping
    private Map<String, Header> headers = new HashMap<String, Header>();

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
     * <p>Getter for the field <code>description</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * <p>Setter for the field <code>description</code>.</p>
     *
     * @param description a {@link java.lang.String} object.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * <p>Setter for the field <code>body</code>.</p>
     *
     * @param body a {@link java.util.Map} object.
     */
    public void setBody(Map<String, MimeType> body)
    {
        this.body = body;
    }

    /**
     * <p>Getter for the field <code>body</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, MimeType> getBody()
    {
        return body;
    }
}
