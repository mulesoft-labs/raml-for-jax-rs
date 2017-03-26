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
import org.raml.api.ScalarType;
import org.raml.emitter.types.RamlProperty;
import org.raml.emitter.types.RamlType;
import org.raml.emitter.types.TypeRegistry;
import org.raml.utilities.IndentedAppendable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import static java.lang.String.format;

/**
 * Created by Jean-Philippe Belanger on 3/26/17. Just potential zeroes and ones
 */
public class JaxbForRamlToJaxrs implements TypeHandler {

  @Override
  public void writeType(TypeRegistry registry, IndentedAppendable writer, RamlMediaType ramlMediaType,
                        RamlResourceMethod method, Type type)
      throws IOException {

    List<RamlMediaType> mediaTypes = method.getConsumedMediaTypes();

    writeBody(registry, writer, mediaTypes, type);
  }

  @Override
  public int handlesType(RamlResourceMethod method, Type type) {

    List<RamlMediaType> consumedMediaTypes = method.getConsumedMediaTypes();

    return handles(type, consumedMediaTypes);
  }

  private int handles(Type type, List<RamlMediaType> mediaTypes) {
    boolean mediaTypeMatches = FluentIterable.from(mediaTypes).anyMatch(new Predicate<RamlMediaType>() {

      @Override
      public boolean apply(RamlMediaType input) {
        return input.toStringRepresentation().startsWith("application/xml");
      }
    });

    if (mediaTypeMatches && type instanceof Class && hasXmlAnnotation((Class) type)) {
      return 100;
    } else {

      return -1;
    }
  }


  private boolean hasXmlAnnotation(Class type) {

    return type.getAnnotation(XmlRootElement.class) != null;
  }


  private void writeBody(TypeRegistry registry, IndentedAppendable writer,
                         List<RamlMediaType> mediaTypes, Type bodyType)
      throws IOException {

    Class type = (Class) bodyType;

    for (RamlMediaType mediaType : mediaTypes) {
      writer.appendLine(format("%s:", mediaType.toStringRepresentation()));

      writer.indent();
      writer.appendLine("type: " + type.getSimpleName());
      writer.outdent();

      registry.registerType(type.getSimpleName(), type, new TypeScanner() {

        @Override
        public void scanType(TypeRegistry typeRegistry, Type type, RamlType ramlType) {

          Class c = (Class) type;
          for (Field field : c.getDeclaredFields()) {

            Type genericType = field.getGenericType();
            RamlType fieldRamlType;
            fieldRamlType = getRamlType(c.getSimpleName(), typeRegistry, genericType);

            XmlElement elem = field.getAnnotation(XmlElement.class);
            if (elem != null) {

              String name = elem.name().equals("##default") ? field.getName() : elem.name();
              ramlType.addProperty(RamlProperty.createProperty(name, fieldRamlType));
            } else {

              XmlAttribute attribute = field.getAnnotation(XmlAttribute.class);
              if (attribute != null) {

                String name = elem.name().equals("##default") ? field.getName() : elem.name();
                ramlType.addProperty(RamlProperty.createProperty(name, fieldRamlType));
              }
            }
          }
        }

        private RamlType getRamlType(String simpleName, TypeRegistry typeRegistry, Type genericType) {
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
                              ptype.getActualTypeArguments()[0]);
              return RamlType.collectionOf(collectionType);
            }
          }

          fieldRamlType = typeRegistry.registerType(simpleName, genericType, this);
          return fieldRamlType;
        }
      });
    }

    writer.outdent();
  }
}
