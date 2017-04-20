/*
 * Copyright 2013-2017 (c) MuleSoft, Inc.
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
package org.raml.jaxrs.parser.analyzers;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.RuntimeResource;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.raml.jaxrs.model.JaxRsApplication;
import org.raml.jaxrs.model.JaxRsSupportedAnnotation;
import org.raml.jaxrs.parser.model.JerseyJaxRsApplication;
import org.raml.jaxrs.parser.source.SourceParser;
import org.raml.utilities.iterables.Iterables;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static org.raml.utilities.matchers.Matchers.contentEqualsInAnyOrder;

public class JerseyAnalyzerTest {

  @Mock
  JerseyBridge jerseyBridge;

  @Mock
  SourceParser sourceParser;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  private JerseyAnalyzer makeAnalyzerFor(Iterable<Class<?>> classes,
                                         Set<JaxRsSupportedAnnotation> supportedAnnotations) {
    return JerseyAnalyzer.create(classes, jerseyBridge, sourceParser, supportedAnnotations);
  }

  private static <T> Supplier<T> mockSupplierFor(final Class<? extends T> clazz) {
    return new Supplier<T>() {

      @Override
      public T get() {
        return mock(clazz);
      }
    };
  }

  private <T> FluentIterable<T> supplyingNTimes(int noItems, Supplier<? extends T> supplier) {
    return FluentIterable.from(Iterables.suplying(supplier)).limit(noItems);
  }

  private Iterable<Class<?>> testClasses() {
    return Lists.<Class<?>>newArrayList(String.class, Integer.class, Double.class, Byte.class);
  }

  // @Test
  /*
   * public void testAnalyze() { FluentIterable<Class<?>> classes = FluentIterable.from(testClasses());
   * 
   * JerseyAnalyzer analyzer = makeAnalyzerFor(classes, supportedAnnotations);
   * 
   * FluentIterable<Resource> resources = classes.transform(new Function<Class<?>, Resource>() {
   * 
   * @Nullable
   * 
   * @Override public Resource apply(@Nullable Class<?> aClass) { return mock(Resource.class); } }); Matcher<Iterable<Class<?>>>
   * classesMatcher = contentEqualsInAnyOrder(classes);
   * when(jerseyBridge.resourcesFrom(argThat(classesMatcher))).thenReturn(resources);
   * 
   * List<RuntimeResource> runtimeResources = supplyingNTimes(3, mockSupplierFor(RuntimeResource.class)).toList();
   * when(jerseyBridge.runtimeResourcesFrom(resources)).thenReturn(runtimeResources);
   * 
   * JaxRsApplication application = analyzer.analyze();
   * 
   * InOrder inOrder = Mockito.inOrder(jerseyBridge); inOrder.verify(jerseyBridge).resourcesFrom(argThat(classesMatcher));
   * inOrder.verify(jerseyBridge).runtimeResourcesFrom(resources);
   * 
   * assertTrue(application instanceof JerseyJaxRsApplication); }
   */


}
