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

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.raml.api.RamlMediaType;
import org.raml.api.RamlResourceMethod;
import org.raml.utilities.IndentedAppendable;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import static java.lang.String.format;

/**
 * Created by Jean-Philippe Belanger on 3/26/17. Just potential zeroes and ones
 */
public class JaxbForRamlToJaxrs implements TypeHandler {

  @Override
  public void writeType(IndentedAppendable writer, RamlMediaType ramlMediaType, RamlResourceMethod method, Type type)
      throws IOException {

    List<RamlMediaType> mediaTypes = method.getConsumedMediaTypes();
    Optional<Type> bodyType = method.getConsumedType();

    writeBody(writer, mediaTypes, bodyType);

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


  private void writeBody(IndentedAppendable writer, List<RamlMediaType> mediaTypes, Optional<Type> bodyType)
      throws IOException {

    for (RamlMediaType mediaType : mediaTypes) {
      writer.appendLine(format("%s:", mediaType.toStringRepresentation()));
      if (bodyType.isPresent()) {

        Class type = (Class) bodyType.get();
        writer.indent();
        writer.appendLine("type: " + type.getSimpleName());
        writer.outdent();
      }
    }

    writer.outdent();
  }
}
