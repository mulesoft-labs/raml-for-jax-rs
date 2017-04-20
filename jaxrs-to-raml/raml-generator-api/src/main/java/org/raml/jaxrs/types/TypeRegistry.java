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
package org.raml.jaxrs.types;

import org.raml.api.RamlEntity;
import org.raml.jaxrs.emitters.AnnotationInstanceEmitter;
import org.raml.jaxrs.plugins.TypeScanner;
import org.raml.utilities.IndentedAppendable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 3/26/17. Just potential zeroes and ones
 */
public class TypeRegistry {

  private Map<String, RamlType> types = new HashMap<>();

  public RamlType registerType(String name, RamlEntity type, TypeScanner scanner) {

    if (types.containsKey(name)) {
      return types.get(name);
    } else {

      RamlType ramlType = new RamlType(type);
      types.put(name, ramlType);

      scanner.scanType(this, type, ramlType);
      return ramlType;
    }
  }

  public void writeAll(AnnotationInstanceEmitter annotationInstanceEmitter, IndentedAppendable writer) throws IOException {
    for (RamlType ramlType : types.values()) {

      ramlType.write(annotationInstanceEmitter, writer);
    }
  }
}
