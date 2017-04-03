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
package org.raml.jaxrs.examples;

import org.junit.Ignore;
import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;

import java.io.File;
import java.net.URISyntaxException;

import static junit.framework.TestCase.fail;

/**
 * Created by Jean-Philippe Belanger on 3/28/17. Just potential zeroes and ones
 */
@Ignore
public class JustLoadTest {

  @Test
  public void justLoad() throws URISyntaxException {
    /*
     * RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(new
     * File(JustLoadTest.class.getResource("/jaxrs-test-resources.raml").toURI()));
     */
    RamlModelResult ramlModelResult =
        new RamlModelBuilder().buildApi(new File("target/generated-sources/raml-jaxrs/jaxrs-test-resources.raml"));
    if (ramlModelResult.hasErrors())
    {
      for (ValidationResult validationResult : ramlModelResult.getValidationResults())
      {
        System.out.println(validationResult.getMessage());
      }

      fail("Cant load");
    }
  }
}
