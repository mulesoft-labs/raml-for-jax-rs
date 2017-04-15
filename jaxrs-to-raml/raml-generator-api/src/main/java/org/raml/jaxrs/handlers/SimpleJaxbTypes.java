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

import com.google.common.base.Optional;
import org.raml.api.RamlEntity;
import org.raml.api.RamlMediaType;
import org.raml.api.RamlResourceMethod;
import org.raml.api.Annotable;
import org.raml.jaxrs.types.RamlProperty;
import org.raml.jaxrs.types.RamlType;
import org.raml.jaxrs.types.TypeRegistry;
import org.raml.jaxrs.plugins.TypeHandler;
import org.raml.jaxrs.plugins.TypeScanner;
import org.raml.utilities.IndentedAppendable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Created by Jean-Philippe Belanger on 3/26/17. Just potential zeroes and ones
 */
public class SimpleJaxbTypes implements TypeHandler {

  @Override
  public void writeType(TypeRegistry registry, IndentedAppendable writer, RamlMediaType ramlMediaType,
                        RamlResourceMethod method, RamlEntity type)
      throws IOException {


    writeBody(registry, writer, ramlMediaType, type);
  }

  private void writeBody(TypeRegistry registry, IndentedAppendable writer,
                         RamlMediaType mediaTypes, RamlEntity bodyType)
      throws IOException {

    Class type = (Class) bodyType.getType();

    writer.appendLine("type", type.getSimpleName());

    registry.registerType(type.getSimpleName(), bodyType, new SimpleJaxbTypeScanner());
  }

  private static class SimpleJaxbTypeScanner implements TypeScanner {

    @Override
    public void scanType(TypeRegistry typeRegistry, RamlEntity type, RamlType ramlType) {

      Class c = (Class) type.getType();
      for (Field field : c.getDeclaredFields()) {

        Type genericType = field.getGenericType();
        RamlType fieldRamlType;
        fieldRamlType = PluginUtilities
            .getRamlType(typeRegistry, this, c.getSimpleName(), type.createDependent(genericType));

        XmlElement elem = field.getAnnotation(XmlElement.class);
        if (elem != null) {

          String name = elem.name().equals("##default") ? field.getName() : elem.name();
          ramlType.addProperty(RamlProperty.createProperty(new FieldAnnotable(field), name, fieldRamlType));
        } else {

          XmlAttribute attribute = field.getAnnotation(XmlAttribute.class);
          if (attribute != null) {

            String name = elem.name().equals("##default") ? field.getName() : elem.name();
            ramlType.addProperty(RamlProperty.createProperty(new FieldAnnotable(field), name, fieldRamlType));
          }
        }
      }
    }

    private static class FieldAnnotable implements Annotable {

      private final Field field;

      public FieldAnnotable(Field field) {
        this.field = field;
      }

      @Override
      public <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationType) {
        return Optional.fromNullable(field.getAnnotation(annotationType));
      }
    }
  }
}
