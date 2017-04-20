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
package org.raml.jaxrs.converter.model;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import org.raml.api.RamlFormParameter;
import org.raml.api.RamlHeaderParameter;
import org.raml.api.RamlMultiFormDataParameter;
import org.raml.api.RamlSupportedAnnotation;
import org.raml.api.ScalarType;
import org.raml.jaxrs.model.JaxRsFormParameter;
import org.raml.jaxrs.model.JaxRsHeaderParameter;
import org.raml.jaxrs.model.JaxRsMultiPartFormDataParameter;
import org.raml.jaxrs.model.JaxRsResource;
import org.raml.jaxrs.model.JaxRsMethod;
import org.raml.jaxrs.model.JaxRsQueryParameter;
import org.raml.api.RamlMediaType;
import org.raml.api.RamlQueryParameter;
import org.raml.api.RamlResource;
import org.raml.api.RamlResourceMethod;
import org.raml.jaxrs.model.JaxRsSupportedAnnotation;

import java.util.List;

public class Utilities {

  public static FluentIterable<RamlMediaType> toRamlMediaTypes(
                                                               Iterable<javax.ws.rs.core.MediaType> mediaTypes) {
    return FluentIterable.from(mediaTypes).transform(
                                                     new Function<javax.ws.rs.core.MediaType, RamlMediaType>() {

                                                       @Override
                                                       public RamlMediaType apply(javax.ws.rs.core.MediaType mediaType) {
                                                         return JaxRsRamlMediaType.create(mediaType);
                                                       }
                                                     });
  }

  public static FluentIterable<RamlResource> toRamlResources(Iterable<JaxRsResource> jaxRsResources) {
    return FluentIterable.from(jaxRsResources).transform(
                                                         new Function<JaxRsResource, RamlResource>() {

                                                           @Override
                                                           public RamlResource apply(JaxRsResource jaxRsResource) {
                                                             return JaxRsRamlResource.create(jaxRsResource);
                                                           }
                                                         });
  }

  public static FluentIterable<RamlSupportedAnnotation> toRamlSupportedAnnotation(Iterable<JaxRsSupportedAnnotation> jaxRsResources) {
    return FluentIterable.from(jaxRsResources).transform(
                                                         new Function<JaxRsSupportedAnnotation, RamlSupportedAnnotation>() {

                                                           @Override
                                                           public RamlSupportedAnnotation apply(JaxRsSupportedAnnotation annotation) {
                                                             return new JaxRsRamlSupportedAnnotation(annotation
                                                                 .getJavaAnnotation());
                                                           }
                                                         });
  }

  public static FluentIterable<RamlResourceMethod> toRamlMethods(Iterable<JaxRsMethod> methods) {
    return FluentIterable.from(methods).transform(new Function<JaxRsMethod, RamlResourceMethod>() {

      @Override
      public RamlResourceMethod apply(JaxRsMethod method) {
        return JaxRsRamlMethod.create(method);
      }
    });
  }

  public static FluentIterable<RamlQueryParameter> toRamlQueryParameters(
                                                                         Iterable<JaxRsQueryParameter> queryParameters) {
    return FluentIterable.from(queryParameters).transform(
                                                          new Function<JaxRsQueryParameter, RamlQueryParameter>() {

                                                            @Override
                                                            public RamlQueryParameter apply(JaxRsQueryParameter queryParameter) {
                                                              return JaxRsRamlQueryParameter.create(queryParameter);
                                                            }
                                                          });
  }

  public static FluentIterable<RamlHeaderParameter> toRamlHeaderParameters(
                                                                           List<JaxRsHeaderParameter> headerParameters) {
    return FluentIterable.from(headerParameters).transform(
                                                           new Function<JaxRsHeaderParameter, RamlHeaderParameter>() {

                                                             @Override
                                                             public RamlHeaderParameter apply(JaxRsHeaderParameter jaxRsHeaderParameter) {
                                                               return JaxRsRamlHeaderParameter.create(jaxRsHeaderParameter);
                                                             }
                                                           }

        );
  }

  public static FluentIterable<RamlFormParameter> toRamlFormParameters(List<JaxRsFormParameter> formParameters) {
    return FluentIterable.from(formParameters).transform(
                                                         new Function<JaxRsFormParameter, RamlFormParameter>() {

                                                           @Override
                                                           public RamlFormParameter apply(JaxRsFormParameter jaxRsFormParameter) {
                                                             return JaxRsRamlFormParameter.create(jaxRsFormParameter);
                                                           }
                                                         }

        );
  }

  public static FluentIterable<RamlMultiFormDataParameter> toRamlMultiPartFormDataParameters(
                                                                                             List<JaxRsMultiPartFormDataParameter> multiPartFormDataParameters) {
    return FluentIterable.from(multiPartFormDataParameters)
        .transform(
                   new Function<JaxRsMultiPartFormDataParameter, RamlMultiFormDataParameter>() {

                     @Override
                     public RamlMultiFormDataParameter apply(JaxRsMultiPartFormDataParameter input) {
                       return JaxRsRamlMultiFormDataParameter.create(input);
                     }
                   }
        );
  }
}
