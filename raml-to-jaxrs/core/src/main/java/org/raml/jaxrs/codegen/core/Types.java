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

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.startsWith;
import static org.apache.commons.lang.WordUtils.capitalize;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import org.aml.apimodel.INamedParam;
import org.aml.apimodel.MimeType;
import org.apache.commons.lang.Validate;

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
   
    private final Context context;
    
    /**
     * <p>Constructor for Types.</p>
     *
     * @param context a {@link org.raml.jaxrs.codegen.core.Context} object.
     */
    public Types(final Context context)
    {
        Validate.notNull(context, "context can't be null");

        this.context = context;

    }

    /**
     * <p>buildParameterType.</p>
     *
     * @param parameter a {@link org.aml.typesystem.ramlreader.NamedParam} object.
     * @param name a {@link java.lang.String} object.
     * @return a {@link com.sun.codemodel.JType} object.
     * @throws java.lang.Exception if any.
     */
    public JType buildParameterType(final INamedParam parameter, final String name) throws Exception
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
     * @param mimeType a {@link org.aml.apimodel.MimeType} object.
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
     * @param mimeType a {@link org.aml.apimodel.MimeType} object.
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
    	return (JClass) context.getType(mimeType.getTypeModel());                
    }


    static Class<?> getJavaType(final INamedParam parameter)
    {
        if (parameter.getTypeKind() == null)
        {
            return String.class;
        }

        final boolean usePrimitive = !parameter.isRepeat()
                                     && (parameter.isRequired() || isNotBlank(parameter.getDefaultValue()));

        switch (parameter.getTypeKind())
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
                return Object.class;
        }
    }
}