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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.raml.emitter.Dumper;
import org.raml.emitter.MapFilter;
import org.raml.emitter.TraitsDumper;
import org.raml.emitter.TypeDumper;
import org.raml.emitter.UrlParameterFilter;
import org.raml.model.parameter.UriParameter;
import org.raml.parser.annotation.Key;
import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Parent;
import org.raml.parser.annotation.Scalar;
import org.raml.parser.annotation.Sequence;
import org.raml.parser.builder.TraitsExtraHandler;
import org.raml.parser.resolver.ResourceHandler;
import org.raml.parser.rule.SecurityReferenceSequenceRule;

/**
 * <p>Resource class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class Resource
{

	
	
    @Scalar
    private String displayName;
    
    @Sequence(rule = SecurityReferenceSequenceRule.class)
    private List<SecurityReference> securedBy = new ArrayList<SecurityReference>();

    @Scalar
    @Dumper(TypeDumper.class)
    private String type;

    @Sequence(extraHandler=TraitsExtraHandler.class)
    @Dumper(TraitsDumper.class)
    private List<String> is = new ArrayList<String>();
    
    @Scalar
    private String description;

    @Parent(property = "uri")
    private String parentUri;

    @Key
    private String relativeUri;

    @Mapping
    @MapFilter(UrlParameterFilter.class)
    private Map<String, UriParameter> uriParameters = new LinkedHashMap<String, UriParameter>();


    
    private List<TemplateUse>isModel=new ArrayList<TemplateUse>();
    
    private List<TemplateUse>typeModel=new ArrayList<TemplateUse>();

    /**
     * <p>Getter for the field <code>isModel</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<TemplateUse> getIsModel() {
		return isModel;
	}

	/**
	 * <p>Setter for the field <code>isModel</code>.</p>
	 *
	 * @param isModel a {@link java.util.List} object.
	 */
	public void setIsModel(List<TemplateUse> isModel) {
		this.isModel = isModel;
	}

	

    @Mapping(rule = org.raml.parser.rule.UriParametersRule.class)
    private Map<String, List<UriParameter>> baseUriParameters = new LinkedHashMap<String, List<UriParameter>>();

    @Mapping(implicit = true)
    private Map<ActionType, Action> actions = new LinkedHashMap<ActionType, Action>();

    @Mapping(handler = ResourceHandler.class, implicit = true)
    private Map<String, Resource> resources = new LinkedHashMap<String, Resource>();

    /**
     * <p>Constructor for Resource.</p>
     */
    public Resource()
    {
    }

    /**
     * <p>Setter for the field <code>relativeUri</code>.</p>
     *
     * @param relativeUri a {@link java.lang.String} object.
     */
    public void setRelativeUri(String relativeUri)
    {    	
    	this.relativeUri = relativeUri;
    }

    /**
     * <p>Getter for the field <code>parentUri</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getParentUri()
    {
        return parentUri;
    }

    /**
     * <p>Setter for the field <code>parentUri</code>.</p>
     *
     * @param parentUri a {@link java.lang.String} object.
     */
    public void setParentUri(String parentUri)
    {
        this.parentUri = parentUri;
    }

    /**
     * <p>Setter for the field <code>uriParameters</code>.</p>
     *
     * @param uriParameters a {@link java.util.Map} object.
     */
    public void setUriParameters(Map<String, UriParameter> uriParameters)
    {
        this.uriParameters = uriParameters;
    }

    /**
     * <p>Getter for the field <code>actions</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<ActionType, Action> getActions()
    {
        return actions;
    }

    /**
     * <p>Setter for the field <code>actions</code>.</p>
     *
     * @param actions a {@link java.util.Map} object.
     */
    public void setActions(Map<ActionType, Action> actions)
    {
        this.actions = actions;
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
     * <p>Setter for the field <code>description</code>.</p>
     *
     * @param description a {@link java.lang.String} object.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * <p>Getter for the field <code>relativeUri</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getRelativeUri()
    {
        return relativeUri;
    }

    /**
     * <p>getUri.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getUri()
    {
        return parentUri + relativeUri;
    }

    /**
     * <p>getAction.</p>
     *
     * @param name a {@link org.raml.model.ActionType} object.
     * @return a {@link org.raml.model.Action} object.
     */
    public Action getAction(ActionType name)
    {
        return actions.get(name);
    }

    /**
     * <p>getAction.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link org.raml.model.Action} object.
     */
    public Action getAction(String name)
    {
        return actions.get(ActionType.valueOf(name.toUpperCase()));
    }

    /**
     * <p>Getter for the field <code>resources</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, Resource> getResources()
    {
        return resources;
    }

    /**
     * <p>Setter for the field <code>resources</code>.</p>
     *
     * @param resources a {@link java.util.Map} object.
     */
    public void setResources(Map<String, Resource> resources)
    {
        this.resources = resources;
    }

    /**
     * <p>Getter for the field <code>uriParameters</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, UriParameter> getUriParameters()
    {
        return uriParameters;
    }

    /**
     * <p>Getter for the field <code>is</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<String> getIs()
    {
        return is;
    }

    /**
     * <p>Setter for the field <code>is</code>.</p>
     *
     * @param is a {@link java.util.List} object.
     */
    public void setIs(List<String> is)
    {
        this.is = is;
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
     * <p>getTypeModelT.</p>
     *
     * @return a {@link org.raml.model.TemplateUse} object.
     */
    public TemplateUse getTypeModelT(){
    	if (typeModel.isEmpty()){
    		return null;
    	}
        return typeModel.iterator().next();
    }
    
    /**
     * <p>Getter for the field <code>typeModel</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<TemplateUse> getTypeModel() {
		return typeModel;
	}

	/**
	 * <p>Setter for the field <code>typeModel</code>.</p>
	 *
	 * @param typeModel a {@link java.util.List} object.
	 */
	public void setTypeModel(List<TemplateUse> typeModel) {
		this.typeModel = typeModel;
	}

	/**
	 * <p>Setter for the field <code>type</code>.</p>
	 *
	 * @param type a {@link java.lang.String} object.
	 */
	public void setType(String type){
    	this.type=type;
    }

    /**
     * <p>setTypeModelT.</p>
     *
     * @param type a {@link org.raml.model.TemplateUse} object.
     */
    public void setTypeModelT(TemplateUse type)
    {
    	typeModel.clear();
    	if (type!=null){
    		typeModel.add(type);
    	}
    }
    

    /**
     * <p>Getter for the field <code>securedBy</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<SecurityReference> getSecuredBy()
    {
        return securedBy;
    }

    /**
     * <p>Setter for the field <code>securedBy</code>.</p>
     *
     * @param securedBy a {@link java.util.List} object.
     */
    public void setSecuredBy(List<SecurityReference> securedBy)
    {
        this.securedBy = securedBy;
    }

    /**
     * <p>Getter for the field <code>baseUriParameters</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, List<UriParameter>> getBaseUriParameters()
    {
        return baseUriParameters;
    }

    /**
     * <p>Setter for the field <code>baseUriParameters</code>.</p>
     *
     * @param baseUriParameters a {@link java.util.Map} object.
     */
    public void setBaseUriParameters(Map<String, List<UriParameter>> baseUriParameters)
    {
        this.baseUriParameters = baseUriParameters;
    }

    
    /** {@inheritDoc} */
    public boolean equals(Object o)
    {
       return super.equals(o);

    }

    
    /**
     * <p>hashCode.</p>
     *
     * @return a int.
     */
    public int hashCode()
    {
        return super.hashCode();
    }

    
    /**
     * <p>toString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String toString()
    {
        return "Resource{displayName='" + displayName + '\'' +
               ", uri='" + (parentUri != null ? getUri() : "-" + '\'') + '}';
    }

    /**
     * <p>getResource.</p>
     *
     * @param path a {@link java.lang.String} object.
     * @return a {@link org.raml.model.Resource} object.
     */
    public Resource getResource(String path)
    {
        for (Resource resource : resources.values())
        {
            if (path.startsWith(resource.getRelativeUri()))
            {
                if (path.length() == resource.getRelativeUri().length())
                {
                    return resource;
                }
                if (path.charAt(resource.getRelativeUri().length()) == '/')
                {
                    return resource.getResource(path.substring(resource.getRelativeUri().length()));
                }
            }
        }
        return null;
    }
	
}
