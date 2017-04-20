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

import org.glassfish.jersey.server.model.Parameter;
import org.raml.jaxrs.model.JaxRsEntity;
import org.raml.jaxrs.model.JaxRsMultiPartFormDataParameter;
import org.raml.jaxrs.parser.source.SourceParser;

/**
 * Created by Jean-Philippe Belanger on 4/8/17. Just potential zeroes and ones
 */
public class JerseyJaxRsMultiPartFormDataParameter implements JaxRsMultiPartFormDataParameter {


  private final Parameter parameter;
  private final SourceParser sourceParser;

  public JerseyJaxRsMultiPartFormDataParameter(Parameter input, SourceParser sourceParser) {
    this.parameter = input;
    this.sourceParser = sourceParser;
  }

  @Override
  public String getName() {
    return parameter.getSourceName();
  }

  @Override
  public JaxRsEntity getPartEntity() {
    return JerseyJaxRsEntity.create(parameter, sourceParser);
  }

  public static JaxRsMultiPartFormDataParameter create(Parameter input, SourceParser sourceParser) {
    return new JerseyJaxRsMultiPartFormDataParameter(input, sourceParser);
  }
}
