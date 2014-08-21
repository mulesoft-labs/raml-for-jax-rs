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
package org.raml.model.parameter;

import static org.raml.model.ParamType.STRING;

import java.math.BigDecimal;
import java.util.List;

import org.raml.model.ParamType;
import org.raml.parser.annotation.Scalar;
import org.raml.parser.annotation.Sequence;
import org.raml.parser.rule.SequenceTupleNullsAllowedRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractParam implements Cloneable
{

    @Scalar
    private String displayName;

    @Scalar
    private String description;

    @Scalar
    private ParamType type;

    @Scalar
    private boolean required;

    @Scalar
    private boolean repeat;

    @Sequence(alias = "enum", rule = SequenceTupleNullsAllowedRule.class)
    private List<String> enumeration;
    @Scalar
    private String pattern;
    @Scalar
    private Integer minLength;
    @Scalar
    private Integer maxLength;
    @Scalar
    private BigDecimal minimum;
    @Scalar
    private BigDecimal maximum;

    @Scalar(alias = "default")
    private String defaultValue;

    @Scalar
    private String example;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public AbstractParam()
    {
        this.type = STRING;
    }

    public AbstractParam(String displayName, ParamType type, boolean required)
    {
        this.displayName = displayName;
        this.type = type;
        this.required = required;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setType(ParamType type)
    {
        this.type = type;
    }

    public void setRequired(boolean required)
    {
        this.required = required;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public ParamType getType()
    {
        return type;
    }

    public boolean isRequired()
    {
        return required;
    }

    public boolean isRepeat()
    {
        return repeat;
    }

    public void setRepeat(boolean repeat)
    {
        this.repeat = repeat;
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

    public String getExample()
    {
        return example;
    }

    public List<String> getEnumeration()
    {
        return enumeration;
    }

    public void setEnumeration(List<String> enumeration)
    {
        this.enumeration = enumeration;
    }

    public String getPattern()
    {
        return pattern;
    }

    public void setPattern(String pattern)
    {
        this.pattern = pattern;
    }

    public Integer getMinLength()
    {
        return minLength;
    }

    public void setMinLength(Integer minLength)
    {
        this.minLength = minLength;
    }

    public Integer getMaxLength()
    {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength)
    {
        this.maxLength = maxLength;
    }

    public BigDecimal getMinimum()
    {
        return minimum;
    }

    public void setMinimum(BigDecimal minimum)
    {
        this.minimum = minimum;
    }

    public BigDecimal getMaximum()
    {
        return maximum;
    }

    public void setMaximum(BigDecimal maximum)
    {
        this.maximum = maximum;
    }

    public void setDefaultValue(String defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    public void setExample(String example)
    {
        this.example = example;
    }

    public boolean validate(String value)
    {
        if (type == null)
        {
            type = STRING;
        }
        return type.validate(this, value);
    }
}
