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
package org.raml.jaxrs.generator.v10;

import org.junit.Test;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GAbstractionFactory;
import org.raml.jaxrs.generator.ramltypes.GRequest;
import org.raml.jaxrs.generator.ramltypes.GResponseType;
import org.raml.jaxrs.generator.utils.RamlV10;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.resources.Resource;

import static org.junit.Assert.assertEquals;

/**
 * Created by Jean-Philippe Belanger on 12/11/16. Just potential zeroes and ones
 */
public class V10GResourceTest {

  @Test
  public void simpleRequest() throws Exception {

    Api api = RamlV10.buildApiV10(this, "resource-simple.raml");
    GAbstractionFactory fac = new GAbstractionFactory();
    V10GResource gr = new V10GResource(new CurrentBuild(api, null), fac, api.resources().get(0));
    GRequest req = gr.methods().get(0).body().get(0);
    assertEquals("application/json", req.mediaType());
    assertEquals("ObjectBase", req.type().type());
    assertEquals("ObjectBase", req.type().name());
  }

  @Test
  public void extendingRequest() throws Exception {

    Api api = RamlV10.buildApiV10(this, "resource-extending-request.raml");
    GAbstractionFactory fac = new GAbstractionFactory();
    V10GResource gr = new V10GResource(new CurrentBuild(api, null), fac, api.resources().get(0));
    GRequest req = gr.methods().get(0).body().get(0);
    assertEquals("application/json", req.mediaType());
    assertEquals("ObjectBase", req.type().type());
    assertEquals("FunPutApplicationJson", req.type().name());
    assertEquals("model.FunPutApplicationJson", req.type().defaultJavaTypeName("").toString());
  }

  @Test
  public void simpleResponse() throws Exception {

    Api api = RamlV10.buildApiV10(this, "resource-response-simple.raml");
    GAbstractionFactory fac = new GAbstractionFactory();
    V10GResource gr = new V10GResource(new CurrentBuild(api, null), fac, api.resources().get(0));
    GResponseType resp = gr.methods().get(0).responses().get(0).body().get(0);
    assertEquals("application/json", resp.mediaType());
    assertEquals("ObjectBase", resp.type().type());
    assertEquals("ObjectBase", resp.type().name());
  }

  @Test
  public void extendingResponse() throws Exception {

    Api api = RamlV10.buildApiV10(this, "resource-response-extending.raml");
    GAbstractionFactory fac = new GAbstractionFactory();
    V10GResource gr = new V10GResource(new CurrentBuild(api, null), fac, api.resources().get(0));
    GResponseType req = gr.methods().get(0).responses().get(0).body().get(0);
    assertEquals("application/json", req.mediaType());
    assertEquals("ObjectBase", req.type().type());
    assertEquals("FunPut200ApplicationJson", req.type().name());
    assertEquals("model.FunPut200ApplicationJson", req.type().defaultJavaTypeName("").toString());
  }

}
