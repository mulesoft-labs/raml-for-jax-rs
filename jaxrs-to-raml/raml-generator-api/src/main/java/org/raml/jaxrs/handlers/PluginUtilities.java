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
package org.raml.jaxrs.handlers;

import org.raml.api.RamlEntity;
import org.raml.api.ScalarType;
import org.raml.jaxrs.types.RamlType;
import org.raml.jaxrs.types.TypeRegistry;
import org.raml.jaxrs.plugins.TypeScanner;
import org.raml.utilities.types.Cast;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;

/**
 * Created by Jean-Philippe Belanger on 3/26/17. Just potential zeroes and ones
 */
public class PluginUtilities {

  public static RamlType getRamlType(TypeRegistry typeRegistry, TypeScanner scanner, String simpleName,
                                     RamlEntity genericType) {
    RamlType fieldRamlType;
    if (ScalarType.fromType(genericType.getType()).isPresent()) {
      // scalars
      return new RamlType(genericType);
    }

    if (genericType.getType() instanceof ParameterizedType) {

      ParameterizedType ptype = (ParameterizedType) genericType.getType();

      if (Collection.class.isAssignableFrom((Class<?>) ptype.getRawType())) {

        RamlEntity collectionEntityType = genericType.createDependent(ptype.getActualTypeArguments()[0]);
        RamlType collectionType =
            getRamlType(typeRegistry, scanner, Cast.toClass(ptype.getActualTypeArguments()[0]).getSimpleName(),
                        collectionEntityType
            );
        return RamlType.collectionOf(collectionType);
      }
    }

    fieldRamlType = typeRegistry.registerType(simpleName, genericType, scanner);
    return fieldRamlType;
  }
}
