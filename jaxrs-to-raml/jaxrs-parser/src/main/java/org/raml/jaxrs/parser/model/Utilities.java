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
package org.raml.jaxrs.parser.model;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.raml.jaxrs.model.JaxRsEntity;
import org.raml.jaxrs.model.JaxRsFormParameter;
import org.raml.jaxrs.model.JaxRsHeaderParameter;
import org.raml.jaxrs.model.JaxRsMultiPartFormDataParameter;
import org.raml.jaxrs.model.JaxRsQueryParameter;
import org.raml.jaxrs.model.JaxRsSupportedAnnotation;
import org.raml.jaxrs.parser.source.SourceParser;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

class Utilities {

  private static final Predicate<Parameter> IS_QUERY_PARAMETER_PREDICATE =
      new Predicate<Parameter>() {

        @Override
        public boolean apply(@Nullable Parameter parameter) {
          return parameter.getSource() == Parameter.Source.QUERY;
        }
      };

  private static final Predicate<Parameter> IS_FORM_PARAMETER_PREDICATE =
      new Predicate<Parameter>() {

        @Override
        public boolean apply(@Nullable Parameter parameter) {
          return parameter.getSource() == Parameter.Source.FORM;
        }
      };

  private static final Predicate<Parameter> IS_CONSUMED_PARAMETER_PREDICATE =
      new Predicate<Parameter>() {

        @Override
        public boolean apply(@Nullable Parameter parameter) {
          return parameter.getSource() == Parameter.Source.ENTITY;
        }
      };

  public static final Predicate<Parameter> IS_HEADER_PARAMETER_PREDICATE =
      new Predicate<Parameter>() {

        @Override
        public boolean apply(@Nullable Parameter parameter) {
          return parameter.getSource() == Parameter.Source.HEADER;
        }
      };

  private Utilities() {}

  public static FluentIterable<Parameter> getMultiPartFormDataParameter(
                                                                        ResourceMethod resourceMethod) {
    return FluentIterable.from(resourceMethod.getInvocable().getParameters()).filter(new Predicate<Parameter>() {

      @Override
      public boolean apply(@Nullable Parameter input) {

        return input.isAnnotationPresent(FormDataParam.class) && input.getRawType() != FormDataContentDisposition.class;
      }
    });
  }

  public static FluentIterable<Parameter> getQueryParameters(ResourceMethod resourceMethod) {
    return FluentIterable.from(resourceMethod.getInvocable().getParameters()).filter(
                                                                                     isQueryParameterPredicate());
  }

  public static FluentIterable<Parameter> getConsumedParameter(ResourceMethod resourceMethod) {
    return FluentIterable.from(resourceMethod.getInvocable().getParameters()).filter(
                                                                                     isConsumedParameterPredicate());
  }

  public static FluentIterable<Parameter> getFormParameters(ResourceMethod resourceMethod) {

    return FluentIterable.from(resourceMethod.getInvocable().getParameters()).filter(
                                                                                     isFormParameterPredicate());
  }

  public static FluentIterable<JaxRsQueryParameter> toJaxRsQueryParameters(
                                                                           Iterable<Parameter> parameters,
                                                                           final SourceParser sourceParser) {
    return FluentIterable.from(parameters).transform(
                                                     new Function<Parameter, JaxRsQueryParameter>() {

                                                       @Nullable
                                                       @Override
                                                       public JaxRsQueryParameter apply(@Nullable Parameter parameter) {
                                                         return JerseyJaxRsQueryParameter.create(parameter, sourceParser);
                                                       }
                                                     });
  }

  public static Predicate<Parameter> isQueryParameterPredicate() {
    return IS_QUERY_PARAMETER_PREDICATE;
  }

  public static Predicate<Parameter> isFormParameterPredicate() {
    return IS_FORM_PARAMETER_PREDICATE;
  }

  public static Predicate<Parameter> isConsumedParameterPredicate() {
    return IS_CONSUMED_PARAMETER_PREDICATE;
  }


  public static FluentIterable<Parameter> getHeaderParameters(ResourceMethod resourceMethod) {
    return FluentIterable.from(resourceMethod.getInvocable().getParameters()).filter(
                                                                                     isHeaderParameterPredicate());
  }

  public static Predicate<Parameter> isHeaderParameterPredicate() {
    return IS_HEADER_PARAMETER_PREDICATE;
  }

  public static FluentIterable<JaxRsHeaderParameter> toJaxRsHeaderParameters(
                                                                             Iterable<Parameter> headerParameters,
                                                                             final SourceParser sourceParser) {
    return FluentIterable.from(headerParameters).transform(
                                                           new Function<Parameter, JaxRsHeaderParameter>() {

                                                             @Nullable
                                                             @Override
                                                             public JaxRsHeaderParameter apply(@Nullable Parameter parameter) {
                                                               return JerseyJaxRsHeaderParameter.create(parameter, sourceParser);
                                                             }
                                                           });
  }

  public static Optional<JaxRsEntity> toJaxRsEntityParameters(Iterable<Parameter> consumedParameter, final SourceParser parser) {

    return FluentIterable.from(consumedParameter).transform(new Function<Parameter, JaxRsEntity>() {

      @Nullable
      @Override
      public JaxRsEntity apply(@Nullable Parameter input) {
        return JerseyJaxRsEntity.create(input, parser);
      }
    }).first();
  }

  public static Optional<JaxRsEntity> getReturnValue(ResourceMethod resourceMethod, SourceParser sourceParser) {

    return JerseyJaxRsEntity.create(resourceMethod.getInvocable().getResponseType(), sourceParser);
  }

  public static FluentIterable<JaxRsFormParameter> toJaxRsFormParameters(Iterable<Parameter> formParameters) {
    return FluentIterable.from(formParameters).transform(new Function<Parameter, JaxRsFormParameter>() {

      @Nullable
      @Override
      public JaxRsFormParameter apply(@Nullable Parameter input) {
        return JerseyJaxRsFormParameter.create(input);
      }
    });
  }

  public static FluentIterable<JaxRsMultiPartFormDataParameter> toJaxRsMultiPartFormDataParameter(
                                                                                                  Iterable<Parameter> multiPartFormDataParameter,
                                                                                                  final SourceParser sourceParser) {
    return FluentIterable.from(multiPartFormDataParameter).transform(
                                                                     new Function<Parameter, JaxRsMultiPartFormDataParameter>() {

                                                                       @Nullable
                                                                       @Override
                                                                       public JaxRsMultiPartFormDataParameter apply(@Nullable Parameter input) {
                                                                         return JerseyJaxRsMultiPartFormDataParameter
                                                                             .create(input, sourceParser);
                                                                       }
                                                                     });
  }
}
