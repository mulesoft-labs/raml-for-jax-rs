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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.raml.api.RamlEntity;
import org.raml.jaxrs.plugins.TypeHandler;
import org.raml.jaxrs.plugins.TypeScanner;
import org.raml.jaxrs.types.RamlProperty;
import org.raml.jaxrs.types.RamlType;
import org.raml.jaxrs.types.TypeRegistry;
import org.raml.utilities.IndentedAppendable;
import org.raml.utilities.types.Cast;

import java.beans.Introspector;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.ParameterizedType;

/**
 * Created by Jean-Philippe Belanger on 3/26/17. Just potential zeroes and ones
 */
public class SimpleJacksonTypes implements TypeHandler {

  @Override
  public void writeType(TypeRegistry registry, IndentedAppendable writer,
                        RamlEntity type)
      throws IOException {

    Type javaType = type.getType();
    Class c = Cast.toClass(javaType);
    if (c.isEnum()) {

      writeEnum(registry, writer, c, type);
    } else {

      writeBody(registry, writer, type);
    }
  }

  private void writeEnum(TypeRegistry registry, IndentedAppendable writer,
                         final Class enumType, RamlEntity entity)
      throws IOException {


    writer.appendLine("type", enumType.getSimpleName());
    registry.registerType(enumType.getSimpleName(), entity, new TypeScanner() {

      @Override
      public void scanType(TypeRegistry typeRegistry, RamlEntity type, RamlType ramlType) {

        ramlType.setEnumValues(FluentIterable.of(enumType.getEnumConstants()).transform(new Function<Object, String>() {

          @Override
          public String apply(Object input) {
            return ((Enum) input).name().toLowerCase();
          }
        }).toList());
      }
    });
  }

  private void writeBody(TypeRegistry registry, IndentedAppendable writer,
                         RamlEntity bodyType)
      throws IOException {

    Class type = Cast.toClass(bodyType.getType());

    writer.appendLine("type", type.getSimpleName());

    registry.registerType(type.getSimpleName(), bodyType, new SimpleJacksonTypeScanner());
  }

  private static class SimpleJacksonTypeScanner implements TypeScanner {

    @Override
    public void scanType(TypeRegistry typeRegistry, RamlEntity ramlEntity, RamlType ramlType) {
      Type type = ramlEntity.getType();
      Class c = Cast.toClass(type);

      forFields(typeRegistry, ramlEntity, ramlType, c);
      forProperties(typeRegistry, ramlEntity, ramlType, c);
    }

    private void forProperties(TypeRegistry typeRegistry, RamlEntity type, RamlType ramlType, Class c) {
      for (Method method : c.getDeclaredMethods()) {

        if (!(method.getName().startsWith("get") || method.getName().startsWith("is"))) {

          continue;
        }

        if (Modifier.isStatic(method.getModifiers())) {

          continue;
        }


        Type genericType = method.getGenericReturnType();
        RamlType fieldRamlType;
        fieldRamlType = PluginUtilities
            .getRamlType(typeRegistry, this, method.getReturnType().getSimpleName(), type.createDependent(genericType));

        JsonProperty elem = method.getAnnotation(JsonProperty.class);
        if (elem != null) {

          String name = elem.value().equals("") ? buildName(method) : elem.value();
          ramlType.addProperty(RamlProperty.createProperty(new MethodAnnotable(method), name, fieldRamlType));
        }
      }
    }

    private void forFields(TypeRegistry typeRegistry, RamlEntity type, RamlType ramlType, Class c) {
      for (Field field : c.getDeclaredFields()) {


        Type genericType = field.getGenericType();
        RamlType fieldRamlType = PluginUtilities
            .getRamlType(typeRegistry, this, field.getType().getSimpleName(), type.createDependent(genericType));

        JsonProperty elem = field.getAnnotation(JsonProperty.class);
        if (elem != null) {

          String name = elem.value().equals("") ? field.getName() : elem.value();
          ramlType.addProperty(RamlProperty.createProperty(new FieldAnnotable(field), name, fieldRamlType));
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

  }
}
