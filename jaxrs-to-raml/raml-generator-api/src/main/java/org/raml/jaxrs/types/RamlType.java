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
package org.raml.jaxrs.types;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import org.raml.api.Annotable;
import org.raml.api.RamlData;
import org.raml.api.ScalarType;
import org.raml.jaxrs.emitters.AnnotationInstanceEmitter;
import org.raml.jaxrs.emitters.Emittable;
import org.raml.jaxrs.emitters.ExampleEmitter;
import org.raml.jaxrs.emitters.LocalEmitter;
import org.raml.utilities.IndentedAppendable;
import org.raml.utilities.format.Joiner;
import org.raml.utilities.format.Joiners;
import org.raml.utilities.types.Cast;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 3/26/17. Just potential zeroes and ones
 */

public class RamlType implements Annotable, Emittable {

  private final RamlData type;

  private final boolean collection;

  private Map<String, RamlProperty> properties = new HashMap<>();
  private List<RamlType> superTypes;
  private Collection<String> enumValues;

  public RamlType(RamlData type) {

    this.type = type;
    this.collection = false;
  }

  public RamlType(RamlData type, boolean collection) {

    this.type = type;
    this.collection = collection;
  }

  public static RamlType collectionOf(RamlType collectionType) {

    return new RamlType(collectionType.type, true);
  }

  public void addProperty(RamlProperty property) {

    properties.put(property.getName(), property);
  }

  public void setEnumValues(Collection<String> values) {

    this.enumValues = values;
  }

  public void write(AnnotationInstanceEmitter emitter, IndentedAppendable writer) throws IOException {
    Type ttype = type.getType();
    Class c = Cast.toClass(ttype);
    writer.appendLine(c.getSimpleName() + ":");
    writer.indent();

    if (superTypes != null && superTypes.size() > 0) {
      writer.appendList("type", Collections2.transform(superTypes, new Function<RamlType, String>() {

        @Override
        public String apply(RamlType input) {
          return input.getTypeName();
        }
      }).toArray(new String[] {}));
    }

    emitter.emit(this);

    writeExample(writer);

    if (type.getDescription().isPresent()) {
      writer.appendEscapedLine("description", type.getDescription().get());
    }

    if (enumValues != null) {
      writer.appendLine("enum", Joiner.on(",").withPrefix("[").withSuffix("]").join(enumValues));
    }

    if (properties.values().size() > 0) {
      writer.appendLine("properties:");
      writer.indent();
      for (RamlProperty ramlProperty : properties.values()) {

        ramlProperty.write(emitter, writer);
      }
      writer.outdent();
    }
    writer.outdent();
  }

  public void writeExample(IndentedAppendable writer) throws IOException {

    this.emit(new ExampleEmitter(writer));
  }


  @SuppressWarnings({"rawtypes"})
  public String getTypeName() {

    Optional<ScalarType> st = ScalarType.fromType(type.getType());
    if (st.isPresent()) {
      if (collection == true) {
        return st.get().getRamlSyntax() + "[]";
      } else {
        return st.get().getRamlSyntax();
      }
    } else {
      Type typeType = type.getType();
      if (typeType instanceof ParameterizedType) {
        ParameterizedType pt = (ParameterizedType) typeType;
        typeType = pt.getRawType();
      }
      String name;
      if (typeType instanceof TypeVariable) {
        name = ((TypeVariable) typeType).getName();
      } else {
        Class c = (Class) typeType;
        name = c.getSimpleName();
      }

      if (collection == true) {
        return name + "[]";
      } else {
        return name;
      }
    }
  }


  public void setSuperTypes(List<RamlType> superTypes) {
    this.superTypes = superTypes;
  }

  @Override
  public <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationType) {
    return type.getAnnotation(annotationType);
  }

  public boolean isRamlScalarType() {

    Optional<ScalarType> st = ScalarType.fromType(type.getType());
    return st.isPresent();
  }

  public void emit(LocalEmitter emitter) throws IOException {

    emitter.emit(this);
  }

  public Collection<RamlProperty> getProperties() {
    return properties.values();
  }
}
