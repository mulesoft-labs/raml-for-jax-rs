/*
 * Copyright 2013-2018 (c) MuleSoft, Inc.
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

import amf.client.model.domain.EndPoint;
import amf.client.model.domain.Parameter;
import android.hardware.Camera.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.raml.jaxrs.generator.ramltypes.GParameter;
import org.raml.jaxrs.generator.ramltypes.GResource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by Jean-Philippe Belanger on 5/13/17. Just potential zeroes and ones
 */
public class ResourceUtilsTest {

  private EndPoint resource = new EndPoint().withPath("/fun");

  EndPoint topResource = new EndPoint();

  private Parameter one = new Parameter().withName("one");

  private Parameter two = new Parameter().withName("two");

  private Parameter three = new Parameter().withName("three");

  @Test
  public void accumulateUriParameters() throws Exception {

    List<Parameter> parameters = ResourceUtils.accumulateUriParameters(resource.withParameters(Arrays.asList(one, two)));

    assertEquals(2, parameters.size());
    assertEquals(one, parameters.get(0));
    assertEquals(two, parameters.get(1));
  }


  /*
   * @Test public void accumulateUriParametersFromParent() throws Exception {
   * 
   * when(one.name()).thenReturn("one"); when(two.name()).thenReturn("two"); when(three.name()).thenReturn("three");
   * 
   * when(topResource.relativePath()).thenReturn("/fun/"); when(resource.relativePath()).thenReturn("/allo");
   * 
   * when(resource.parentResource()).thenReturn(topResource); when(topResource.uriParameters()).thenReturn(Arrays.asList(one,
   * two)); when(resource.uriParameters()).thenReturn(Collections.singletonList(three));
   * 
   * List<GParameter> parameters = ResourceUtils.accumulateUriParameters(resource);
   * 
   * assertEquals(3, parameters.size()); assertEquals(one, parameters.get(0)); assertEquals(two, parameters.get(1));
   * assertEquals(three, parameters.get(2)); }
   * 
   * @Test public void accumulateImplicitParameter() throws Exception {
   * 
   * when(one.name()).thenReturn("one");
   * 
   * when(resource.relativePath()).thenReturn("/fun/{id}");
   * when(resource.uriParameters()).thenReturn(Collections.singletonList(one));
   * 
   * List<GParameter> parameters = ResourceUtils.accumulateUriParameters(resource);
   * 
   * assertEquals(1, parameters.size()); assertEquals("one", parameters.get(0).name()); }
   * 
   * @Test public void accumulateImplicitUriParametersFromParent() throws Exception {
   * 
   * when(one.name()).thenReturn("id"); when(two.name()).thenReturn("color"); when(three.name()).thenReturn("goo");
   * 
   * when(topResource.relativePath()).thenReturn("/fun/{id}/{color}");
   * when(topResource.uriParameters()).thenReturn(Arrays.asList(one, two));
   * 
   * when(resource.parentResource()).thenReturn(topResource); when(resource.relativePath()).thenReturn("/{goo}");
   * when(resource.uriParameters()).thenReturn(Collections.singletonList(three));
   * 
   * List<GParameter> parameters = ResourceUtils.accumulateUriParameters(resource);
   * 
   * assertEquals(3, parameters.size()); assertEquals("id", parameters.get(0).name()); assertEquals("color",
   * parameters.get(1).name()); assertEquals("goo", parameters.get(2).name()); }
   * 
   * @Test public void accumulateDeclaredLater() throws Exception {
   * 
   * when(topResource.relativePath()).thenReturn("/fun/{id}"); when(resource.parentResource()).thenReturn(topResource);
   * when(resource.relativePath()).thenReturn("/fun"); when(one.name()).thenReturn("id");
   * when(resource.uriParameters()).thenReturn(Collections.singletonList(one));
   * 
   * 
   * List<GParameter> parameters = ResourceUtils.accumulateUriParameters(resource);
   * 
   * assertEquals(1, parameters.size()); assertEquals("id", parameters.get(0).name()); }
   */
}
