/*
 * Copyright 2013-2015 (c) MuleSoft, Inc.
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
package org.raml.jaxrs.codegen.core;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_XML;
import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.startsWith;
import static org.apache.commons.lang.StringUtils.substringAfter;
import static org.apache.commons.lang.StringUtils.substringAfterLast;
import static org.apache.commons.lang.StringUtils.substringBefore;
import static org.apache.commons.lang.WordUtils.capitalize;
import static org.raml.jaxrs.codegen.core.Names.buildJavaFriendlyName;
import static org.raml.jaxrs.codegen.core.Names.buildNestedSchemaName;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.lang.Validate;
import org.raml.model.Action;
import org.raml.model.MimeType;
import org.raml.model.Resource;
import org.raml.model.Response;
import org.raml.model.parameter.AbstractParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JType;

/**
 * <p>Types class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class Types
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Types.class);

    private final Context context;
    private final Map<String, JClass> schemaClasses;

    /**
     * <p>Constructor for Types.</p>
     *
     * @param context a {@link org.raml.jaxrs.codegen.core.Context} object.
     */
    public Types(final Context context)
    {
        Validate.notNull(context, "context can't be null");

        this.context = context;

        schemaClasses = new HashMap<String, JClass>();
    }

    /**
     * <p>buildParameterType.</p>
     *
     * @param parameter a {@link org.raml.model.parameter.AbstractParam} object.
     * @param name a {@link java.lang.String} object.
     * @return a {@link com.sun.codemodel.JType} object.
     * @throws java.lang.Exception if any.
     */
    public JType buildParameterType(final AbstractParam parameter, final String name) throws Exception
    {
        if ((parameter.getEnumeration() != null) && (!parameter.getEnumeration().isEmpty())&&Names.isValidEnumValues(parameter.getEnumeration()))
        {
            return context.createResourceEnum(context.getCurrentResourceInterface(), capitalize(name),
                parameter.getEnumeration());
        }

        final JType codegenType = context.getGeneratorType(getJavaType(parameter));

        if (parameter.isRepeat())
        {
            return ((JClass) context.getGeneratorType(List.class)).narrow(codegenType);
        }
        else
        {
            return codegenType;
        }
    }

    /**
     * <p>getRequestEntityClass.</p>
     *
     * @param mimeType a {@link org.raml.model.MimeType} object.
     * @return a {@link com.sun.codemodel.JType} object.
     * @throws java.io.IOException if any.
     */
    public JType getRequestEntityClass(final MimeType mimeType) throws IOException
    {
        final JClass schemaClass = getSchemaClass(mimeType);

        if (schemaClass != null)
        {
            return schemaClass;
        }
        else if (startsWith(mimeType.getType(), "text/"))
        {
            return getGeneratorType(String.class);
        }
        else if (MediaType.APPLICATION_OCTET_STREAM.equals(mimeType.getType()))
        {
        	            return getGeneratorType(InputStream.class);
        }
        else
        {
            // fallback to a generic reader
            return getGeneratorType(Reader.class);
        }
    }

    /**
     * <p>getResponseEntityClass.</p>
     *
     * @param mimeType a {@link org.raml.model.MimeType} object.
     * @return a {@link com.sun.codemodel.JType} object.
     * @throws java.io.IOException if any.
     */
    public JType getResponseEntityClass(final MimeType mimeType) throws IOException
    {
        final JClass schemaClass = getSchemaClass(mimeType);

        if (schemaClass != null)
        {
            return schemaClass;
        }
        else if (startsWith(mimeType.getType(), "text/"))
        {
            return getGeneratorType(String.class);
        }
        else
        {
            // fallback to a streaming output
            return getGeneratorType(StreamingOutput.class);
        }
    }

    /**
     * <p>getGeneratorType.</p>
     *
     * @param clazz a {@link java.lang.Class} object.
     * @return a {@link com.sun.codemodel.JType} object.
     */
    public JType getGeneratorType(final Class<?> clazz)
    {
        return context.getGeneratorType(clazz);
    }

    /**
     * <p>getGeneratorClass.</p>
     *
     * @param clazz a {@link java.lang.Class} object.
     * @return a {@link com.sun.codemodel.JClass} object.
     */
    public JClass getGeneratorClass(final Class<?> clazz)
    {
        return (JClass) context.getGeneratorType(clazz);
    }

    
    /**
     * <p>getGeneratorClass.</p>
     *
     * @param classFQN a {@link java.lang.String} object.
     * @return a {@link com.sun.codemodel.JClass} object.
     */
    public JClass getGeneratorClass(final String classFQN)
    {
         return context.getGeneratorClass(classFQN);
    }
    
    private JClass getSchemaClass(final MimeType mimeType) throws IOException
    {
        final String schemaNameOrContent = mimeType.getSchema();
        if (isBlank(schemaNameOrContent))
        {
            return null;
        }

        final String buildSchemaKey = buildSchemaKey(mimeType);

        final JClass existingClass = schemaClasses.get(buildSchemaKey);
        if (existingClass != null)
        {
            return existingClass;
        }

        if (isCompatibleWith(mimeType, APPLICATION_XML, TEXT_XML))
        {
            //at this point all classes generated from XSDs are contained in the schemaClasses map;
            return null;
        }
        else if (isCompatibleWith(mimeType, APPLICATION_JSON))
        {
            final Entry<File, String> schemaNameAndFile = context.getSchemaFile(schemaNameOrContent);
            if (isBlank(schemaNameAndFile.getValue()))
            {
                schemaNameAndFile.setValue(buildNestedSchemaName(mimeType,context.getConfiguration()));
            }

            final String className = buildJavaFriendlyName(schemaNameAndFile.getValue());
            final JClass generatedClass = context.generateClassFromJsonSchema(className,
                schemaNameAndFile.getKey().toURI().toURL());
            schemaClasses.put(buildSchemaKey, generatedClass);
            return generatedClass;
        }
        else
        {
            return null;
        }
    }

    private boolean isCompatibleWith(final MimeType mt, final String... mediaTypes)
    {
        final String mimeType = mt.getType();

        if (isBlank(mimeType))
        {
            return false;
        }

        for (final String mediaType : mediaTypes)
        {
            if (mediaType.toString().equals(mimeType))
            {
                return true;
            }

            final String primaryType = substringBefore(mimeType, "/");

            if (substringBefore(mediaType, "/").equals(primaryType))
            {
                final String subType = defaultIfBlank(substringAfterLast(mimeType, "+"),
                    substringAfter(mimeType, "/"));

                if (substringAfter(mediaType, "/").equals(subType))
                {
                    return true;
                }
            }
        }

        return false;
    }

    private String buildSchemaKey(final MimeType mimeType)
    {
        return Names.getShortMimeType(mimeType) + "@" + mimeType.getSchema().hashCode();
    }

    static Class<?> getJavaType(final AbstractParam parameter)
    {
        if (parameter.getType() == null)
        {
            return String.class;
        }

        final boolean usePrimitive = !parameter.isRepeat()
                                     && (parameter.isRequired() || isNotBlank(parameter.getDefaultValue()));

        switch (parameter.getType())
        {
            case BOOLEAN :
                return usePrimitive ? boolean.class : Boolean.class;
            case DATE :
                return Date.class;
            case FILE :
                return File.class;
            case INTEGER :
                return usePrimitive ? int.class : Integer.class;
            case NUMBER :
                return BigDecimal.class;
            case STRING :
                return String.class;
            default :
                LOGGER.warn("Unsupported RAML type: " + parameter.getType().toString());
                return Object.class;
        }
    }
    
    /**
     * <p>generateClassesFromXmlSchemas.</p>
     *
     * @param resources a {@link java.util.Collection} object.
     */
    public void generateClassesFromXmlSchemas(Collection<Resource> resources) {

        if (resources == null) {
            return;
        }
        HashMap<String, File> schemaFiles = new HashMap<String, File>();
        for (Resource r : resources) {
            collectXmlSchemaFiles(r, schemaFiles);
        }
        schemaClasses.putAll(context.generateClassesFromXmlSchemas(schemaFiles));
    }

    /**
     * <p>collectXmlSchemaFiles.</p>
     *
     * @param resource a {@link org.raml.model.Resource} object.
     * @param schemaFiles a {@link java.util.Map} object.
     */
    public void collectXmlSchemaFiles(Resource resource,
            Map<String, File> schemaFiles) {

        Collection<Action> actions = resource.getActions().values();
        for (Action a : actions) {
            Map<String, Response> responses = a.getResponses();
            if (responses != null) {
                for (Response resp : responses.values()) {
                    Map<String, MimeType> body = resp.getBody();
                    if (body != null) {
                        for (MimeType mt : body.values()) {
                            try {
                                collectXmlSchemaFiles(mt, schemaFiles);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            Map<String, MimeType> body = a.getBody();
            if (body != null) {
                for (MimeType mt : body.values()) {
                    try {
                        collectXmlSchemaFiles(mt, schemaFiles);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        Collection<Resource> resources = resource.getResources().values();
        for (Resource r : resources) {
            collectXmlSchemaFiles(r, schemaFiles);
        }
    }

    private void collectXmlSchemaFiles(MimeType mimeType, Map<String, File> schemaFiles)
            throws IOException {

        if (!isCompatibleWith(mimeType, APPLICATION_XML, TEXT_XML)) {
            return;
        }

        final String schemaNameOrContent = mimeType.getSchema();
        if (isBlank(schemaNameOrContent)) {
            return;
        }
        final String buildSchemaKey = buildSchemaKey(mimeType);
        final Entry<File, String> schemaNameAndFile = context.getSchemaFile(schemaNameOrContent);
        schemaFiles.put(buildSchemaKey, schemaNameAndFile.getKey());
    }
}
