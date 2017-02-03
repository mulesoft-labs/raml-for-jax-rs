/*
 * Copyright ${licenseYear} (c) MuleSoft, Inc.
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
package org.raml.jaxrs.cli;

import org.raml.jaxrs.converter.RamlConfiguration;
import org.raml.jaxrs.raml.core.DefaultRamlConfiguration;
import org.raml.jaxrs.raml.core.OneStopShop;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

  public static void main(String[] args) throws Exception {
    Path jaxRsResourceFile = Paths.get(args[0]);
    Path ramlOutputFile = Paths.get(args[1]);

    Path jaxRsSourceRoot = null;
    if (args.length > 2) {
      jaxRsSourceRoot = Paths.get(args[2]);
    }

    RamlConfiguration ramlConfiguration =
        DefaultRamlConfiguration.forApplication(jaxRsResourceFile.getFileName().toString());

    OneStopShop.Builder builder =
        OneStopShop.builder().withJaxRsClassesRoot(jaxRsResourceFile)
            .withRamlOutputFile(ramlOutputFile).withRamlConfiguration(ramlConfiguration);

    if (null != jaxRsSourceRoot) {
      builder.withSourceCodeRoot(jaxRsSourceRoot);
    }

    OneStopShop oneStopShop = builder.build();

    oneStopShop.parseJaxRsAndOutputRaml();
  }
}
