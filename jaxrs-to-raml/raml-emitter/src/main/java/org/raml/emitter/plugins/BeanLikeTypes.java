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

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.raml.api.RamlMediaType;
import org.raml.api.RamlResourceMethod;
import org.raml.emitter.types.RamlProperty;
import org.raml.emitter.types.RamlType;
import org.raml.emitter.types.TypeRegistry;
import org.raml.jaxrs.common.BuildType;
import org.raml.utilities.IndentedAppendable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

/**
 * Created by Jean-Philippe Belanger on 3/26/17. Just potential zeroes and ones
 */
public class BeanLikeTypes implements TypeHandler {

  @Override
  public void writeType(TypeRegistry registry, IndentedAppendable writer, RamlMediaType ramlMediaType,
                        RamlResourceMethod method, Type type)
      throws IOException {

    List<RamlMediaType> mediaTypes = method.getConsumedMediaTypes();

    writeBody(registry, writer, mediaTypes, type);
  }

  @Override
  public boolean handlesType(RamlResourceMethod method, Type type) {

    Class<?> c = (Class) type;
    BuildType t = c.getAnnotation(BuildType.class);
    if (t != null) {
      return t.value().equals("ramlforjaxrs-simple");
    }

    return false;
  }

  private void writeBody(final TypeRegistry registry, IndentedAppendable writer,
                         List<RamlMediaType> mediaTypes, final Type bodyType)
      throws IOException {

    // find top interface.
    final Class topInterface = (Class) bodyType;

    // find fields

    writer.indent();
    writer.appendLine("type: " + topInterface.getSimpleName());
    writer.outdent();

    TypeScanner scanner = new TypeScanner() {

      @Override
      public void scanType(TypeRegistry typeRegistry, Type typeClass, RamlType ramlType) {

        // rebuild types
        rebuildType(typeRegistry, (Class) typeClass, this);
      }
    };

    scanner.scanType(registry, topInterface, null);
  }

  private RamlType rebuildType(TypeRegistry registry, Class currentInterface,
                               TypeScanner typeScanner) {

    Class[] interfaces = currentInterface.getInterfaces();
    List<RamlType> superTypes = new ArrayList<>();
    for (Class interf : interfaces) {
      superTypes.add(rebuildType(registry, interf, typeScanner));
    }

    Method[] methods = currentInterface.getDeclaredMethods();
    RamlType rt = registry.registerType(currentInterface.getSimpleName(), currentInterface, typeScanner);
    rt.setSuperTypes(superTypes);
    for (Method method : methods) {

      if (method.getName().startsWith("get")) {

        String badlyCasedfieldName = method.getName().substring(3);
        String fieldName = Character.toLowerCase(badlyCasedfieldName.charAt(0)) + badlyCasedfieldName.substring(1);
        rt.addProperty(RamlProperty.createProperty(fieldName, PluginUtilities.getRamlType(method.getReturnType().getSimpleName(),
                                                                                          registry,
                                                                                          method.getGenericReturnType(),
                                                                                          typeScanner)));
      }
    }

    return rt;
  }
}
