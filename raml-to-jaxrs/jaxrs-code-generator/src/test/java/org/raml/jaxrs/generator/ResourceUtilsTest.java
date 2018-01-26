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
package org.raml.jaxrs.generator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.raml.jaxrs.generator.ramltypes.GParameter;
import org.raml.jaxrs.generator.ramltypes.GResource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by Jean-Philippe Belanger on 5/13/17. Just potential zeroes and ones
 */
public class ResourceUtilsTest {

  @Mock
  private GResource resource;

  @Mock
  GResource topResource;

  @Mock
  private GParameter one;

  @Mock
  private GParameter two;

  @Mock
  private GParameter three;

  @Before
  public void mockito() {

    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void accumulateUriParameters() throws Exception {

    when(resource.relativePath()).thenReturn("/fun");
    when(one.name()).thenReturn("one");
    when(two.name()).thenReturn("two");
    when(resource.uriParameters()).thenReturn(Arrays.asList(one, two));

    List<GParameter> parameters = ResourceUtils.accumulateUriParameters(resource);

    assertEquals(2, parameters.size());
    assertEquals(one, parameters.get(0));
    assertEquals(two, parameters.get(1));
  }


  @Test
  public void accumulateUriParametersFromParent() throws Exception {

    when(one.name()).thenReturn("one");
    when(two.name()).thenReturn("two");
    when(three.name()).thenReturn("three");

    when(topResource.relativePath()).thenReturn("/fun/");
    when(resource.relativePath()).thenReturn("/allo");

    when(resource.parentResource()).thenReturn(topResource);
    when(topResource.uriParameters()).thenReturn(Arrays.asList(one, two));
    when(resource.uriParameters()).thenReturn(Collections.singletonList(three));

    List<GParameter> parameters = ResourceUtils.accumulateUriParameters(resource);

    assertEquals(3, parameters.size());
    assertEquals(one, parameters.get(0));
    assertEquals(two, parameters.get(1));
    assertEquals(three, parameters.get(2));
  }

  @Test
  public void accumulateImplicitParameter() throws Exception {

    when(resource.relativePath()).thenReturn("/fun/{id}");
    when(resource.uriParameters()).thenReturn(Collections.<GParameter>emptyList());

    List<GParameter> parameters = ResourceUtils.accumulateUriParameters(resource);

    assertEquals(1, parameters.size());
    assertEquals("id", parameters.get(0).name());
  }

  @Test
  public void accumulateImplicitUriParametersFromParent() throws Exception {

    when(resource.parentResource()).thenReturn(topResource);
    when(topResource.relativePath()).thenReturn("/fun/{id}/{color}");
    when(resource.relativePath()).thenReturn("/{goo}");

    List<GParameter> parameters = ResourceUtils.accumulateUriParameters(resource);

    assertEquals(3, parameters.size());
    assertEquals("id", parameters.get(0).name());
    assertEquals("color", parameters.get(1).name());
    assertEquals("goo", parameters.get(2).name());
  }

  @Test
  public void accumulateDeclaredLater() throws Exception {

    when(resource.parentResource()).thenReturn(topResource);
    when(topResource.relativePath()).thenReturn("/fun/{id}");
    when(resource.relativePath()).thenReturn("/fun");
    when(one.name()).thenReturn("id");
    when(resource.uriParameters()).thenReturn(Collections.singletonList(one));


    List<GParameter> parameters = ResourceUtils.accumulateUriParameters(resource);

    assertEquals(1, parameters.size());
    assertEquals("id", parameters.get(0).name());
  }

}
