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
import org.raml.jaxrs.emitters.ExampleModelEmitter;
import org.raml.jaxrs.emitters.LocalEmitter;
import org.raml.jaxrs.emitters.ModelEmitterAnnotations;
import org.raml.jaxrs.handlers.PojoToRamlProperty;
import org.raml.pojotoraml.*;
import org.raml.pojotoraml.plugins.PojoToRamlClassParserFactory;
import org.raml.pojotoraml.plugins.PojoToRamlExtensionFactory;
import org.raml.utilities.tuples.ImmutablePair;
import org.raml.utilities.tuples.Pair;
import org.raml.utilities.types.Cast;
import sun.security.krb5.internal.crypto.Des;

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

  static private Map<String, RamlType> allTypes = new HashMap<>();
  static private Map<String, TypeBuilder> allBuilders = new HashMap<>();
  static private Map<Type, String> ramlTypeNames = new HashMap<>();

  private final Type realType;
  private final Descriptor descriptor;

  private Map<String, RamlProperty> properties = new HashMap<>();


  public RamlType(Type type, Descriptor descriptor) {

    this.realType = type;
    this.descriptor = descriptor;
  }

  public static Map<String, RamlType> getAllTypes() {
    return allTypes;
  }

  public void addProperty(RamlProperty property) {

    properties.put(property.getName(), property);
  }

  public void write(final List<RamlSupportedAnnotation> supportedAnnotations, Package topPackage,
                    RamlDocumentBuilder documentBuilder)
      throws IOException {

    Type ttype = realType;
    Class<?> c = Cast.toClass(ttype);

    final List<Pair<SimpleAnnotable, TypePropertyBuilder>> pojoToRamlProperties = new ArrayList<>();
    final PojoToRamlExtensionFactory toRamlClassParserFactory = new PojoToRamlExtensionFactory(topPackage);

    final PojoToRaml pojoToRaml = PojoToRamlBuilder.create(new PojoToRamlClassParserFactory(topPackage), new AdjusterFactory() {

      @Override
      public RamlAdjuster createAdjuster(Class<?> clazz) {
        RamlAdjuster adjuster =
            toRamlClassParserFactory.createAdjusters(clazz, new Fixer(supportedAnnotations, pojoToRamlProperties),
                                                     new RamlAdjuster.Helper() {

                                                       @Override
                                                       public TypeBuilder adjustType(Type type, String typeName,
                                                                                     TypeBuilder builder) {

                                                         allTypes.put(typeName, new RamlType(type, new Descriptor() {

                                                           @Override
                                                           public Optional<String> describe() {
                                                             return Optional.absent();
                                                           }
                                                         }));
                                                         allBuilders.put(typeName, builder);
                                                         ramlTypeNames.put(type, typeName);
                                                         return super.adjustType(type, typeName, builder);
                                                       }

                                                       @Override
                                                       public TypePropertyBuilder adjustScalarProperty(TypeDeclarationBuilder typeDeclaration,
                                                                                                       final Property property,
                                                                                                       TypePropertyBuilder typePropertyBuilder) {


                                                         allTypes
                                                             .get(typeDeclaration.id())
                                                             .addProperty(new RamlProperty(property.name(),
                                                                                           new SimpleAnnotable(property), true));
                                                         return super.adjustScalarProperty(typeDeclaration, property,
                                                                                           typePropertyBuilder);
                                                       }

                                                       @Override
                                                       public TypePropertyBuilder adjustComposedProperty(TypeDeclarationBuilder typeDeclaration,
                                                                                                         final Property property,
                                                                                                         TypePropertyBuilder typePropertyBuilder) {

                                                         allTypes.get(typeDeclaration.id())
                                                             .addProperty(new
                                                                          RamlProperty(property.name(),
                                                                                       new SimpleAnnotable(property), true));

                                                         return super.adjustComposedProperty(typeDeclaration, property,
                                                                                             typePropertyBuilder);
                                                       }
                                                     });
        return adjuster;
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

    return ramlTypeNames.get(realType);
  }


  @Override
  public <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationType) {

    if (realType instanceof Class) {
      return Optional.fromNullable(((Class<?>) realType).getAnnotation(annotationType));
    } else {
      return Optional.absent();
    }
  }

  public boolean isRamlScalarType() {

    Optional<ScalarType> st = ScalarType.fromType(realType);
    return st.isPresent();
  }

  public void emit(LocalEmitter emitter) throws IOException {

    emitter.emit(this);
  }

  public Collection<RamlProperty> getProperties() {
    return properties.values();
  }

  public void emitExamples() throws IOException {

    this.emit(new ExampleModelEmitter(allBuilders.get(getTypeName())));
  }

  private static class SimpleAnnotable implements Annotable {

    private final Property property;

    public SimpleAnnotable(Property property) {
      this.property = property;
    }

    @Override
    public <T extends Annotation> Optional<T>
        getAnnotation(Class<T> annotationType) {
      return property
          .getAnnotation(annotationType);
    }
  }

  private class Fixer extends RamlAdjuster.Helper {

    private final List<RamlSupportedAnnotation> supportedAnnotations;
    private final List<Pair<SimpleAnnotable, TypePropertyBuilder>> pojoToRamlProperties;

    public Fixer(List<RamlSupportedAnnotation> supportedAnnotations,
                 List<Pair<SimpleAnnotable, TypePropertyBuilder>> pojoToRamlProperties) {
      this.supportedAnnotations = supportedAnnotations;
      this.pojoToRamlProperties = pojoToRamlProperties;
    }

    @Override
    public TypeBuilder adjustType(Type actualType, String typeName, TypeBuilder typeBuilder) {

      try {
        ModelEmitterAnnotations.annotate(supportedAnnotations, RamlType.this, typeBuilder);
      } catch (IOException e) {
        e.printStackTrace();
      }

      if (descriptor.describe().isPresent()) {

        typeBuilder.description(descriptor.describe().get());
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

      pojoToRamlProperties.add(ImmutablePair.create(new SimpleAnnotable(property), typePropertyBuilder));
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

      pojoToRamlProperties.add(ImmutablePair.create(new SimpleAnnotable(property), typePropertyBuilder));
      return typePropertyBuilder;
    }
  }
}
