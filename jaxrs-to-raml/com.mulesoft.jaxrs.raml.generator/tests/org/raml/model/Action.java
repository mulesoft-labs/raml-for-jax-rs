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
    
    public List<TemplateUse> getIsModel() {
		return isModel;
	}

    
	public void setIsModel(List<TemplateUse> isModel) {
		this.isModel = isModel;
	}

    @Sequence
    private List<Protocol> protocols = new ArrayList<Protocol>();

    @Sequence(rule = SecurityReferenceSequenceRule.class)
    private List<SecurityReference> securedBy = new ArrayList<SecurityReference>();

    @Mapping(rule = org.raml.parser.rule.UriParametersRule.class)
    private Map<String, List<UriParameter>> baseUriParameters = new HashMap<String, List<UriParameter>>();

    public Action()
    {
    }

    public ActionType getType()
    {
        return type;
    }

    public void setType(ActionType type)
    {
        this.type = type;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Map<String, Header> getHeaders()
    {
        return headers;
    }

    public void setHeaders(Map<String, Header> headers)
    {
        this.headers = headers;
    }

    public Map<String, QueryParameter> getQueryParameters()
    {
        return queryParameters;
    }

    public void setQueryParameters(Map<String, QueryParameter> queryParameters)
    {
        this.queryParameters = queryParameters;
    }

    public Map<String, MimeType> getBody()
    {
        return body;
    }

    public void setBody(Map<String, MimeType> body)
    {
        this.body = body;
    }

    public Map<String, Response> getResponses()
    {
        return responses;
    }

    public void setResponses(Map<String, Response> responses)
    {
        this.responses = responses;
    }

    public Resource getResource()
    {
        return resource;
    }

    public void setResource(Resource resource)
    {
        this.resource = resource;
    }

    public List<String> getIs()
    {
        return is;
    }

    public void setIs(List<String> is)
    {
        this.is = is;
    }

    public List<Protocol> getProtocols()
    {
        return protocols;
    }

    public void setProtocols(List<Protocol> protocols)
    {
        this.protocols = protocols;
    }

    public List<SecurityReference> getSecuredBy()
    {
        return securedBy;
    }

    public void setSecuredBy(List<SecurityReference> securedBy)
    {
        this.securedBy = securedBy;
    }

    public Map<String, List<UriParameter>> getBaseUriParameters()
    {
        return baseUriParameters;
    }

    public void setBaseUriParameters(Map<String, List<UriParameter>> baseUriParameters)
    {
        this.baseUriParameters = baseUriParameters;
    }

    @Override
    public String toString()
    {
        return "Action{" +
               "type='" + type + '\'' +
               ", resource=" + (resource != null ? resource.getUri() : "-") + '}';
    }
    
    public boolean isQuestioned() {
		return isQuestioned;
	}

	public void setQuestioned(boolean isQuestioned) {
		this.isQuestioned = isQuestioned;
	}

}
