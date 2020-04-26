/*
 * Copyright 2013-2018 (c) MuleSoft, Inc.
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


import amf.client.model.document.Document;
import amf.client.model.domain.EndPoint;
import amf.client.model.domain.WebApi;
import org.raml.jaxrs.generator.v10.ExtensionManager;
import org.raml.jaxrs.generator.v10.ResourceHandler;
import org.raml.ramltopojo.RamlLoader;
import org.raml.ramltopojo.RamlLoaderException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by Jean-Philippe Belanger on 10/26/16. Just potential zeroes and ones
 */
public class RamlScanner {

  public enum Version implements RamlLoader.Loader {
    RAML_08 {

      @Override
      public Document load(String raml) throws RamlLoaderException {
        return RamlLoader.load(raml);
      }
    },
    RAML_10 {

      @Override
      public Document load(String raml) throws RamlLoaderException {
        return RamlLoader.load08(raml);
      }
    }
  }

  private final Configuration configuration;

  public RamlScanner(Configuration configuration) {
    this.configuration = configuration;
  }


  public void handle(File resource, Version version) throws IOException, GenerationException {

    handle(resource.toURI().toString(), version);
  }

  public void handle(String ramlTarget, Version version) throws GenerationException, IOException {

    Document d = version.load(ramlTarget);
    handleRamlFile(d);
  }

  public void handleRamlFile(Document document) throws IOException {

    CurrentBuild build =
        new CurrentBuild(document, ExtensionManager.createExtensionManager());

    configuration.setupBuild(build);
    // build.constructClasses(new V10Finder(build, api));

    ResourceHandler resourceHandler = new ResourceHandler(build);


    // handle resources.
    List<EndPoint> topEndpoints = ((WebApi) document.encodes()).endPoints().stream()
        .filter(e -> !e.parent().isPresent())
        .collect(Collectors.toList());

    for (EndPoint resource : topEndpoints) {
      resourceHandler.handle(resource);
    }

    build.generate(configuration.getOutputDirectory());
  }
}
