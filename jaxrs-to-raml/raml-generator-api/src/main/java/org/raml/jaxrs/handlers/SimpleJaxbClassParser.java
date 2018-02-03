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

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import org.raml.pojotoraml.ClassParser;
import org.raml.pojotoraml.Property;
import org.raml.utilities.types.Cast;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Jean-Philippe Belanger on 3/26/17. Just potential zeroes and ones
 */
public class SimpleJaxbClassParser implements ClassParser {

  @Override
  public List<Property> properties(Class<?> classToParse) {

    Map<String, PojoToRamlProperty> properties = new TreeMap<>();

    Class c = Cast.toClass(classToParse);
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
        forFields(classToParse, properties, false);
        forProperties(classToParse, properties, true, false);
        break;
      case PROPERTY:
        // Only properties
        forProperties(classToParse, properties, false, false);
        forFields(classToParse, properties, true);
        break;
      case PUBLIC_MEMBER:
        forProperties(classToParse, properties, false, true);
        forFields(classToParse, properties, true);
        break;
    }

    return FluentIterable.from(properties.entrySet()).transform(new Function<Map.Entry<String, PojoToRamlProperty>, Property>() {

      @Nullable
      @Override
      public Property apply(@Nullable Map.Entry<String, PojoToRamlProperty> entry) {
        return entry.getValue();
      }
    }).toList();
  }

  @Override
  public Collection<Type> parentClasses(Class<?> classToParse) {
    return null;
  }

  private void forProperties(Class<?> classToParse, Map<String, PojoToRamlProperty> properties, boolean explicitOnly,
                             boolean publicOnly) {
    for (final Method method : classToParse.getDeclaredMethods()) {

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

      final Type genericType = method.getGenericReturnType();

      if (explicitOnly && (!method.isAnnotationPresent(XmlAttribute.class) && (!method.isAnnotationPresent(XmlElement.class)))) {

        continue;
      }

      XmlElement elem = method.getAnnotation(XmlElement.class);
      if (elem != null) {

        final String name = elem.name().equals("##default") ? buildName(method) : elem.name();
        properties.put(name, new MethodPojoToRamlProperty(method, name, genericType));

      } else {

        XmlAttribute attribute = method.getAnnotation(XmlAttribute.class);
        if (attribute != null) {

          String name = elem.name().equals("##default") ? buildName(method) : elem.name();
          properties.put(name, new MethodPojoToRamlProperty(method, name, genericType));
        } else {

          properties.put(buildName(method), new MethodPojoToRamlProperty(method, buildName(method), genericType));
        }
      }
    }
  }

  private void forFields(Class<?> classToParse, Map<String, PojoToRamlProperty> properties, boolean explicitOnly) {
    for (Field field : classToParse.getDeclaredFields()) {

      if (field.isAnnotationPresent(XmlTransient.class) || Modifier.isTransient(field.getModifiers())) {

        continue;
      }

      if (explicitOnly && (!field.isAnnotationPresent(XmlAttribute.class) && (!field.isAnnotationPresent(XmlElement.class)))) {

        continue;
      }

      Type genericType = field.getGenericType();

      XmlElement elem = field.getAnnotation(XmlElement.class);
      if (elem != null) {

        String name = elem.name().equals("##default") ? field.getName() : elem.name();
        properties.put(name, new FieldPojoToRamlProperty(field, name, genericType));
      } else {

        XmlAttribute attribute = field.getAnnotation(XmlAttribute.class);
        if (attribute != null) {

          String name = elem.name().equals("##default") ? field.getName() : elem.name();
          properties.put(name, new FieldPojoToRamlProperty(field, name, genericType));
        } else {

          properties.put(field.getName(), new FieldPojoToRamlProperty(field, field.getName(), genericType));
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

  private static class MethodPojoToRamlProperty implements PojoToRamlProperty {

    private final Method method;
    private final String name;
    private final Type genericType;

    public MethodPojoToRamlProperty(Method method, String name, Type genericType) {
      this.method = method;
      this.name = name;
      this.genericType = genericType;
    }

    @Override
    public <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationType) {
      return Optional.fromNullable(method.getAnnotation(annotationType));
    }

    @Override
    public String name() {
      return name;
    }

    @Override
    public Type type() {
      return genericType;
    }
  }

  private static class FieldPojoToRamlProperty implements PojoToRamlProperty {

    private final Field field;
    private final String name;
    private final Type genericType;

    public FieldPojoToRamlProperty(Field field, String name, Type genericType) {
      this.field = field;
      this.name = name;
      this.genericType = genericType;
    }

    @Override
    public <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationType) {
      return Optional.fromNullable(field.getAnnotation(annotationType));
    }

    @Override
    public String name() {
      return name;
    }

    @Override
    public Type type() {
      return genericType;
    }
  }

}
