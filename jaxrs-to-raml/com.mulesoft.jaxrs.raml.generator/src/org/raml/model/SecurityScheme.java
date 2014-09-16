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

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public SecuritySchemeDescriptor getDescribedBy()
    {
        return describedBy;
    }

    public void setDescribedBy(SecuritySchemeDescriptor describedBy)
    {
        this.describedBy = describedBy;
    }

    public Map<String, List<String>> getSettings()
    {
        return settings;
    }

    public void setSettings(Map<String, List<String>> settings)
    {
        this.settings = settings;
    }
}
