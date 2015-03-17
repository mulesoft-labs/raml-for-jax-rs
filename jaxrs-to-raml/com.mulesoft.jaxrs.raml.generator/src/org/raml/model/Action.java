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
import org.raml.emitter.TraitsDumper;
import org.raml.model.parameter.Header;
import org.raml.model.parameter.QueryParameter;
import org.raml.model.parameter.UriParameter;
import org.raml.parser.annotation.Key;
import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Parent;
import org.raml.parser.annotation.Scalar;
import org.raml.parser.annotation.Sequence;
import org.raml.parser.annotation.TransformHandler;
import org.raml.parser.builder.QuestionedActionTypeHandler;
import org.raml.parser.builder.TraitsExtraHandler;
import org.raml.parser.rule.SecurityReferenceSequenceRule;


/**
 * <p>Action class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class Action
{

    @Key
    @TransformHandler(QuestionedActionTypeHandler.class)
    private ActionType type;
    
    @Sequence(extraHandler=TraitsExtraHandler.class)
    @Dumper(TraitsDumper.class)
    private List<String> is = new ArrayList<String>();
    
    protected boolean isQuestioned;


	@Scalar
    private String description;

    @Mapping
    private Map<String, Header> headers = new LinkedHashMap<String, Header>();

    @Mapping
    private Map<String, QueryParameter> queryParameters = new LinkedHashMap<String, QueryParameter>();

    @Mapping
    private Map<String, MimeType> body = new LinkedHashMap<String, MimeType>();

    @Mapping
    private Map<String, Response> responses = new LinkedHashMap<String, Response>();

    @Parent
    private Resource resource;

    

	private List<TemplateUse> isModel=new ArrayList<TemplateUse>();
    
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

    @Sequence
    private List<Protocol> protocols = new ArrayList<Protocol>();

    @Sequence(rule = SecurityReferenceSequenceRule.class)
    private List<SecurityReference> securedBy = new ArrayList<SecurityReference>();

    @Mapping(rule = org.raml.parser.rule.UriParametersRule.class)
    private Map<String, List<UriParameter>> baseUriParameters = new HashMap<String, List<UriParameter>>();

    /**
     * <p>Constructor for Action.</p>
     */
    public Action()
    {
    }

    /**
     * <p>Getter for the field <code>type</code>.</p>
     *
     * @return a {@link org.raml.model.ActionType} object.
     */
    public ActionType getType()
    {
        return type;
    }

    /**
     * <p>Setter for the field <code>type</code>.</p>
     *
     * @param type a {@link org.raml.model.ActionType} object.
     */
    public void setType(ActionType type)
    {
        this.type = type;
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
     * <p>Getter for the field <code>body</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, MimeType> getBody()
    {
        return body;
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

    /**
     * <p>Getter for the field <code>resource</code>.</p>
     *
     * @return a {@link org.raml.model.Resource} object.
     */
    public Resource getResource()
    {
        return resource;
    }

    /**
     * <p>Setter for the field <code>resource</code>.</p>
     *
     * @param resource a {@link org.raml.model.Resource} object.
     */
    public void setResource(Resource resource)
    {
        this.resource = resource;
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
     * <p>Getter for the field <code>protocols</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Protocol> getProtocols()
    {
        return protocols;
    }

    /**
     * <p>Setter for the field <code>protocols</code>.</p>
     *
     * @param protocols a {@link java.util.List} object.
     */
    public void setProtocols(List<Protocol> protocols)
    {
        this.protocols = protocols;
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

    
    /**
     * <p>toString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String toString()
    {
        return "Action{" +
               "type='" + type + '\'' +
               ", resource=" + (resource != null ? resource.getUri() : "-") + '}';
    }
    
    /**
     * <p>isQuestioned.</p>
     *
     * @return a boolean.
     */
    public boolean isQuestioned() {
		return isQuestioned;
	}

	/**
	 * <p>setQuestioned.</p>
	 *
	 * @param isQuestioned a boolean.
	 */
	public void setQuestioned(boolean isQuestioned) {
		this.isQuestioned = isQuestioned;
	}

}
