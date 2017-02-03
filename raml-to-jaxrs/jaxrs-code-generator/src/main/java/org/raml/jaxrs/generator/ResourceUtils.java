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
package org.raml.jaxrs.generator;

import com.google.common.collect.Multimap;
import org.raml.jaxrs.generator.ramltypes.GMethod;
import org.raml.jaxrs.generator.ramltypes.GRequest;
import org.raml.jaxrs.generator.ramltypes.GResource;
import org.raml.jaxrs.generator.ramltypes.GResponse;

/**
 * Created by Jean-Philippe Belanger on 12/4/16. Just potential zeroes and ones
 */
public class ResourceUtils {

  public static void fillInBodiesAndResponses(GResource resource,
                                              Multimap<GMethod, GRequest> incomingBodies, Multimap<GMethod, GResponse> responses) {


    for (GMethod method : resource.methods()) {

      if (method.body().size() == 0) {
        incomingBodies.put(method, null);
      } else {
        for (GRequest typeDeclaration : method.body()) {

          incomingBodies.put(method, typeDeclaration);
        }
      }

      if (method.responses().size() == 0) {
        incomingBodies.put(method, null);
      } else {
        for (GResponse response : method.responses()) {

          responses.put(method, response);
        }
      }
    }

  }
}
