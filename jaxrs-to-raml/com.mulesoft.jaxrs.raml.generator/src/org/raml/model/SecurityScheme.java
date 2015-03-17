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
import org.raml.parser.annotation.Scalar;

/**
 * <p>SecurityScheme class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class SecurityScheme
{

    @Scalar
    private String description;

    @Scalar
    private String type;

    @Scalar
    private SecuritySchemeDescriptor describedBy;

    @Mapping
    private Map<String, List<String>> settings = new HashMap<String, List<String>>();

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
     * <p>Getter for the field <code>type</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getType()
    {
        return type;
    }

    /**
     * <p>Setter for the field <code>type</code>.</p>
     *
     * @param type a {@link java.lang.String} object.
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * <p>Getter for the field <code>describedBy</code>.</p>
     *
     * @return a {@link org.raml.model.SecuritySchemeDescriptor} object.
     */
    public SecuritySchemeDescriptor getDescribedBy()
    {
        return describedBy;
    }

    /**
     * <p>Setter for the field <code>describedBy</code>.</p>
     *
     * @param describedBy a {@link org.raml.model.SecuritySchemeDescriptor} object.
     */
    public void setDescribedBy(SecuritySchemeDescriptor describedBy)
    {
        this.describedBy = describedBy;
    }

    /**
     * <p>Getter for the field <code>settings</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, List<String>> getSettings()
    {
        return settings;
    }

    /**
     * <p>Setter for the field <code>settings</code>.</p>
     *
     * @param settings a {@link java.util.Map} object.
     */
    public void setSettings(Map<String, List<String>> settings)
    {
        this.settings = settings;
    }
}
