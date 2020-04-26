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
package org.raml.jaxrs.generator.utils;

import amf.client.model.document.Document;
import amf.client.model.domain.EndPoint;
import amf.client.model.domain.WebApi;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.resources.ResourceBuilder;
import org.raml.jaxrs.generator.v10.ExtensionManager;
import org.raml.jaxrs.generator.v10.V10Finder;
import org.raml.ramltopojo.RamlLoader;
import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 12/25/16. Just potential zeroes and ones
 */
public class RamlV10 {

  public static Document buildApiV10(Object test, String raml) {

    return RamlLoader.load(test.getClass().getResource(raml).toString());
  }

  public static void buildResourceV10(Object test, String raml, CodeContainer<TypeSpec> container,
                                      String name, String uri) throws IOException {

    Document api = buildApiV10(test, raml);
    WebApi webApi = (WebApi) api.encodes();
    EndPoint endPoint = webApi.endPoints().get(0);
    CurrentBuild currentBuild =
        new CurrentBuild(api, ExtensionManager.createExtensionManager());

    currentBuild.constructClasses(new V10Finder(null, api, webApi));
    ResourceBuilder builder =
        new ResourceBuilder(currentBuild, endPoint, name, uri);
    builder.output(container);
  }

}
