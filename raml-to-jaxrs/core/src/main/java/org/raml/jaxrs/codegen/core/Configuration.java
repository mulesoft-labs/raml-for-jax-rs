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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jsonschema2pojo.AnnotationStyle;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;
import org.raml.jaxrs.codegen.core.ext.AbstractGeneratorExtension;
import org.raml.jaxrs.codegen.core.ext.GeneratorExtension;

/**
 * <p>Configuration class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class Configuration
{
    public enum JaxrsVersion
    {
        JAXRS_1_1("1.1"), JAXRS_2_0("2.0");//TEST

        private final String alias;

        private JaxrsVersion(final String alias)
        {
            this.alias = alias;
        }

        public static JaxrsVersion fromAlias(final String alias)
        {
            final List<String> supportedAliases = new ArrayList<String>();

            for (final JaxrsVersion jaxrsVersion : JaxrsVersion.values())
            {
                if (jaxrsVersion.alias.equals(alias))
                {
                    return jaxrsVersion;
                }
                supportedAliases.add(jaxrsVersion.alias);
            }

            throw new IllegalArgumentException(alias + " is not a supported JAX-RS version ("
                                               + StringUtils.join(supportedAliases, ',') + ")");
        }
    };

    private File outputDirectory;
    private JaxrsVersion jaxrsVersion = JaxrsVersion.JAXRS_1_1;
    private String basePackageName;
    private String modelPackageName = "model";
    private boolean useJsr303Annotations = false;
    private AnnotationStyle jsonMapper = AnnotationStyle.JACKSON1;
    private File sourceDirectory;
    private Class methodThrowException = Exception.class;
    private Map<String, String> jsonMapperConfiguration;
    private String asyncResourceTrait;
	private boolean emptyResponseReturnVoid;
	private boolean generateClientInterface;

	/**
	 * <p>isGenerateClientInterface.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isGenerateClientInterface() {
		return generateClientInterface;
	}

	/**
	 * <p>Setter for the field <code>generateClientInterface</code>.</p>
	 *
	 * @param generateClientInterface a boolean.
	 */
	public void setGenerateClientInterface(boolean generateClientInterface) {
		this.generateClientInterface = generateClientInterface;
	}

	private List<GeneratorExtension> extensions = new ArrayList<GeneratorExtension>();



	/**
	 * <p>Getter for the field <code>asyncResourceTrait</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getAsyncResourceTrait()
    {
         return asyncResourceTrait;
    }

    /**
     * <p>Setter for the field <code>asyncResourceTrait</code>.</p>
     *
     * @param asyncResourceTrait a {@link java.lang.String} object.
     */
    public void setAsyncResourceTrait(final String asyncResourceTrait)
    {
         this.asyncResourceTrait = asyncResourceTrait;
    }

    /**
     * <p>createJsonSchemaGenerationConfig.</p>
     *
     * @return a {@link org.jsonschema2pojo.GenerationConfig} object.
     */
    public GenerationConfig createJsonSchemaGenerationConfig()
    {
        return new DefaultGenerationConfig()
        {
            @Override
            public AnnotationStyle getAnnotationStyle()
            {
                return jsonMapper;
            }

            @Override
            public boolean isIncludeJsr303Annotations()
            {
                return useJsr303Annotations;
            }
            @Override
            public boolean isGenerateBuilders()
            {
                return getConfiguredValue("generateBuilders", true);
            }

            @Override
            public boolean isIncludeHashcodeAndEquals()
            {
                return getConfiguredValue("includeHashcodeAndEquals", false);
            }

            @Override
            public boolean isIncludeToString()
            {
                return getConfiguredValue("includeToString", false);
            }

            @Override
            public boolean isUseLongIntegers()
            {
                return getConfiguredValue("useLongIntegers", false);
            }

            private boolean getConfiguredValue(final String key, final boolean def)
            {
                if (jsonMapperConfiguration == null || jsonMapperConfiguration.isEmpty())
                {
                    return def;
               }

                final String val = jsonMapperConfiguration.get(key);
                return val!=null?Boolean.parseBoolean(val): def;
            }
        };
    }

    /**
     * <p>Getter for the field <code>outputDirectory</code>.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public File getOutputDirectory()
    {
        return outputDirectory;
    }

    /**
     * <p>Setter for the field <code>outputDirectory</code>.</p>
     *
     * @param outputDirectory a {@link java.io.File} object.
     */
    public void setOutputDirectory(final File outputDirectory)
    {
        this.outputDirectory = outputDirectory;
    }

    /**
     * <p>Getter for the field <code>jaxrsVersion</code>.</p>
     *
     * @return a {@link org.raml.jaxrs.codegen.core.Configuration.JaxrsVersion} object.
     */
    public JaxrsVersion getJaxrsVersion()
    {
        return jaxrsVersion;
    }

    /**
     * <p>Setter for the field <code>jaxrsVersion</code>.</p>
     *
     * @param jaxrsVersion a {@link org.raml.jaxrs.codegen.core.Configuration.JaxrsVersion} object.
     */
    public void setJaxrsVersion(final JaxrsVersion jaxrsVersion)
    {
        this.jaxrsVersion = jaxrsVersion;
    }

    /**
     * <p>Getter for the field <code>basePackageName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getBasePackageName()
    {
        return basePackageName;
    }

    /**
     * <p>Setter for the field <code>basePackageName</code>.</p>
     *
     * @param basePackageName a {@link java.lang.String} object.
     */
    public void setBasePackageName(final String basePackageName)
    {
        this.basePackageName = basePackageName;
    }

    /**
     * <p>Getter for the field <code>modelPackageName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getModelPackageName()
    {
        return modelPackageName;
    }

    /**
     * <p>Setter for the field <code>modelPackageName</code>.</p>
     *
     * @param modelPackageName a {@link java.lang.String} object.
     */
    public void setModelPackageName(final String modelPackageName)
    {
        this.modelPackageName = modelPackageName;
    }

    /**
     * <p>isUseJsr303Annotations.</p>
     *
     * @return a boolean.
     */
    public boolean isUseJsr303Annotations()
    {
        return useJsr303Annotations;
    }

    /**
     * <p>Setter for the field <code>useJsr303Annotations</code>.</p>
     *
     * @param useJsr303Annotations a boolean.
     */
    public void setUseJsr303Annotations(final boolean useJsr303Annotations)
    {
        this.useJsr303Annotations = useJsr303Annotations;
    }

    /**
     * <p>Getter for the field <code>jsonMapper</code>.</p>
     *
     * @return a {@link org.jsonschema2pojo.AnnotationStyle} object.
     */
    public AnnotationStyle getJsonMapper()
    {
        return jsonMapper;
    }

    /**
     * <p>Setter for the field <code>jsonMapper</code>.</p>
     *
     * @param jsonMapper a {@link org.jsonschema2pojo.AnnotationStyle} object.
     */
    public void setJsonMapper(final AnnotationStyle jsonMapper)
    {
        this.jsonMapper = jsonMapper;
    }

    /**
     * <p>Getter for the field <code>methodThrowException</code>.</p>
     *
     * @return a {@link java.lang.Class} object.
     */
    public Class getMethodThrowException() {
        return methodThrowException;
    }

    /**
     * <p>Setter for the field <code>methodThrowException</code>.</p>
     *
     * @param methodThrowException a {@link java.lang.Class} object.
     */
    public void setMethodThrowException(Class methodThrowException) {
        this.methodThrowException = methodThrowException;
    }

    /**
     * <p>Getter for the field <code>sourceDirectory</code>.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public File getSourceDirectory() {
        return sourceDirectory;
    }

    /**
     * <p>Setter for the field <code>sourceDirectory</code>.</p>
     *
     * @param sourceDirectory a {@link java.io.File} object.
     */
    public void setSourceDirectory(File sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    /**
     * <p>Getter for the field <code>jsonMapperConfiguration</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, String> getJsonMapperConfiguration()
    {
       return jsonMapperConfiguration;
    }

    /**
     * <p>Setter for the field <code>jsonMapperConfiguration</code>.</p>
     *
     * @param jsonMapperConfiguration a {@link java.util.Map} object.
     */
    public void setJsonMapperConfiguration(Map<String, String> jsonMapperConfiguration)
    {
       this.jsonMapperConfiguration = jsonMapperConfiguration;
    }

	/**
	 * <p>isEmptyResponseReturnVoid.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isEmptyResponseReturnVoid() {
		return emptyResponseReturnVoid;
	}

	/**
	 * <p>Setter for the field <code>emptyResponseReturnVoid</code>.</p>
	 *
	 * @param emptyResponseReturnVoid a boolean.
	 */
	public void setEmptyResponseReturnVoid(boolean emptyResponseReturnVoid) {
		this.emptyResponseReturnVoid = emptyResponseReturnVoid;
	}

	/**
	 * <p>Getter for the field <code>extensions</code>.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<GeneratorExtension> getExtensions() {
		return this.extensions;
	}


}
