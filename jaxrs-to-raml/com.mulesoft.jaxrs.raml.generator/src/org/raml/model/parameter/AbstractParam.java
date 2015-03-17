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

/**
 * <p>AbstractParam class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
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

    //protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * <p>Constructor for AbstractParam.</p>
     */
    public AbstractParam()
    {
        this.type = STRING;
    }

    /**
     * <p>Constructor for AbstractParam.</p>
     *
     * @param displayName a {@link java.lang.String} object.
     * @param type a {@link org.raml.model.ParamType} object.
     * @param required a boolean.
     */
    public AbstractParam(String displayName, ParamType type, boolean required)
    {
        this.displayName = displayName;
        this.type = type;
        this.required = required;
    }

    /**
     * <p>Setter for the field <code>displayName</code>.</p>
     *
     * @param displayName a {@link java.lang.String} object.
     */
    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
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
     * <p>Setter for the field <code>type</code>.</p>
     *
     * @param type a {@link org.raml.model.ParamType} object.
     */
    public void setType(ParamType type)
    {
        this.type = type;
    }

    /**
     * <p>Setter for the field <code>required</code>.</p>
     *
     * @param required a boolean.
     */
    public void setRequired(boolean required)
    {
        this.required = required;
    }

    /**
     * <p>Getter for the field <code>displayName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDisplayName()
    {
        return displayName;
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
     * <p>Getter for the field <code>type</code>.</p>
     *
     * @return a {@link org.raml.model.ParamType} object.
     */
    public ParamType getType()
    {
        return type;
    }

    /**
     * <p>isRequired.</p>
     *
     * @return a boolean.
     */
    public boolean isRequired()
    {
        return required;
    }

    /**
     * <p>isRepeat.</p>
     *
     * @return a boolean.
     */
    public boolean isRepeat()
    {
        return repeat;
    }

    /**
     * <p>Setter for the field <code>repeat</code>.</p>
     *
     * @param repeat a boolean.
     */
    public void setRepeat(boolean repeat)
    {
        this.repeat = repeat;
    }

    /**
     * <p>Getter for the field <code>defaultValue</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDefaultValue()
    {
        return defaultValue;
    }

    /**
     * <p>Getter for the field <code>example</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getExample()
    {
        return example;
    }

    /**
     * <p>Getter for the field <code>enumeration</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<String> getEnumeration()
    {
        return enumeration;
    }

    /**
     * <p>Setter for the field <code>enumeration</code>.</p>
     *
     * @param enumeration a {@link java.util.List} object.
     */
    public void setEnumeration(List<String> enumeration)
    {
        this.enumeration = enumeration;
    }

    /**
     * <p>Getter for the field <code>pattern</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPattern()
    {
        return pattern;
    }

    /**
     * <p>Setter for the field <code>pattern</code>.</p>
     *
     * @param pattern a {@link java.lang.String} object.
     */
    public void setPattern(String pattern)
    {
        this.pattern = pattern;
    }

    /**
     * <p>Getter for the field <code>minLength</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getMinLength()
    {
        return minLength;
    }

    /**
     * <p>Setter for the field <code>minLength</code>.</p>
     *
     * @param minLength a {@link java.lang.Integer} object.
     */
    public void setMinLength(Integer minLength)
    {
        this.minLength = minLength;
    }

    /**
     * <p>Getter for the field <code>maxLength</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getMaxLength()
    {
        return maxLength;
    }

    /**
     * <p>Setter for the field <code>maxLength</code>.</p>
     *
     * @param maxLength a {@link java.lang.Integer} object.
     */
    public void setMaxLength(Integer maxLength)
    {
        this.maxLength = maxLength;
    }

    /**
     * <p>Getter for the field <code>minimum</code>.</p>
     *
     * @return a {@link java.math.BigDecimal} object.
     */
    public BigDecimal getMinimum()
    {
        return minimum;
    }

    /**
     * <p>Setter for the field <code>minimum</code>.</p>
     *
     * @param minimum a {@link java.math.BigDecimal} object.
     */
    public void setMinimum(BigDecimal minimum)
    {
        this.minimum = minimum;
    }

    /**
     * <p>Getter for the field <code>maximum</code>.</p>
     *
     * @return a {@link java.math.BigDecimal} object.
     */
    public BigDecimal getMaximum()
    {
        return maximum;
    }

    /**
     * <p>Setter for the field <code>maximum</code>.</p>
     *
     * @param maximum a {@link java.math.BigDecimal} object.
     */
    public void setMaximum(BigDecimal maximum)
    {
        this.maximum = maximum;
    }

    /**
     * <p>Setter for the field <code>defaultValue</code>.</p>
     *
     * @param defaultValue a {@link java.lang.String} object.
     */
    public void setDefaultValue(String defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    /**
     * <p>Setter for the field <code>example</code>.</p>
     *
     * @param example a {@link java.lang.String} object.
     */
    public void setExample(String example)
    {
        this.example = example;
    }

    /**
     * <p>validate.</p>
     *
     * @param value a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean validate(String value)
    {
        if (type == null)
        {
            type = STRING;
        }
        return type.validate(this, value);
    }
}
