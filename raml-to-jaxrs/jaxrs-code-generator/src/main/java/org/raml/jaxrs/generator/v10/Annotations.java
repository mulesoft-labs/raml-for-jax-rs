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
package org.raml.jaxrs.generator.v10;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.extension.resources.ResourceClassExtension;
import org.raml.jaxrs.generator.extension.resources.ResourceMethodExtension;
import org.raml.jaxrs.generator.extension.resources.ResponseClassExtension;
import org.raml.jaxrs.generator.extension.resources.ResponseMethodExtension;
import org.raml.jaxrs.generator.ramltypes.GMethod;
import org.raml.jaxrs.generator.ramltypes.GResource;
import org.raml.jaxrs.generator.ramltypes.GResponse;
import org.raml.v2.api.model.v10.common.Annotable;
import org.raml.v2.api.model.v10.datamodel.TypeInstance;
import org.raml.v2.api.model.v10.datamodel.TypeInstanceProperty;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 1/2/17. Just potential zeroes and ones
 */
public abstract class Annotations<T> {


  public static Annotations<String> CLASS_NAME = new Annotations<String>() {

    @Override
    public String getWithContext(CurrentBuild currentBuild, Annotable target, Annotable... others) {

      return getWithDefault("types", "classname", null, target, others);
    }
  };


  public static Annotations<Boolean> USE_PRIMITIVE_TYPE = new Annotations<Boolean>() {

    @Override
    public Boolean getWithContext(CurrentBuild currentBuild, Annotable target, Annotable... others) {

      return getWithDefault("types", "usePrimitiveType", false, target, others);
    }

  };

  /*
   * Resources.
   */
  public static Annotations<ResourceClassExtension<GResource>> ON_RESOURCE_CLASS_CREATION =
      new Annotations<ResourceClassExtension<GResource>>() {

        @Override
        public ResourceClassExtension<GResource> getWithContext(CurrentBuild currentBuild, Annotable target, Annotable... others) {
          List<String> classNames = getWithDefault("resources", "onResourceClassCreation", null, target, others);

          List<ResourceClassExtension<GResource>> extension = createExtension(currentBuild, classNames);
          return new ResourceClassExtension.Composite(extension);
        }
      };

  public static Annotations<ResourceClassExtension<GResource>> ON_RESOURCE_CLASS_FINISH =
      new Annotations<ResourceClassExtension<GResource>>() {

        @Override
        public ResourceClassExtension<GResource> getWithContext(CurrentBuild currentBuild, Annotable target, Annotable... others) {
          List<String> classNames = getWithDefault("resources", "onResourceClassFinish", null, target, others);

          List<ResourceClassExtension<GResource>> extension = createExtension(currentBuild, classNames);
          return new ResourceClassExtension.Composite(extension);
        }
      };

  public static Annotations<ResourceMethodExtension<GMethod>> ON_METHOD_CREATION =
      new Annotations<ResourceMethodExtension<GMethod>>() {

        @Override
        public ResourceMethodExtension<GMethod> getWithContext(CurrentBuild currentBuild, Annotable target, Annotable... others) {

          List<String> classNames = getWithDefault("methods", "onResourceMethodCreation", null, target, others);

          List<ResourceMethodExtension<GMethod>> extension = createExtension(currentBuild, classNames);
          return new ResourceMethodExtension.Composite(extension);
        }
      };

  public static Annotations<ResourceMethodExtension<GMethod>> ON_METHOD_FINISH =
      new Annotations<ResourceMethodExtension<GMethod>>() {

        @Override
        public ResourceMethodExtension<GMethod> getWithContext(CurrentBuild currentBuild, Annotable target, Annotable... others) {
          List<String> classNames = getWithDefault("methods", "onResourceMethodFinish", null, target, others);

          List<ResourceMethodExtension<GMethod>> extension = createExtension(currentBuild, classNames);
          return new ResourceMethodExtension.Composite(extension);
        }
      };



  public static Annotations<ResponseClassExtension<GMethod>> ON_RESPONSE_CLASS_CREATION =
      new Annotations<ResponseClassExtension<GMethod>>() {

        @Override
        public ResponseClassExtension<GMethod> getWithContext(CurrentBuild currentBuild, Annotable target, Annotable... others) {
          List<String> classNames = getWithDefault("methods", "onResponseClassCreation", null, target, others);

          List<ResponseClassExtension<GMethod>> extension = createExtension(currentBuild, classNames);
          return new ResponseClassExtension.Composite(extension);
        }
      };

  public static Annotations<ResponseClassExtension<GMethod>> ON_RESPONSE_CLASS_FINISH =
      new Annotations<ResponseClassExtension<GMethod>>() {

        @Override
        public ResponseClassExtension<GMethod> getWithContext(CurrentBuild currentBuild, Annotable target, Annotable... others) {
          List<String> classNames = getWithDefault("methods", "onResponseClassFinish", null, target, others);

          List<ResponseClassExtension<GMethod>> extension = createExtension(currentBuild, classNames);
          return new ResponseClassExtension.Composite(extension);
        }
      };



