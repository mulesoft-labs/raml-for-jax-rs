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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.HttpMethod;

import org.aml.apimodel.Api;
import org.aml.raml2java.JavaWriter;
import org.aml.typesystem.AbstractType;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.jsonschema2pojo.Annotator;
import org.jsonschema2pojo.AnnotatorFactory;
import org.jsonschema2pojo.CompositeAnnotator;
import org.jsonschema2pojo.GenerationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXParseException;

import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.ErrorReceiver;
import com.sun.tools.xjc.Language;
import com.sun.tools.xjc.ModelLoader;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.util.ErrorReceiverFilter;

class Context
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Context.class);

    private final Configuration configuration;
    private final JCodeModel codeModel;
    private final Map<String, Set<String>> resourcesMethods;
    private final Map<String, Object> httpMethodAnnotations;

    private boolean shouldGenerateResponseWrapper = false;
    private JDefinedClass currentResourceInterface;
    private JavaWriter writer;

    /**
     * <p>ref.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link com.sun.codemodel.JType} object.
     */
    public JType ref(String name){
    	return codeModel.ref(name);
    }

    /**
     * <p>getType.</p>
     *
     * @param tp a {@link org.aml.typesystem.AbstractType} object.
     * @return a {@link com.sun.codemodel.JType} object.
     */
    public JType getType(AbstractType tp){
    	return writer.getType(tp);
    }
    
    /**
     * <p>Constructor for Context.</p>
     *
     * @param configuration a {@link org.raml.jaxrs.codegen.core.Configuration} object.
     * @param raml a {@link org.aml.apimodel.Api} object.
     * @throws java.io.IOException if any.
     */
    public Context(final Configuration configuration, final Api raml) throws IOException
    {
        Validate.notNull(configuration, "configuration can't be null");
        Validate.notNull(raml, "raml can't be null");

        this.configuration = configuration;
        writer=new JavaWriter();
        codeModel = writer.getModel();
        writer.getConfig().setJacksonSupport(true);
        writer.getConfig().setJaxbSupport(true);
        writer.setDefaultPackageName(configuration.getBasePackageName()+"."+configuration.getModelPackageName());
        resourcesMethods = new HashMap<String, Set<String>>();

        // prime the HTTP method annotation cache
        httpMethodAnnotations = new HashMap<String, Object>();
        for (final Class<? extends Annotation> clazz : JAXRS_HTTP_METHODS)
        {
            httpMethodAnnotations.put(clazz.getSimpleName(), clazz);
        }

        
        // configure the JSON -> POJO generator
        final GenerationConfig jsonSchemaGenerationConfig = configuration.createJsonSchemaGenerationConfig();
//        schemaMapper = new SchemaMapper(new RuleFactory(jsonSchemaGenerationConfig, getAnnotator(jsonSchemaGenerationConfig),
//                new SchemaStore()), new SchemaGenerator());
        
        
    }



    /**
     *
     * @param config
     * @return
     */
    private static Annotator getAnnotator(GenerationConfig config) {
        Annotator coreAnnotator = new AnnotatorFactory().getAnnotator(config.getAnnotationStyle());
        if(config.getCustomAnnotator() != null){
            Annotator customAnnotator = new AnnotatorFactory().getAnnotator(config.getCustomAnnotator());
            return new CompositeAnnotator(coreAnnotator, customAnnotator);
        }
        return coreAnnotator;
    }

    /**
     * <p>generate.</p>
     *
     * @return a {@link java.util.Set} object.
     * @throws java.io.IOException if any.
     */
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

        

        return generatedFiles;
    }

    

    /**
     * <p>Getter for the field <code>configuration</code>.</p>
     *
     * @return a {@link org.raml.jaxrs.codegen.core.Configuration} object.
     */
    public Configuration getConfiguration()
    {
        return configuration;
    }

    /**
     * <p>Getter for the field <code>currentResourceInterface</code>.</p>
     *
     * @return a {@link com.sun.codemodel.JDefinedClass} object.
     */
    public JDefinedClass getCurrentResourceInterface()
    {
        return currentResourceInterface;
    }

    /**
     * <p>Setter for the field <code>currentResourceInterface</code>.</p>
     *
     * @param currentResourceInterface a {@link com.sun.codemodel.JDefinedClass} object.
     */
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

    /**
     * <p>getResponseWrapperType.</p>
     *
     * @return a {@link com.sun.codemodel.JClass} object.
     */
    public JClass getResponseWrapperType()
    {
        shouldGenerateResponseWrapper = true;

        return codeModel.directClass(getSupportPackage() + ".ResponseWrapper");
    }

    /**
     * <p>createResourceInterface.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link com.sun.codemodel.JDefinedClass} object.
     * @throws java.lang.Exception if any.
     */
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

        final JPackage pkg = codeModel._package(configuration.getBasePackageName() + "." +configuration.getRestIFPackageName());
        return pkg._interface(actualName);
    }

    /**
     * <p>createResourceMethod.</p>
     *
     * @param resourceInterface a {@link com.sun.codemodel.JDefinedClass} object.
     * @param methodName a {@link java.lang.String} object.
     * @param returnType a {@link com.sun.codemodel.JType} object.
     * @return a {@link com.sun.codemodel.JMethod} object.
     */
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

    /**
     * <p>createResourceEnum.</p>
     *
     * @param resourceInterface a {@link com.sun.codemodel.JDefinedClass} object.
     * @param name a {@link java.lang.String} object.
     * @param values a {@link java.util.List} object.
     * @return a {@link com.sun.codemodel.JDefinedClass} object.
     * @throws java.lang.Exception if any.
     */
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
    /**
     * <p>getGeneratorClass.</p>
     *
     * @param clazzFQN a {@link java.lang.String} object.
     * @return a {@link com.sun.codemodel.JClass} object.
     */
    public JClass getGeneratorClass(final String clazzFQN)
    {
          return codeModel.ref(clazzFQN);
    }

    /**
     * <p>addHttpMethodAnnotation.</p>
     *
     * @param httpMethod a {@link java.lang.String} object.
     * @param annotatable a {@link com.sun.codemodel.JAnnotatable} object.
     * @return a {@link org.raml.jaxrs.codegen.core.Context} object.
     * @throws java.lang.Exception if any.
     */
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

    /**
     * <p>getGeneratorType.</p>
     *
     * @param clazz a {@link java.lang.Class} object.
     * @return a {@link com.sun.codemodel.JType} object.
     */
    public JType getGeneratorType(final Class<?> clazz)
    {
        return clazz.isPrimitive() ? JType.parse(codeModel, clazz.getSimpleName()) : codeModel.ref(clazz);
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
        return configuration.getBasePackageName()
            .concat(".")
            .concat(configuration.getModelPackageName());
    }

    private String getSupportPackage()
    {
        return configuration.getBasePackageName() + "." +configuration.getRestIFPackageName() +".support";
    }

    /**
     * <p>generateClassesFromXmlSchemas.</p>
     *
     * @param schemaFiles a {@link java.util.Map} object.
     * @return a {@link java.util.Map} object.
     */
    public Map<String, JClass> generateClassesFromXmlSchemas(Map<String, File> schemaFiles)
    {
        Map<String, JClass> result = new HashMap<String, JClass>();
        if (schemaFiles == null || schemaFiles.isEmpty()) {
            return result;
        }

        HashMap<String, String> classNameToKeyMap = new HashMap<String, String>();
        for (Map.Entry<String, File> entry : schemaFiles.entrySet()) {
            String key = entry.getKey();
            File file = entry.getValue();

            List<JDefinedClass> classes = generateClassesFromXmlSchemas(
                    new JCodeModel(), file);
            if (classes == null || classes.isEmpty()) {
                continue;
            }
            String className = classes.get(0).name();
            if (className != null) {
                classNameToKeyMap.put(className, key);
            }
        }

        File[] fileArray = schemaFiles.values().toArray(new File[schemaFiles.size()]);
        List<JDefinedClass> classList = generateClassesFromXmlSchemas(codeModel, fileArray);
        for (JDefinedClass cl : classList) {
            String name = cl.name();
            String key = classNameToKeyMap.get(name);
            if (key == null) {
                continue;
            }
            result.put(key, cl);
        }
        return result;
    }

    private List<JDefinedClass> generateClassesFromXmlSchemas(JCodeModel codeModel, File... schemaFiles)
    {
        ArrayList<JDefinedClass> classList = new ArrayList<JDefinedClass>();

        ArrayList<String> argList = new ArrayList<String>();
        argList.add("-mark-generated");
        argList.add("-p");
        argList.add(getModelPackage());
        for (File f : schemaFiles) {
            argList.add(f.getAbsolutePath());
        }

        String[] args = argList.toArray(new String[argList.size()]);

        final Options opt = new Options();
        opt.setSchemaLanguage(Language.XMLSCHEMA);
        try {
            opt.parseArguments(args);
        } catch (Exception e) {
        }

        ErrorReceiver receiver = new ErrorReceiverFilter() {
            @Override
            public void info(SAXParseException exception) {
                if (opt.verbose)
                    super.info(exception);
            }

            @Override
            public void warning(SAXParseException exception) {
                if (!opt.quiet)
                    super.warning(exception);
            }

        };
        try {
            Model model = ModelLoader.load(opt, codeModel, receiver);
            Outline outline = model.generateCode(opt, receiver);
            for (ClassOutline co : outline.getClasses()) {
                JDefinedClass cl = co.implClass;
                if (cl.outer() == null) {
                    classList.add(cl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classList;
    }

	/**
	 * <p>Getter for the field <code>codeModel</code>.</p>
	 *
	 * @return a {@link com.sun.codemodel.JCodeModel} object.
	 */
	public JCodeModel getCodeModel() {
		return codeModel;
	}
}
