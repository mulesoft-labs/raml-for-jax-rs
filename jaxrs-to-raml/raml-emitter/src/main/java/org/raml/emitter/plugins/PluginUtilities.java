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
package org.raml.emitter.plugins;

import org.raml.api.ScalarType;
import org.raml.emitter.types.RamlType;
import org.raml.emitter.types.TypeRegistry;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Created by Jean-Philippe Belanger on 3/26/17. Just potential zeroes and ones
 */
public class PluginUtilities {

  static RamlType getRamlType(String simpleName, TypeRegistry typeRegistry, Type genericType, TypeScanner scanner) {
    RamlType fieldRamlType;
    if (ScalarType.fromType(genericType).isPresent()) {
      // scalars
      return new RamlType(genericType);
    }

    if (genericType instanceof ParameterizedType) {

      ParameterizedType ptype = (ParameterizedType) genericType;

      if (Collection.class.isAssignableFrom((Class<?>) ptype.getRawType())) {
        RamlType collectionType =
            getRamlType(((Class) ptype.getActualTypeArguments()[0]).getSimpleName(), typeRegistry,
                        ptype.getActualTypeArguments()[0], scanner);
        return RamlType.collectionOf(collectionType);
      }
    }

    fieldRamlType = typeRegistry.registerType(simpleName, genericType, scanner);
    return fieldRamlType;
  }
}
