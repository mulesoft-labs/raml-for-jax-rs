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

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.jci.compilers.CompilationResult;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hamcrest.Matchers;
import org.jsonschema2pojo.AnnotationStyle;
import org.junit.Test;

import javax.ws.rs.Path;
import java.net.URLClassLoader;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class MultipleIncludesTestCase extends AbstractGeneratorTestCase {
    @Test
    public void runWithExtension() throws Exception {
        Configuration configuration = createConfigurationForSourceDirectory("/org/raml/includes");
        configuration.setJaxrsVersion(Configuration.JaxrsVersion.JAXRS_2_0);
        configuration.setJsonMapper(AnnotationStyle.JACKSON2);
        configuration.setUseJsr303Annotations(true);
        generateSourceAndAddToSet(configuration, "/org/raml/includes/multipleIncludes.yaml");
        CompilationResult compilationResult = compileSources("1.7", "eclipse");
        assertThat(ToStringBuilder.reflectionToString(compilationResult.getErrors(), ToStringStyle.SHORT_PREFIX_STYLE),
                compilationResult.getErrors(), Matchers.is(Matchers.emptyArray()));
        URLClassLoader classLoader = createClassLoaderForGeneratedClasses();
        Class<?> resourceClass = classLoader.loadClass("org.raml.jaxrs.test.resource.MultipleResource");
        assertThat(resourceClass.getAnnotation(Path.class).value(), is("multiple"));
        Class<?> errorJson = classLoader.loadClass("org.raml.jaxrs.test.model.ErrorJson");
        assertThat(errorJson.getAnnotation(JsonPropertyOrder.class).value(), is(notNullValue()));
    }
}
