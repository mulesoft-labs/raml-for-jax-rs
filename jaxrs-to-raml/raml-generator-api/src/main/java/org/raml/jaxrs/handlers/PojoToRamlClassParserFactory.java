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
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.raml.jaxrs.common.RamlGenerator;
import org.raml.jaxrs.common.RamlGeneratorForClass;
import org.raml.jaxrs.common.RamlGenerators;
import org.raml.pojotoraml.ClassParser;
import org.raml.pojotoraml.ClassParserFactory;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;

/**
 * Created. There, you have it.
 */
public class PojoToRamlClassParserFactory implements ClassParserFactory {

  private final Package topPackage;

  public PojoToRamlClassParserFactory(Package topPackage) {
    this.topPackage = topPackage;
  }

  @Override
  public ClassParser createParser(final Class<?> clazz) {

    RamlGenerator generator = clazz.getAnnotation(RamlGenerator.class);

    ClassParser parser = null;
    if (generator != null) {
      try {
        parser = generator.parser().getConstructor(Class.class).newInstance(clazz);
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      }
    }

    if ( parser == null && topPackage != null ) {

      RamlGenerators generators = topPackage.getAnnotation(RamlGenerators.class);
      Optional<ClassParser> classParserOptional =  FluentIterable.of(generators.value()).filter(new Predicate<RamlGeneratorForClass>() {
        @Override
        public boolean apply(@Nullable RamlGeneratorForClass ramlGeneratorForClass) {
          return ramlGeneratorForClass.forClass().equals(clazz);
        }
      }).first().transform(new Function<RamlGeneratorForClass, ClassParser>() {
        @Nullable
        @Override
        public ClassParser apply(@Nullable RamlGeneratorForClass ramlGeneratorForClass) {
          try {
            return ramlGeneratorForClass.generator().parser().getConstructor(Class.class).newInstance(clazz);
          }catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {

            return null;
          }
        }
      });

      return classParserOptional.or(new BeanLikeClassParser(clazz));
    }

    return parser;
  }
}
