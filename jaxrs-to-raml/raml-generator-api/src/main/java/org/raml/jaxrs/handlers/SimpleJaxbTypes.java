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
import org.raml.api.Annotable;
import org.raml.jaxrs.types.RamlProperty;
import org.raml.jaxrs.types.RamlType;
import org.raml.jaxrs.types.TypeRegistry;
import org.raml.jaxrs.plugins.TypeHandler;
import org.raml.jaxrs.plugins.TypeScanner;
import org.raml.utilities.IndentedAppendable;
import org.raml.utilities.types.Cast;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.beans.Introspector;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

/**
 * Created by Jean-Philippe Belanger on 3/26/17. Just potential zeroes and ones
 */
public class SimpleJaxbTypes implements TypeHandler {

  @Override
  public void writeType(TypeRegistry registry, IndentedAppendable writer,
                        RamlEntity type)
      throws IOException {


    writeBody(registry, writer, type);
  }

  private void writeBody(TypeRegistry registry, IndentedAppendable writer,
                         RamlEntity bodyType)
      throws IOException {

    Class type = Cast.toClass(bodyType.getType());

    writer.appendLine("type", type.getSimpleName());

    registry.registerType(type.getSimpleName(), bodyType, new SimpleJaxbTypeScanner());
  }

  private static class SimpleJaxbTypeScanner implements TypeScanner {

    @Override
    public void scanType(TypeRegistry typeRegistry, RamlEntity type, RamlType ramlType) {

      Class c = Cast.toClass(type.getType());
      XmlAccessorType accessorType = (XmlAccessorType) c.getAnnotation(XmlAccessorType.class);
      XmlAccessType accessType = XmlAccessType.PUBLIC_MEMBER;

      if (accessorType != null) {

        accessType = accessorType.value();
      }

      switch (accessType) {
        case NONE:
          // only annotated
          break;
        case FIELD:
          // only non transient fields
          forFields(typeRegistry, type, ramlType, c, false);
          forProperties(typeRegistry, type, ramlType, c, true, false);
          break;
        case PROPERTY:
          // Only properties
          forProperties(typeRegistry, type, ramlType, c, false, false);
          forFields(typeRegistry, type, ramlType, c, true);
          break;
        case PUBLIC_MEMBER:
          forProperties(typeRegistry, type, ramlType, c, false, true);
          forFields(typeRegistry, type, ramlType, c, true);
          break;
      }

    }

    private void forProperties(TypeRegistry typeRegistry, RamlEntity type, RamlType ramlType, Class c, boolean explicitOnly,
                               boolean publicOnly) {
      for (Method method : c.getDeclaredMethods()) {

        if (!(method.getName().startsWith("get") || method.getName().startsWith("is"))) {

          continue;
        }


        if (publicOnly && !Modifier.isPublic(method.getModifiers())) {

          continue;
        }

        if (Modifier.isStatic(method.getModifiers())) {

          continue;
        }

        if (method.isAnnotationPresent(XmlTransient.class)) {

          continue;
        }

        Type genericType = method.getGenericReturnType();
        RamlType fieldRamlType;
        fieldRamlType = PluginUtilities
            .getRamlType(typeRegistry, this, method.getReturnType().getSimpleName(), type.createDependent(genericType));

        if (explicitOnly && (!method.isAnnotationPresent(XmlAttribute.class) && (!method.isAnnotationPresent(XmlElement.class)))) {

          continue;
        }

        XmlElement elem = method.getAnnotation(XmlElement.class);
        if (elem != null) {

          String name = elem.name().equals("##default") ? buildName(method) : elem.name();
          ramlType.addProperty(RamlProperty.createProperty(new MethodAnnotable(method), name, fieldRamlType));
        } else {

          XmlAttribute attribute = method.getAnnotation(XmlAttribute.class);
          if (attribute != null) {

            String name = elem.name().equals("##default") ? buildName(method) : elem.name();
            ramlType.addProperty(RamlProperty.createProperty(new MethodAnnotable(method), name, fieldRamlType));
          } else {

            ramlType
                .addProperty(RamlProperty.createProperty(new MethodAnnotable(method), buildName(method), fieldRamlType));
          }
        }
      }
    }

    private void forFields(TypeRegistry typeRegistry, RamlEntity type, RamlType ramlType, Class c, boolean explicitOnly) {
      for (Field field : c.getDeclaredFields()) {

        if (field.isAnnotationPresent(XmlTransient.class) || Modifier.isTransient(field.getModifiers())) {

          continue;
        }

        if (explicitOnly && (!field.isAnnotationPresent(XmlAttribute.class) && (!field.isAnnotationPresent(XmlElement.class)))) {

          continue;
        }

        Type genericType = field.getGenericType();
        RamlType fieldRamlType;
        fieldRamlType = PluginUtilities
            .getRamlType(typeRegistry, this, field.getType().getSimpleName(), type.createDependent(genericType));

        XmlElement elem = field.getAnnotation(XmlElement.class);
        if (elem != null) {

          String name = elem.name().equals("##default") ? field.getName() : elem.name();
          ramlType.addProperty(RamlProperty.createProperty(new FieldAnnotable(field), name, fieldRamlType));
        } else {

          XmlAttribute attribute = field.getAnnotation(XmlAttribute.class);
          if (attribute != null) {

            String name = elem.name().equals("##default") ? field.getName() : elem.name();
            ramlType.addProperty(RamlProperty.createProperty(new FieldAnnotable(field), name, fieldRamlType));
          } else {

            ramlType
                .addProperty(RamlProperty.createProperty(new FieldAnnotable(field), field.getName(), fieldRamlType));
          }
        }
      }
    }

    private String buildName(Method method) {

      if (method.getName().startsWith("is")) {
        return Introspector.decapitalize(method.getName().substring(2));
      } else {

        return Introspector.decapitalize(method.getName().substring(3));
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
