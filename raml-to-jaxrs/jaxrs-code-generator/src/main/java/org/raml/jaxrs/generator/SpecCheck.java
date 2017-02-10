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

import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.internal.impl.RamlBuilder;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;

import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 12/7/16. Just potential zeroes and ones
 */
public class SpecCheck {

  public static void main(String[] args) {
    RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(args[0]);
    if (ramlModelResult.hasErrors()) {
      for (ValidationResult validationResult : ramlModelResult.getValidationResults()) {
        System.err.println(validationResult.toString());
      }
    } else {
      Api api = ramlModelResult.getApiV10();
      if (api != null) {
        System.err.println("spec parsed");
      } else {

        System.err.println("spec did not parse");
      }
    }
  }
}
