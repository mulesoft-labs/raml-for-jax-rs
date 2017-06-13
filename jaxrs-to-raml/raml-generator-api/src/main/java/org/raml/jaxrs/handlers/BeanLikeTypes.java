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
import org.raml.jaxrs.types.RamlProperty;
import org.raml.jaxrs.types.RamlType;
import org.raml.jaxrs.types.TypeRegistry;
import org.raml.jaxrs.plugins.TypeHandler;
import org.raml.jaxrs.plugins.TypeScanner;
import org.raml.utilities.IndentedAppendable;
import org.raml.utilities.types.Cast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Jean-Philippe Belanger on 3/26/17. Just potential zeroes and ones
 */
public class BeanLikeTypes implements TypeHandler {

  @Override
  public void writeType(TypeRegistry registry, IndentedAppendable writer,
                        RamlEntity type)
      throws IOException {

    writeBody(registry, writer, type);
  }

  private void writeBody(final TypeRegistry registry, IndentedAppendable writer,
                         final RamlEntity bodyType)
      throws IOException {

    // find top interface.
    final Class topInterface = Cast.toClass(bodyType.getType());

    // find fields
    writer.appendLine("type", topInterface.getSimpleName());

    TypeScanner scanner = new TypeScanner() {

      @Override
      public void scanType(TypeRegistry typeRegistry, RamlEntity typeClass, RamlType ramlType) {

        // rebuild types
        rebuildType(typeRegistry, typeClass, this);
      }
    };

    scanner.scanType(registry, bodyType, null);
  }

  private RamlType rebuildType(TypeRegistry registry, RamlEntity entity,
                               TypeScanner typeScanner) {

    Class currentInterface = Cast.toClass(entity.getType());
    Class[] interfaces = currentInterface.getInterfaces();
    List<RamlType> superTypes = new ArrayList<>();
    for (Class interf : interfaces) {
      superTypes.add(rebuildType(registry, entity.createDependent(interf), typeScanner));
    }

    Method[] methods = currentInterface.getDeclaredMethods();
    RamlType rt = registry.registerType(currentInterface.getSimpleName(), entity, typeScanner);
    rt.setSuperTypes(superTypes);
    for (Method method : methods) {

      if (method.getName().startsWith("get")) {

        String badlyCasedfieldName = method.getName().substring(3);
        String fieldName = Character.toLowerCase(badlyCasedfieldName.charAt(0)) + badlyCasedfieldName.substring(1);
        rt.addProperty(RamlProperty.createProperty(
                                                   new MethodAnnotable(method), fieldName,
                                                   PluginUtilities.getRamlType(registry, typeScanner, method.getReturnType()
                                                       .getSimpleName(),
                                                                               entity.createDependent(method
                                                                                   .getGenericReturnType())
                                                       )));
      }
    }

    return rt;
  }

}
