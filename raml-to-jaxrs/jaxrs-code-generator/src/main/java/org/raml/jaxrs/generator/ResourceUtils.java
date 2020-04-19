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

import amf.client.model.domain.*;
import com.google.common.collect.Multimap;

import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 12/4/16. Just potential zeroes and ones
 */
public class ResourceUtils {

  public static void fillInBodiesAndResponses(EndPoint resource,
                                              Multimap<Operation, Payload> incomingBodies, Multimap<Operation, Response> responses) {


    for (Operation method : resource.operations()) {

      if (method.request().payloads().size() == 0) {
        incomingBodies.put(method, null);
      } else {
        for (Payload payload : method.request().payloads()) {

          incomingBodies.put(method, payload);
        }
      }

      if (method.responses().size() == 0) {
        incomingBodies.put(method, null);
      } else {
        for (Response response : method.responses()) {

          responses.put(method, response);
        }
      }
    }

  }

  public static List<Parameter> accumulateUriParameters(EndPoint resource) {

    return resource.parameters();
  }


}
