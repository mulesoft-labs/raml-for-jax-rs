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
package org.raml.jaxrs.generator.v08;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.raml.jaxrs.generator.ramltypes.GMethod;
import org.raml.jaxrs.generator.ramltypes.GParameter;
import org.raml.jaxrs.generator.ramltypes.GRequest;
import org.raml.jaxrs.generator.ramltypes.GResource;
import org.raml.jaxrs.generator.ramltypes.GResponse;
import org.raml.v2.api.model.v08.bodies.BodyLike;
import org.raml.v2.api.model.v08.bodies.Response;
import org.raml.v2.api.model.v08.methods.Method;
import org.raml.v2.api.model.v08.parameters.Parameter;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * Created by Jean-Philippe Belanger on 12/11/16. Just potential zeroes and ones
 */
public class V08Method implements GMethod {

  private final V08GResource v08GResource;
  private final List<GParameter> queryParameters;
  private final List<GResponse> responses;
  private final Method input;
  private final List<GParameter> headers;

  private List<GRequest> requests;

  public V08Method(final V08GResource v08GResource, final Method input,
                   final Set<String> globalSchemas, final V08TypeRegistry registry) {
    this.v08GResource = v08GResource;

    this.queryParameters =
        Lists.transform(input.queryParameters(), new Function<Parameter, GParameter>() {

          @Nullable
          @Override
          public GParameter apply(@Nullable Parameter input) {
            return new V08GParameter(input);
          }
        });

    this.headers =
        Lists.transform(input.headers(), new Function<Parameter, GParameter>() {

          @Nullable
          @Override
          public GParameter apply(@Nullable Parameter input) {
            return new V08GParameter(input);
          }
        });

    this.requests = Lists.transform(input.body(), new Function<BodyLike, GRequest>() {

      @Nullable
      @Override
      public GRequest apply(@Nullable BodyLike input) {

        return new V08GRequest(V08Method.this.v08GResource, V08Method.this, input, globalSchemas,
                               registry);
      }
    });

    this.responses = Lists.transform(input.responses(), new Function<Response, GResponse>() {

      @Nullable
      @Override
      public GResponse apply(@Nullable Response resp) {
        return new V08Response(v08GResource.implementation(), input, resp, globalSchemas, registry);
      }
    });
    this.input = input;
  }

  @Override
  public Method implementation() {
    return input;
  }

  @Override
  public List<GRequest> body() {
    return requests;
  }

  @Override
  public GResource resource() {
    return v08GResource;
  }

  @Override
  public String method() {
    return input.method();
  }

  @Override
  public List<GParameter> queryParameters() {
    return queryParameters;
  }

  @Override
  public List<GResponse> responses() {
    return responses;
  }


  @Override
  public List<GParameter> headers() {
    return headers;
  }

  @Override
  public boolean equals(Object obj) {

    if (obj == null || !(obj instanceof V08Method)) {

      return false;
    }

    V08Method method = (V08Method) obj;
    return method.v08GResource.resourcePath().equals(v08GResource.resourcePath())
        && method.method().equals(method());
  }

  @Override
  public int hashCode() {

    return method().hashCode() + v08GResource.resourcePath().hashCode();
  }

}
