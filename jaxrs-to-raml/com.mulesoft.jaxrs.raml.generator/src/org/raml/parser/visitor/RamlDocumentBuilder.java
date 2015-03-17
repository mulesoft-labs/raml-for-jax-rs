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
package org.raml.parser.visitor;

import static org.raml.parser.rule.BaseUriRule.URI_PATTERN;

import java.lang.reflect.Field;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.model.parameter.UriParameter;
import org.raml.parser.builder.NodeBuilder;
import org.raml.parser.loader.DefaultResourceLoader;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.tagresolver.IncludeResolver;
import org.raml.parser.tagresolver.JacksonTagResolver;
import org.raml.parser.tagresolver.JaxbTagResolver;
import org.raml.parser.tagresolver.TagResolver;
import org.yaml.snakeyaml.nodes.MappingNode;

/**
 * <p>RamlDocumentBuilder class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class RamlDocumentBuilder extends YamlDocumentBuilder<Raml>
{

    protected TemplateResolver templateResolver;
    private MediaTypeResolver mediaTypeResolver;

    /**
     * <p>Constructor for RamlDocumentBuilder.</p>
     */
    public RamlDocumentBuilder()
    {
        this(new DefaultResourceLoader());
    }

    /**
     * <p>Constructor for RamlDocumentBuilder.</p>
     *
     * @param resourceLoader a {@link org.raml.parser.loader.ResourceLoader} object.
     * @param tagResolvers a {@link org.raml.parser.tagresolver.TagResolver} object.
     */
    public RamlDocumentBuilder(ResourceLoader resourceLoader, TagResolver... tagResolvers)
    {
        super(Raml.class, resourceLoader, defaultResolver(tagResolvers));
    }

    /**
     * <p>Constructor for RamlDocumentBuilder.</p>
     *
     * @param class1 a {@link java.lang.Class} object.
     * @param resourceLoader a {@link org.raml.parser.loader.ResourceLoader} object.
     * @param tagResolvers an array of {@link org.raml.parser.tagresolver.TagResolver} objects.
     */
    public RamlDocumentBuilder(Class<? extends Raml> class1,
			ResourceLoader resourceLoader, TagResolver[] tagResolvers) {
    	super((Class)class1,resourceLoader,tagResolvers);
	}

	private static TagResolver[] defaultResolver(TagResolver[] tagResolvers)
    {
        TagResolver[] defaultResolvers = new TagResolver[] {
                new IncludeResolver(),
                new JacksonTagResolver(),
                new JaxbTagResolver()
        };
        return (TagResolver[]) ArrayUtils.addAll(defaultResolvers, tagResolvers);
    }

    
    /** {@inheritDoc} */
    public void onMappingNodeStart(MappingNode mappingNode, TupleType tupleType)
    {
        super.onMappingNodeStart(mappingNode, tupleType);
        if (getDocumentContext().peek() instanceof Resource)
        {
            Resource resource = (Resource) getDocumentContext().peek();
            getTemplateResolver().resolve(mappingNode, resource.getRelativeUri(), resource.getUri());
        }
        else if (isBodyBuilder(getBuilderContext().peek()))
        {
            getMediaTypeResolver().resolve(mappingNode);
        }
    }

    
    /** {@inheritDoc} */
    public void onMappingNodeEnd(MappingNode mappingNode, TupleType tupleType)
    {
        if (getDocumentContext().peek() instanceof Resource)
        {
            Resource resource = (Resource) getDocumentContext().peek();
            populateDefaultUriParameters(resource);
        }
        super.onMappingNodeEnd(mappingNode, tupleType);
    }

    private String toString(Stack<NodeBuilder<?>> builderContext)
    {
        StringBuilder builder = new StringBuilder(">>> BuilderContext >>> ");
        for (NodeBuilder nb : builderContext)
        {
            builder.append(nb).append(" ->- ");
        }
        return builder.toString();
    }

    private boolean isBodyBuilder(NodeBuilder builder)
    {
        try
        {
            Field valueType = builder.getClass().getDeclaredField("valueClass");
            valueType.setAccessible(true);
            return valueType.get(builder) != null && ((Class) valueType.get(builder)).getName().equals("org.raml.model.MimeType");
        }
        catch (NoSuchFieldException e)
        {
            return false;
        }
        catch (IllegalAccessException e)
        {
            return false;
        }
    }

    /**
     * <p>Getter for the field <code>templateResolver</code>.</p>
     *
     * @return a {@link org.raml.parser.visitor.TemplateResolver} object.
     */
    public TemplateResolver getTemplateResolver()
    {
        if (templateResolver == null)
        {
            templateResolver = new TemplateResolver(getResourceLoader(), this,true);
        }
        return templateResolver;
    }

    /**
     * <p>Getter for the field <code>mediaTypeResolver</code>.</p>
     *
     * @return a {@link org.raml.parser.visitor.MediaTypeResolver} object.
     */
    public MediaTypeResolver getMediaTypeResolver()
    {
        if (mediaTypeResolver == null)
        {
            mediaTypeResolver = new MediaTypeResolver();
        }
        return mediaTypeResolver;
    }

    
    /**
     * <p>preBuildProcess.</p>
     */
    protected void preBuildProcess()
    {
        getTemplateResolver().init(getRootNode());
        getMediaTypeResolver().beforeDocumentStart(getRootNode());
    }

    
    /**
     * <p>postBuildProcess.</p>
     */
    protected void postBuildProcess()
    {
    }

    private void populateDefaultUriParameters(Resource resource)
    {
        Pattern pattern = Pattern.compile(URI_PATTERN);
        Matcher matcher = pattern.matcher(resource.getRelativeUri());
        while (matcher.find())
        {
            String paramName = matcher.group(1);
            if (!resource.getUriParameters().containsKey(paramName))
            {
                resource.getUriParameters().put(paramName, new UriParameter(paramName));
            }
        }
    }

}
