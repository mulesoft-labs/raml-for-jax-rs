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

import org.raml.jaxrs.common.RamlGenerator;
import org.raml.pojotoraml.ClassParser;
import org.raml.pojotoraml.ClassParserFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * Created. There, you have it.
 */
public class PojoToRamlClassParserFactory implements ClassParserFactory {

  @Override
  public ClassParser createParser(Class<?> clazz) {

    RamlGenerator generator = clazz.getAnnotation(RamlGenerator.class);

    ClassParser parser = new BeanLikeClassParser(clazz);
    if (generator != null) {
      try {
        parser = generator.parser().getConstructor(Class.class).newInstance(clazz);
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      }
    }

    return parser;
  }
}
