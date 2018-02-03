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

import com.google.common.base.Optional;
import org.raml.api.Annotable;
import org.raml.api.RamlData;
import org.raml.api.RamlSupportedAnnotation;
import org.raml.api.ScalarType;
import org.raml.builder.RamlDocumentBuilder;
import org.raml.builder.TypeBuilder;
import org.raml.builder.TypeDeclarationBuilder;
import org.raml.builder.TypePropertyBuilder;
import org.raml.jaxrs.emitters.Emittable;
import org.raml.jaxrs.emitters.LocalEmitter;
import org.raml.jaxrs.emitters.ModelEmitterAnnotations;
import org.raml.jaxrs.handlers.PojoToRamlProperty;
import org.raml.pojotoraml.*;
import org.raml.pojotoraml.plugins.PojoToRamlClassParserFactory;
import org.raml.pojotoraml.plugins.PojoToRamlExtensionFactory;
import org.raml.utilities.tuples.ImmutablePair;
import org.raml.utilities.tuples.Pair;
import org.raml.utilities.types.Cast;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

/**
 * Created by Jean-Philippe Belanger on 3/26/17. Just potential zeroes and ones
 */

public class RamlType implements Annotable, Emittable {

  private final RamlData type;

  private final boolean collection;

  private Map<String, RamlProperty> properties = new HashMap<>();
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

  public void write(final List<RamlSupportedAnnotation> supportedAnnotations, Package topPackage,
                    RamlDocumentBuilder documentBuilder)
      throws IOException {

    Type ttype = type.getType();
    Class<?> c = Cast.toClass(ttype);

    final List<Pair<PojoToRamlProperty, TypePropertyBuilder>> pojoToRamlProperties = new ArrayList<>();
    final PojoToRamlExtensionFactory toRamlClassParserFactory = new PojoToRamlExtensionFactory(topPackage);

    final PojoToRaml pojoToRaml = PojoToRamlBuilder.create(new PojoToRamlClassParserFactory(topPackage), new AdjusterFactory() {

      @Override
      public RamlAdjuster createAdjuster(Class<?> clazz) {
        return toRamlClassParserFactory.createAdjusters(clazz, new Fixer(supportedAnnotations, pojoToRamlProperties));
      }
    });

    Result r = pojoToRaml.classToRaml(c);

    // new ExampleModelEmitter(this, pojoToRamlProperties);

    if (r.requestedType() != null) {
      documentBuilder.withTypes(r.requestedType());
      for (TypeDeclarationBuilder typeDeclarationBuilder : r.dependentTypes()) {

        documentBuilder.withTypes(typeDeclarationBuilder);
      }
    }

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


  public void setSuperTypes(List<RamlType> superTypes) {}

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

  private class Fixer extends RamlAdjuster.Helper {

    private final List<RamlSupportedAnnotation> supportedAnnotations;
    private final List<Pair<PojoToRamlProperty, TypePropertyBuilder>> pojoToRamlProperties;

    public Fixer(List<RamlSupportedAnnotation> supportedAnnotations,
                 List<Pair<PojoToRamlProperty, TypePropertyBuilder>> pojoToRamlProperties) {
      this.supportedAnnotations = supportedAnnotations;
      this.pojoToRamlProperties = pojoToRamlProperties;
    }

    @Override
    public TypeBuilder adjustType(Type actualType, TypeBuilder typeBuilder) {

      try {
        ModelEmitterAnnotations.annotate(supportedAnnotations, RamlType.this, typeBuilder);
      } catch (IOException e) {
        e.printStackTrace();
      }

      if (type.getDescription().isPresent()) {

        typeBuilder.description(type.getDescription().get());
      }

      if (enumValues != null) {

        typeBuilder.enumValues(enumValues.toArray(new String[0]));
      }

      return typeBuilder;
    }


    @Override
    public TypePropertyBuilder adjustScalarProperty(TypeDeclarationBuilder typeDeclaration, final Property property,
                                                    TypePropertyBuilder typePropertyBuilder) {

      try {
        // todo fix: annotable should be outside
        ModelEmitterAnnotations.annotate(supportedAnnotations, new Annotable() {

          @Override
          public <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationType) {
            return property.getAnnotation(annotationType);
          }
        }, typePropertyBuilder);
      } catch (IOException e) {
        e.printStackTrace();
      }

      pojoToRamlProperties.add(ImmutablePair.create((PojoToRamlProperty) property, typePropertyBuilder));
      return typePropertyBuilder;
    }

    @Override
    public TypePropertyBuilder adjustComposedProperty(TypeDeclarationBuilder typeDeclaration, final Property property,
                                                      TypePropertyBuilder typePropertyBuilder) {
      try {
        // todo fix: annotable should be outside
        ModelEmitterAnnotations.annotate(supportedAnnotations, new Annotable() {

          @Override
          public <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationType) {
            return property.getAnnotation(annotationType);
          }
        }, typePropertyBuilder);

      } catch (IOException e) {
        e.printStackTrace();
      }

      pojoToRamlProperties.add(ImmutablePair.create((PojoToRamlProperty) property, typePropertyBuilder));
      return typePropertyBuilder;
    }
  }
}
