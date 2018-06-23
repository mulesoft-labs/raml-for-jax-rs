/*
 * Copyright 2013-2018 (c) MuleSoft, Inc.
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
import org.raml.pojotoraml.ClassParser;
import org.raml.pojotoraml.Property;
import org.raml.utilities.types.Cast;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Created by Jean-Philippe Belanger on 3/26/17. Just potential zeroes and ones
 */
public class BeanLikeClassParser implements ClassParser {

  @Override
  public List<Property> properties(Class<?> classToParse) {

    Class currentInterface = Cast.toClass(classToParse);

    Method[] methods = currentInterface.getDeclaredMethods();
    List<Property> properties = new ArrayList<>();
    for (final Method method : methods) {

      if (method.getName().startsWith("get")) {

        if ("getClass".equals(method.getName()) && method.getParameterTypes().length == 0) {
          continue;
        }

        final String badlyCasedfieldName = method.getName().substring(3);
        properties.add(new BeanProperty(badlyCasedfieldName, method));
      }
    }

    return properties;
  }

  @Override
  public Collection<Type> parentClasses(Class<?> classToParse) {

    Class currentInterface = Cast.toClass(classToParse);
    Class[] interfaces = currentInterface.getInterfaces();

    List<Type> superTypes = new ArrayList<>();
    Collections.addAll(superTypes, interfaces);

    return superTypes;
  }

  private static class BeanProperty implements PojoToRamlProperty {

    private final String badlyCasedfieldName;
    private final Method method;

    public BeanProperty(String badlyCasedfieldName, Method method) {
      this.badlyCasedfieldName = badlyCasedfieldName;
      this.method = method;
    }

    @Override
    public String name() {
      return Character.toLowerCase(badlyCasedfieldName.charAt(0)) + badlyCasedfieldName.substring(1);
    }

    @Override
    public Type type() {
      return method.getGenericReturnType();
    }

    @Override
    public <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationType) {
      return Optional.fromNullable(method.getAnnotation(annotationType));
    }
  }
}
