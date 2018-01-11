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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.ramltypes.GParameter;
import org.raml.jaxrs.generator.ramltypes.GResponse;
import org.raml.jaxrs.generator.ramltypes.GResponseType;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 12/10/16. Just potential zeroes and ones
 */
public class V10GResponse implements GResponse {

  private final Response response;
  private final List<GResponseType> bodies;
  private final List<GParameter> headers;

  public V10GResponse(final CurrentBuild currentBuild, final V10GResource v10GResource,
                      final Method method, final Response response) {
    this.response = response;
    this.bodies =
        Lists.transform(this.response.body(), new Function<TypeDeclaration, GResponseType>() {

          @Nullable
          @Override
          public GResponseType apply(@Nullable TypeDeclaration input) {
            return new V10GResponseType(input, currentBuild.fetchType(v10GResource.implementation(),
                                                                      method, response, input));
          }
        });

    this.headers = Lists.transform(response.headers(), new Function<TypeDeclaration, GParameter>() {

      @Nullable
      @Override
      public GParameter apply(@Nullable TypeDeclaration input) {
        return new V10PGParameter(input, currentBuild.fetchType(input.type(), input));
      }
    });
  }

  @Override
  public Response implementation() {
    return response;
  }

  @Override
  public List<GResponseType> body() {
    return bodies;
  }

  @Override
  public String code() {
    return response.code().value();
  }

  @Override
  public List<GParameter> headers() {
    return headers;
  }
}
