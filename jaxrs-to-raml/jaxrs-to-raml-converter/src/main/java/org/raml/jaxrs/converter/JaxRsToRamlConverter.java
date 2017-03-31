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
package org.raml.jaxrs.converter;

import org.raml.jaxrs.converter.model.JaxRsRamlApi;
import org.raml.jaxrs.model.JaxRsApplication;
import org.raml.jaxrs.model.JaxRsResource;
import org.raml.api.RamlApi;
import org.raml.utilities.IndentedAppendable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JaxRsToRamlConverter {

  private final static Logger logger = LoggerFactory.getLogger(JaxRsToRamlConverter.class);

  private JaxRsToRamlConverter() {}

  public static JaxRsToRamlConverter create() {
    return new JaxRsToRamlConverter();
  }

  public RamlApi convert(RamlConfiguration configuration, JaxRsApplication application)
      throws JaxRsToRamlConversionException {

    if (logger.isDebugEnabled()) {
      logger.debug("converting application: \n{}", jaxRsApplicationPrettyString(application));
    }

    return JaxRsRamlApi.create(configuration, application);
  }

  private static String jaxRsApplicationPrettyString(JaxRsApplication application) {
    StringBuilder builder = new StringBuilder();

    try {
      appendApplication(IndentedAppendable.forNoSpaces(2, builder), application);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return builder.toString();
  }

  private static IndentedAppendable appendApplication(IndentedAppendable appendable,
                                                      JaxRsApplication application) throws IOException {
    appendable.appendLine("JaxRsApplication {");

    appendable.indent();
    for (JaxRsResource resource : application.getResources()) {
      appendResource(appendable, resource);
    }
    appendable.outdent();
    appendable.withIndent().appendLine("}");
    return appendable;
  }

  private static IndentedAppendable appendResource(IndentedAppendable appendable,
                                                   JaxRsResource resource) throws IOException {
    appendable.appendLine("Resource {");
    appendable.indent();
    appendable.appendLine("path: " + resource.getPath().getStringRepresentation());
    appendable.outdent();
    appendable.appendLine("}");

    return appendable;
  }

}
