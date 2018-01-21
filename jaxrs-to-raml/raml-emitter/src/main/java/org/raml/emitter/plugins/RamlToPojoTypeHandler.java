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

import org.raml.api.RamlEntity;
import org.raml.api.RamlSupportedAnnotation;
import org.raml.builder.TypeBuilder;
import org.raml.builder.TypeDeclarationBuilder;
import org.raml.builder.TypePropertyBuilder;
import org.raml.jaxrs.handlers.BeanLikeClassParser;
import org.raml.jaxrs.plugins.TypeHandler;
import org.raml.jaxrs.plugins.TypeScanner;
import org.raml.jaxrs.types.RamlType;
import org.raml.jaxrs.types.TypeRegistry;
import org.raml.pojotoraml.*;
import org.raml.pojotoraml.field.FieldClassParser;
import org.raml.utilities.types.Cast;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;

/**
 * Created. There, you have it.
 */
public class RamlToPojoTypeHandler implements TypeHandler {

  @Override
  public String writeType(final TypeRegistry registry, RamlEntity type) throws IOException {

    final Class cls = Cast.toClass(type.getType());

    final PojoToRaml pojoToRaml = PojoToRamlBuilder.create(new ClassParserFactory() {

      @Override
      public ClassParser createParser(Class<?> clazz) {

        return new BeanLikeClassParser(clazz);
      }
    }, RamlAdjuster.NULL_ADJUSTER);


    String name = pojoToRaml.name(cls);
    registry.registerType(name, type);
    return name;
  }
}
