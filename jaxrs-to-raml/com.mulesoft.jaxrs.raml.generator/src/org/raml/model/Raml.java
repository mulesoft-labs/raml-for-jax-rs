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

import org.raml.emitter.ResourceTypeEmitter;
import org.raml.emitter.SchemasEmitter;
import org.raml.emitter.SecuritySchemeEmitter;
import org.raml.emitter.TraitEmitter;
import org.raml.model.parameter.UriParameter;
import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Scalar;
import org.raml.parser.annotation.Sequence;
import org.raml.parser.resolver.ResourceHandler;
import org.raml.parser.rule.GlobalSchemasHandler;
import org.raml.parser.rule.SecurityReferenceSequenceRule;


/**
 * <p>Raml class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class Raml
{

    @Scalar(required = true)
    private String title;

    @Scalar()
    private String version;

    @Scalar(rule = org.raml.parser.rule.BaseUriRule.class)
    private String baseUri;

    @Sequence
    private List<Protocol> protocols = new ArrayList<Protocol>();

    @Mapping(rule = org.raml.parser.rule.UriParametersRule.class)
    private Map<String, UriParameter> baseUriParameters = new HashMap<String, UriParameter>();

    @Scalar()
    private String mediaType;
    
    @Sequence
    @org.raml.emitter.Dumper(SecuritySchemeEmitter.class)
    private List<Map<String, SecurityScheme>> securitySchemes = new ArrayList<Map<String, SecurityScheme>>();

    @Sequence(rule = org.raml.parser.rule.GlobalSchemasRule.class,extraHandler=GlobalSchemasHandler.class)
    @org.raml.emitter.Dumper(SchemasEmitter.class)
	protected List<Map<String, String>> schemas = new ArrayList<Map<String, String>>();

    @Sequence
    @org.raml.emitter.Dumper(ResourceTypeEmitter.class)
    private List<Map<String, Template>> resourceTypes;

    @Sequence
    @org.raml.emitter.Dumper(TraitEmitter.class)
    private List<Map<String, Template>> traits;

   

    @Sequence(rule = SecurityReferenceSequenceRule.class)
    private List<SecurityReference> securedBy = new ArrayList<SecurityReference>();

    @Mapping(handler = ResourceHandler.class, implicit = true)
    private Map<String, Resource> resources = new LinkedHashMap<String, Resource>();

    @Sequence
    private List<DocumentationItem> documentation;

    /**
     * <p>Constructor for Raml.</p>
     */
    public Raml()
    {
    }

    /**
     * <p>Setter for the field <code>documentation</code>.</p>
     *
     * @param documentation a {@link java.util.List} object.
     */
    public void setDocumentation(List<DocumentationItem> documentation)
    {
        this.documentation = documentation;
    }

    /**
     * <p>Getter for the field <code>documentation</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<DocumentationItem> getDocumentation()
    {
        return documentation;
    }

    /**
     * <p>Setter for the field <code>baseUriParameters</code>.</p>
     *
     * @param uriParameters a {@link java.util.Map} object.
     */
    public void setBaseUriParameters(Map<String, UriParameter> uriParameters)
    {
        this.baseUriParameters = uriParameters;
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
     * <p>Getter for the field <code>title</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * <p>Setter for the field <code>title</code>.</p>
     *
     * @param title a {@link java.lang.String} object.
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * <p>Getter for the field <code>version</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * <p>Setter for the field <code>version</code>.</p>
     *
     * @param version a {@link java.lang.String} object.
     */
    public void setVersion(String version)
    {
        this.version = version;
    }

    /**
     * <p>Getter for the field <code>baseUri</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getBaseUri()
    {
        return baseUri;
    }

    /**
     * <p>Setter for the field <code>baseUri</code>.</p>
     *
     * @param baseUri a {@link java.lang.String} object.
     */
    public void setBaseUri(String baseUri)
    {
        this.baseUri = baseUri;
    }

    /**
     * <p>getBasePath.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getBasePath()
    {
        //skip protocol separator "//"
        int start = baseUri.indexOf("//") + 2;
        if (start == -1)
        {
            start = 0;
        }

        start = baseUri.indexOf("/", start);
        return baseUri.substring(start);
    }

    /**
     * <p>getUri.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getUri()
    {
        return "";
    }

    /**
     * <p>Getter for the field <code>mediaType</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getMediaType()
    {
        return mediaType;
    }

    /**
     * <p>Setter for the field <code>mediaType</code>.</p>
     *
     * @param mediaType a {@link java.lang.String} object.
     */
    public void setMediaType(String mediaType)
    {
        this.mediaType = mediaType;
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
     * <p>Getter for the field <code>baseUriParameters</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, UriParameter> getBaseUriParameters()
    {
        return baseUriParameters;
    }

    /**
     * <p>Getter for the field <code>resourceTypes</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Map<String, Template>> getResourceTypes()
    {
        return resourceTypes;
    }

    /**
     * <p>Setter for the field <code>resourceTypes</code>.</p>
     *
     * @param resourceTypes a {@link java.util.List} object.
     */
    public void setResourceTypes(List<Map<String, Template>> resourceTypes)
    {
        this.resourceTypes = resourceTypes;
    }

    /**
     * <p>Getter for the field <code>traits</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Map<String, Template>> getTraits()
    {
        return traits;
    }

    /**
     * <p>Setter for the field <code>traits</code>.</p>
     *
     * @param traits a {@link java.util.List} object.
     */
    public void setTraits(List<Map<String, Template>> traits)
    {
        this.traits = traits;
    }

    /**
     * <p>Getter for the field <code>schemas</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Map<String, String>> getSchemas()
    {
        return schemas;
    }
    

    /**
     * <p>Setter for the field <code>schemas</code>.</p>
     *
     * @param schemas a {@link java.util.List} object.
     */
    public void setSchemas(List<Map<String, String>> schemas)
    {
        this.schemas = schemas;
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
     * <p>Getter for the field <code>securitySchemes</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Map<String, SecurityScheme>> getSecuritySchemes()
    {
        return securitySchemes;
    }

    /**
     * <p>Setter for the field <code>securitySchemes</code>.</p>
     *
     * @param securitySchemes a {@link java.util.List} object.
     */
    public void setSecuritySchemes(List<Map<String, SecurityScheme>> securitySchemes)
    {
        this.securitySchemes = securitySchemes;
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
     * <p>getConsolidatedSchemas.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, String> getConsolidatedSchemas()
    {
        Map<String, String> consolidated = new HashMap<String, String>();
        for (Map<String, String> map : getSchemas())
        {
            consolidated.putAll(map);
        }
        return consolidated;
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
                    final Resource found = resource.getResource(path.substring(resource.getRelativeUri().length()));
                    if (found != null) {
                     return found;
                    }
                }                
            }
        }
        return null;
    }
}