  public static Annotations<ResponseMethodExtension<GResponse>> ON_RESPONSE_METHOD_CREATION =
      new Annotations<ResponseMethodExtension<GResponse>>() {

        @Override
        public ResponseMethodExtension<GResponse> getWithContext(CurrentBuild currentBuild, Annotable target, Annotable... others) {

          List<String> classNames = getWithDefault("responses", "onResponseMethodCreation", null, target, others);

          List<ResponseMethodExtension<GResponse>> extension = createExtension(currentBuild, classNames);
          return new ResponseMethodExtension.Composite(extension);
        }
      };

  public static Annotations<ResponseMethodExtension<GResponse>> ON_RESPONSE_METHOD_FINISH =
      new Annotations<ResponseMethodExtension<GResponse>>() {

        @Override
        public ResponseMethodExtension<GResponse> getWithContext(CurrentBuild currentBuild, Annotable target, Annotable... others) {
          List<String> classNames = getWithDefault("responses", "onResponseMethodFinish", null, target, others);

          List<ResponseMethodExtension<GResponse>> extension = createExtension(currentBuild, classNames);
          return new ResponseMethodExtension.Composite(extension);
        }
      };



  private static <T> List<T> createExtension(final CurrentBuild currentBuild, List<String> classNames) {
    if (classNames == null) {

      return Collections.emptyList();
    } else {

      return FluentIterable.from(classNames).transformAndConcat(new Function<String, Iterable<T>>() {

        @Nullable
        @Override
        public Iterable<T> apply(@Nullable String input) {
          return (Iterable<T>) currentBuild.createExtensions(input);
        }
      }).toList();
    }
  }


  private static <T> T getWithDefault(String annotationName, String propName, T def, Annotable target, Annotable... others) {
    T b = Annotations.evaluate(annotationName, propName, target, others);
    if (b == null) {

      return def;
    } else {
      return b;
    }
  }

  private static <T> T evaluate(String annotationName, String parameterName, Annotable mandatory, Annotable... others) {

    T retval = null;
    List<Annotable> targets = new ArrayList<>();
    targets.add(mandatory);
    targets.addAll(Arrays.asList(others));

    for (Annotable target : targets) {

      AnnotationRef annotationRef = Annotations.findRef(target, annotationName);
      if (annotationRef == null) {

        continue;
      }

      Object o = findProperty(annotationRef, parameterName);
      if (o != null) {
        retval = (T) o;
      }

    }

    return retval;
  }

  private static Object findProperty(AnnotationRef annotationRef, String propName) {


    // annotationRef.structuredValue().properties().get(0).values().get(0).value()
    for (TypeInstanceProperty typeInstanceProperty : annotationRef.structuredValue().properties()) {
      if (typeInstanceProperty.name().equalsIgnoreCase(propName)) {
        if (typeInstanceProperty.isArray()) {
          return toValueList(typeInstanceProperty.values());
        } else {
          return typeInstanceProperty.value().value();
        }
      }
    }

    return null;
  }

  private static List<Object> toValueList(List<TypeInstance> values) {

    return Lists.transform(values, new Function<TypeInstance, Object>() {

      @Nullable
      @Override
      public Object apply(@Nullable TypeInstance input) {
        return input.value();
      }
    });
  }

  private static AnnotationRef findRef(Annotable annotable, String annotation) {

    for (AnnotationRef annotationRef : annotable.annotations()) {
      if (annotationRef.annotation().name().equalsIgnoreCase(annotation)) {

        return annotationRef;
      }
    }

    return null;
  }

  public abstract T getWithContext(CurrentBuild currentBuild, Annotable target, Annotable... others);

  public T getValueWithDefault(T def, Annotable annotable, Annotable... others) {

    T t = getWithContext(null, annotable, others);
    if (t == null) {

      return def;
    } else {
      return t;
    }
  }

  public T get(V10GType type) {

    return getWithContext(null, type.implementation());
  }

  public T get(V10GResource resource) {

    return getWithContext(null, resource.implementation());
  }

  public T get(V10GMethod method) {

    return getWithContext(null, method.implementation());
  }

  public T get(V10GResponse response) {

    return getWithContext(null, response.implementation());
  }

  public T get(T def, V10GType type) {

    return get(def, type.implementation());
  }

  public T get(T def, Annotable type) {

    return getValueWithDefault(def, type);
  }

  public T get(Annotable type) {

    return getValueWithDefault(null, type);
  }

  public T get(T def, Annotable type, Annotable others) {

    return getValueWithDefault(def, type, others);
  }

}
