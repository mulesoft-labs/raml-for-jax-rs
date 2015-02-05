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
    private boolean useJsr303Annotations = false;
    private AnnotationStyle jsonMapper = AnnotationStyle.JACKSON1;
    private File sourceDirectory;
    private Class methodThrowException = Exception.class;
    private Map<String, String> jsonMapperConfiguration;
    private String asyncResourceTrait;
	private boolean emptyResponseReturnVoid;
	private boolean generateClientInterface;
	
	public boolean isGenerateClientInterface() {
		return generateClientInterface;
	}

	public void setGenerateClientInterface(boolean generateClientInterface) {
		this.generateClientInterface = generateClientInterface;
	}

	private List<GeneratorExtension> extensions = new ArrayList<GeneratorExtension>();
    
    

	public String getAsyncResourceTrait()
    {
         return asyncResourceTrait;
    }
    
    public void setAsyncResourceTrait(final String asyncResourceTrait)
    {
         this.asyncResourceTrait = asyncResourceTrait;
    }

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

    public File getOutputDirectory()
    {
        return outputDirectory;
    }

    public void setOutputDirectory(final File outputDirectory)
    {
        this.outputDirectory = outputDirectory;
    }

    public JaxrsVersion getJaxrsVersion()
    {
        return jaxrsVersion;
    }

    public void setJaxrsVersion(final JaxrsVersion jaxrsVersion)
    {
        this.jaxrsVersion = jaxrsVersion;
    }

    public String getBasePackageName()
    {
        return basePackageName;
    }

    public void setBasePackageName(final String basePackageName)
    {
        this.basePackageName = basePackageName;
    }

    public boolean isUseJsr303Annotations()
    {
        return useJsr303Annotations;
    }

    public void setUseJsr303Annotations(final boolean useJsr303Annotations)
    {
        this.useJsr303Annotations = useJsr303Annotations;
    }

    public AnnotationStyle getJsonMapper()
    {
        return jsonMapper;
    }

    public void setJsonMapper(final AnnotationStyle jsonMapper)
    {
        this.jsonMapper = jsonMapper;
    }
    
    public Class getMethodThrowException() {
        return methodThrowException;
    }
    
    public void setMethodThrowException(Class methodThrowException) {
        this.methodThrowException = methodThrowException;
    }

    public File getSourceDirectory() {
        return sourceDirectory;
    }

    public void setSourceDirectory(File sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }
    
    public Map<String, String> getJsonMapperConfiguration()
    {
       return jsonMapperConfiguration;
    }
    
    public void setJsonMapperConfiguration(Map<String, String> jsonMapperConfiguration)
    {
       this.jsonMapperConfiguration = jsonMapperConfiguration;
    }

	public boolean isEmptyResponseReturnVoid() {
		return emptyResponseReturnVoid;
	}
	
	public void setEmptyResponseReturnVoid(boolean emptyResponseReturnVoid) {
		this.emptyResponseReturnVoid = emptyResponseReturnVoid;
	}
	
	public List<GeneratorExtension> getExtensions() {
		return this.extensions;
	}
	

}
