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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.raml.model.parameter.AbstractParam;
import org.raml.model.parameter.FormParameter;
import org.raml.parser.annotation.Key;
import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Scalar;

/**
 * <p>MimeType class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class MimeType
{

    @Key
    private String type;

    @Scalar(rule = org.raml.parser.rule.SchemaRule.class,includeField="schemaOrigin")
    private String schema;

    @Scalar(includeField="exampleOrigin")
    private String example;

    @Mapping
    private Map<String, List<FormParameter>> formParameters;

    private String exampleOrigin;
    
    private String schemaOrigin;
    
	/**
	 * <p>Getter for the field <code>schemaOrigin</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getSchemaOrigin() {
		return schemaOrigin;
	}

	/**
	 * <p>Setter for the field <code>schemaOrigin</code>.</p>
	 *
	 * @param schemaOrigin a {@link java.lang.String} object.
	 */
	public void setSchemaOrigin(String schemaOrigin) {
		this.schemaOrigin = schemaOrigin;
	}

	/**
	 * <p>Getter for the field <code>exampleOrigin</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getExampleOrigin() {
		return exampleOrigin;
	}

	/**
	 * <p>Setter for the field <code>exampleOrigin</code>.</p>
	 *
	 * @param exampleOrigin a {@link java.lang.String} object.
	 */
	public void setExampleOrigin(String exampleOrigin) {
		this.exampleOrigin = exampleOrigin;
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
     * <p>Getter for the field <code>schema</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSchema()
    {
        return schema;
    }

    /**
     * <p>Setter for the field <code>schema</code>.</p>
     *
     * @param schema a {@link java.lang.String} object.
     */
    public void setSchema(String schema)
    {
        this.schema = schema;
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
     * <p>Setter for the field <code>example</code>.</p>
     *
     * @param example a {@link java.lang.String} object.
     */
    public void setExample(String example)
    {
    	this.example = example;
    }

    /**
     * <p>Getter for the field <code>formParameters</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, List<FormParameter>> getFormParameters()
    {
        //TODO throw exception if invalid type?
        return formParameters;
    }

    /**
     * <p>Setter for the field <code>formParameters</code>.</p>
     *
     * @param formParameters a {@link java.util.Map} object.
     */
    public void setFormParameters(Map<String, List<FormParameter>> formParameters)
    {
        this.formParameters = formParameters;
    }

    
    /**
     * <p>toString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String toString()
    {
        return "MimeType{" +
               "type='" + type + '\'' +
               '}';
    }
    
    
    /**
     * <p>getAllChildren.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<? extends Object> getAllChildren(){
    	if(this.formParameters==null||this.formParameters.isEmpty())
    		return null;
    	
    	ArrayList<NamedFormParameter> result = new ArrayList<NamedFormParameter>(){
    
    		
    		public boolean remove(Object o) {
    			if (o instanceof NamedFormParameter){
    				NamedFormParameter qq=(NamedFormParameter) o;
    				
    			}
    			return super.remove(o);
    		}
    	};
    	for(Map.Entry<String, List<FormParameter>> entry : this.formParameters.entrySet()){
    		List<FormParameter> lst = entry.getValue();
    		String name = entry.getKey();
    		for(FormParameter fp : lst ){
    			result.add( new NamedFormParameter(name, fp));
    		}
    	}
    	return result;
    }
    
    /**
     * <p>setAllChildren.</p>
     *
     * @param z a {@link java.util.Collection} object.
     */
    public void setAllChildren(Collection<Object>z){
    	Collection<? extends Object> allChildren = getAllChildren();
    	boolean retainAll = allChildren.removeAll(z);
    	for (Object q:allChildren){
    		NamedFormParameter mm=(NamedFormParameter) q;
    		List<FormParameter> list = formParameters.get(mm.name);
    		if (list!=null){
    			list.remove(mm.original);
    		}    		
    	}
    }
    
    
    public static final class NamedFormParameter extends AbstractParam{
    	
    	public NamedFormParameter(String name, FormParameter original) {
			super();
			this.name = name;
			this.original = original;
		}

		String name;
    	
    	FormParameter original;
    	
    	public String toString(){
    		return name+": " + original.getType();
    	}
    	
    	public void setDisplayName(String displayName)
        {
            this.original.setDisplayName(displayName);
        }

        public void setDescription(String description)
        {
            this.original.setDescription(description);
        }

        public void setType(ParamType type)
        {
            this.original.setType(type);
        }

        public void setRequired(boolean required)
        {
            this.original.setRequired(required);
        }

        public String getDisplayName()
        {
            return original.getDisplayName();
        }

        public String getDescription()
        {
            return original.getDescription();
        }

        public ParamType getType()
        {
            return original.getType();
        }

        public boolean isRequired()
        {
            return original.isRequired();
        }

        public boolean isRepeat()
        {
            return original.isRepeat();
        }

        public void setRepeat(boolean repeat)
        {
            this.original.setRepeat(repeat);
        }

        public String getDefaultValue()
        {
            return original.getDefaultValue();
        }

        public String getExample()
        {
            return original.getExample();
        }

        public List<String> getEnumeration()
        {
            return original.getEnumeration();
        }

        public void setEnumeration(List<String> enumeration)
        {
            this.original.setEnumeration(enumeration);
        }

        public String getPattern()
        {
            return original.getPattern();
        }

        public void setPattern(String pattern)
        {
            this.original.setPattern(pattern);
        }

        public Integer getMinLength()
        {
            return original.getMinLength();
        }

        public void setMinLength(Integer minLength)
        {
            this.original.setMinLength(minLength);
        }

        public Integer getMaxLength()
        {
            return original.getMaxLength();
        }

        public void setMaxLength(Integer maxLength)
        {
            this.original.setMaxLength(maxLength);
        }

        public BigDecimal getMinimum()
        {
            return original.getMinimum();
        }

        public void setMinimum(BigDecimal minimum)
        {
            this.original.setMinimum(minimum);
        }

        public BigDecimal getMaximum()
        {
            return original.getMaximum();
        }

        public void setMaximum(BigDecimal maximum)
        {
            this.original.setMaximum(maximum);
        }

        public void setDefaultValue(String defaultValue)
        {
            this.original.setDefaultValue(defaultValue);
        }

        public void setExample(String example)
        {
            this.original.setExample(example);
        }

        public boolean validate(String value)
        {
            return original.getType().validate(this, value);
        }

		
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			NamedFormParameter other = (NamedFormParameter) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
    	
    	
    }
}
