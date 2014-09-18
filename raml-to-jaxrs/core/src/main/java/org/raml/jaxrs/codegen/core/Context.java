/*
 * Copyright 2013 (c) MuleSoft, Inc.
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

import static org.raml.jaxrs.codegen.core.Constants.JAXRS_HTTP_METHODS;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URL;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.HttpMethod;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.jsonschema2pojo.AnnotatorFactory;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.SchemaGenerator;
import org.jsonschema2pojo.SchemaMapper;
import org.jsonschema2pojo.SchemaStore;
import org.jsonschema2pojo.rules.RuleFactory;
import org.raml.model.Raml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;

class Context
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Context.class);

    private final Configuration configuration;
    private final Raml raml;
    private final JCodeModel codeModel;
    private final Map<String, Set<String>> resourcesMethods;
    private final Map<String, Object> httpMethodAnnotations;

    private final SchemaMapper schemaMapper;

    private boolean shouldGenerateResponseWrapper = false;
    private JDefinedClass currentResourceInterface;
    private final File globalSchemaStore;

    public Context(final Configuration configuration, final Raml raml) throws IOException
    {
        Validate.notNull(configuration, "configuration can't be null");
        Validate.notNull(raml, "raml can't be null");

        this.configuration = configuration;
        this.raml = raml;

        codeModel = new JCodeModel();

        resourcesMethods = new HashMap<String, Set<String>>();

        // prime the HTTP method annotation cache
        httpMethodAnnotations = new HashMap<String, Object>();
        for (final Class<? extends Annotation> clazz : JAXRS_HTTP_METHODS)
        {
            httpMethodAnnotations.put(clazz.getSimpleName(), clazz);
        }

        // write all global schemas to a temporary directory
        globalSchemaStore = Files.createTempDir();
        for (final Entry<String, String> nameAndSchema : raml.getConsolidatedSchemas().entrySet())
        {
            final File schemaFile = new File(globalSchemaStore, nameAndSchema.getKey());
            FileUtils.writeStringToFile(schemaFile, nameAndSchema.getValue());
        }

        // configure the JSON -> POJO generator
        final GenerationConfig jsonSchemaGenerationConfig = configuration.createJsonSchemaGenerationConfig();
        schemaMapper = new SchemaMapper(new RuleFactory(jsonSchemaGenerationConfig,
            new AnnotatorFactory().getAnnotator(configuration.getJsonMapper()),
            new SchemaStore()), new SchemaGenerator());
    }

    public Set<String> generate() throws IOException
    {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream ps = new PrintStream(baos);
        codeModel.build(configuration.getOutputDirectory(), ps);
        ps.close();

        final Set<String> generatedFiles = new HashSet<String>();
        if (shouldGenerateResponseWrapper)
        {
            generatedFiles.add(generateResponseWrapper());
        }
        generatedFiles.addAll(Arrays.asList(StringUtils.split(baos.toString())));

        try
        {
            FileUtils.deleteDirectory(globalSchemaStore);
        }
        catch (final Exception e)
        {
            LOGGER.warn("Failed to delete temporary directory: " + globalSchemaStore);
        }

        return generatedFiles;
    }

    /**
     * @return a {schema file, schema name} tuple.
     */
    public Entry<File, String> getSchemaFile(final String schemaNameOrContent) throws IOException
    {
        if (raml.getConsolidatedSchemas().containsKey(schemaNameOrContent))
        {
            // schemaNameOrContent is actually a global name
            return new SimpleEntry<File, String>(new File(globalSchemaStore, schemaNameOrContent),
                schemaNameOrContent);
        }
        else
        {
            // this is not a global reference but a local schema def - dump it to a temp file so
            // the type generators can pick it up
            final String schemaFileName = "schema" + schemaNameOrContent.hashCode();
            final File schemaFile = new File(globalSchemaStore, schemaFileName);
            FileUtils.writeStringToFile(schemaFile, schemaNameOrContent);
            return new SimpleEntry<File, String>(schemaFile, null);
        }
    }

    public Configuration getConfiguration()
    {
        return configuration;
    }

    public JDefinedClass getCurrentResourceInterface()
    {
        return currentResourceInterface;
    }

    public void setCurrentResourceInterface(final JDefinedClass currentResourceInterface)
    {
        this.currentResourceInterface = currentResourceInterface;
    }

    private String generateResponseWrapper() throws IOException
    {
        final String template = IOUtils.toString(getClass().getResourceAsStream(
            "/org/raml/templates/ResponseWrapper." + configuration.getJaxrsVersion().toString().toLowerCase()
                            + ".template"));

        final File supportPackageOutputDirectory = new File(configuration.getOutputDirectory(),
            getSupportPackage().replace('.', File.separatorChar));

        supportPackageOutputDirectory.mkdirs();

        final File sourceOutputFile = new File(supportPackageOutputDirectory, "ResponseWrapper.java");
        final String source = template.replace("${codegen.support.package}", getSupportPackage());
        final FileWriter fileWriter = new FileWriter(sourceOutputFile);
        IOUtils.write(source, fileWriter);
        IOUtils.closeQuietly(fileWriter);

        return getSupportPackage().replace('.', '/') + "/ResponseWrapper.java";
    }

    public JClass getResponseWrapperType()
    {
        shouldGenerateResponseWrapper = true;

        return codeModel.directClass(getSupportPackage() + ".ResponseWrapper");
    }

    public JDefinedClass createResourceInterface(final String name) throws Exception
    {
        String actualName;
        int i = -1;
        while (true)
        {
            actualName = name + (++i == 0 ? "" : Integer.toString(i));
            if (!resourcesMethods.containsKey(actualName))
            {
                resourcesMethods.put(actualName, new HashSet<String>());
                break;
            }
        }

        final JPackage pkg = codeModel._package(configuration.getBasePackageName() + ".resource");
        return pkg._interface(actualName);
    }

    public JMethod createResourceMethod(final JDefinedClass resourceInterface,
                                        final String methodName,
                                        final JType returnType)
    {
        final Set<String> existingMethodNames = resourcesMethods.get(resourceInterface.name());

        String actualMethodName;
        int i = -1;
        while (true)
        {
            actualMethodName = methodName + (++i == 0 ? "" : Integer.toString(i));
            if (!existingMethodNames.contains(actualMethodName))
            {
                existingMethodNames.add(actualMethodName);
                break;
            }
        }

        return resourceInterface.method(JMod.NONE, returnType, actualMethodName);
    }

    public JDefinedClass createResourceEnum(final JDefinedClass resourceInterface,
                                            final String name,
                                            final List<String> values) throws Exception
    {
    	JClass[] listClasses = resourceInterface.listClasses();
    	for (JClass c:listClasses){
    		if (c.name().equals(name)){
    			return (JDefinedClass) c;
    		}
    	}
        final JDefinedClass _enum = resourceInterface._enum(name);

        for (final String value : values)
        {
            _enum.enumConstant(value);
        }

        return _enum;
    }
    public JClass getGeneratorClass(final String clazzFQN)
    {
          return codeModel.ref(clazzFQN);
    }

    @SuppressWarnings("unchecked")
    public Context addHttpMethodAnnotation(final String httpMethod, final JAnnotatable annotatable)
        throws Exception
    {
        final Object annotationClass = httpMethodAnnotations.get(httpMethod.toUpperCase());
        if (annotationClass == null)
        {
            final JDefinedClass annotationClazz = createCustomHttpMethodAnnotation(httpMethod);
            annotatable.annotate(annotationClazz);
        }
        else if (annotationClass instanceof JClass)
        {
            annotatable.annotate((JClass) annotationClass);
        }
        else if (annotationClass instanceof Class)
        {
            annotatable.annotate((Class<? extends Annotation>) annotationClass);
        }
        else
        {
            throw new IllegalStateException("Found annotation: " + annotationClass + " for HTTP method: "
                                            + httpMethod);
        }

        return this;
    }

    public JType getGeneratorType(final Class<?> clazz)
    {
        return clazz.isPrimitive() ? JType.parse(codeModel, clazz.getSimpleName()) : codeModel.ref(clazz);
    }

    public JClass generateClassFromJsonSchema(final String className, final URL schemaUrl) throws IOException
    {
    	return schemaMapper.generate(codeModel, className, getModelPackage(), schemaUrl).boxify();
    }

    private JDefinedClass createCustomHttpMethodAnnotation(final String httpMethod)
        throws JClassAlreadyExistsException
    {
        final JPackage pkg = codeModel._package(getSupportPackage());
        final JDefinedClass annotationClazz = pkg._annotationTypeDeclaration(httpMethod);
        annotationClazz.annotate(Target.class).param("value", ElementType.METHOD);
        annotationClazz.annotate(Retention.class).param("value", RetentionPolicy.RUNTIME);
        annotationClazz.annotate(HttpMethod.class).param("value", httpMethod);
        annotationClazz.javadoc().add("Custom JAX-RS support for HTTP " + httpMethod + ".");
        httpMethodAnnotations.put(httpMethod.toUpperCase(), annotationClazz);
        return annotationClazz;
    }

    private String getModelPackage()
    {
        return configuration.getBasePackageName() + ".model";
    }

    private String getSupportPackage()
    {
        return configuration.getBasePackageName() + ".support";
    }
}
