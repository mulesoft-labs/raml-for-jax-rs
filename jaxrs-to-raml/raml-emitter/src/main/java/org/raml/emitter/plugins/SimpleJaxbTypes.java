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
import org.raml.api.RamlEntity;
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
import java.lang.reflect.Type;
import java.util.List;

import static java.lang.String.format;

/**
 * Created by Jean-Philippe Belanger on 3/26/17. Just potential zeroes and ones
 */
public class SimpleJaxbTypes implements TypeHandler {

  @Override
  public void writeType(TypeRegistry registry, IndentedAppendable writer, RamlMediaType ramlMediaType,
                        RamlResourceMethod method, RamlEntity type)
      throws IOException {

    List<RamlMediaType> mediaTypes = method.getConsumedMediaTypes();

    writeBody(registry, writer, mediaTypes, type);
  }

  @Override
  public boolean handlesType(RamlResourceMethod method, Type type) {

    Class<?> c = (Class<?>) type;
    BuildType buildType = c.getAnnotation(BuildType.class);
    if (buildType != null && buildType.value().equals("jaxb-annotations")) {
      return true;
    }

    return c.getAnnotation(XmlRootElement.class) != null
        && FluentIterable.of(c.getDeclaredFields()).anyMatch(new Predicate<Field>() {

          @Override
          public boolean apply(Field input) {
            return input.isAnnotationPresent(XmlAttribute.class)
                || input.isAnnotationPresent(XmlElement.class);
          }
        });
  }

  private void writeBody(TypeRegistry registry, IndentedAppendable writer,
                         List<RamlMediaType> mediaTypes, RamlEntity bodyType)
      throws IOException {

    Class type = (Class) bodyType.getType();

    for (RamlMediaType mediaType : mediaTypes) {
      writer.appendLine(format("%s:", mediaType.toStringRepresentation()));

      writer.indent();
      writer.appendLine("type: " + type.getSimpleName());
      writer.outdent();

      registry.registerType(type.getSimpleName(), bodyType, new TypeScanner() {

        @Override
        public void scanType(TypeRegistry typeRegistry, RamlEntity type, RamlType ramlType) {

          Class c = (Class) type.getType();
          for (Field field : c.getDeclaredFields()) {

            Type genericType = field.getGenericType();
            RamlType fieldRamlType;
            fieldRamlType = PluginUtilities.getRamlType(c.getSimpleName(), typeRegistry, type.createDependent(genericType), this);

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
      });
    }

    writer.outdent();
  }
}
