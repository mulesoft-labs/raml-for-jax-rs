/*
 * Copyright ${licenseYear} (c) MuleSoft, Inc.
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

import org.raml.jaxrs.generator.GenerationException;
import org.raml.jaxrs.generator.extension.resources.ResourceClassExtension;
import org.raml.jaxrs.generator.extension.resources.ResourceMethodExtension;
import org.raml.jaxrs.generator.extension.resources.ResponseClassExtension;
import org.raml.jaxrs.generator.extension.resources.ResponseMethodExtension;
import org.raml.jaxrs.generator.ramltypes.GMethod;
import org.raml.jaxrs.generator.ramltypes.GResource;
import org.raml.jaxrs.generator.ramltypes.GResponse;
import org.raml.v2.api.model.v10.common.Annotable;
import org.raml.v2.api.model.v10.datamodel.TypeInstanceProperty;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 1/2/17. Just potential zeroes and ones
 */
public abstract class Annotations<T> {


  public static Annotations<String> CLASS_NAME = new Annotations<String>() {

    @Override
    public String get(Annotable target, Annotable... others) {

      return getWithDefault("types", "classname", null, target, others);
    }
  };

  public static Annotations<String> IMPLEMENTATION_CLASS_NAME = new Annotations<String>() {

    @Override
    public String get(Annotable target, Annotable... others) {

      return getWithDefault("types", "implementationClassName", null, target, others);
    }
  };

  public static Annotations<Boolean> USE_PRIMITIVE_TYPE = new Annotations<Boolean>() {

    @Override
    public Boolean get(Annotable target, Annotable... others) {

      return getWithDefault("types", "usePrimitiveType", false, target, others);
    }

  };

  public static Annotations<Boolean> ABSTRACT = new Annotations<Boolean>() {

    @Override
    public Boolean get(Annotable target, Annotable... others) {

      return getWithDefault("types", "abstract", false, target, others);
    }
  };

  public static Annotations<ResourceClassExtension<GResource>> ON_RESOURCE_CLASS_CREATION =
      new Annotations<ResourceClassExtension<GResource>>() {

        @Override
        public ResourceClassExtension<GResource> get(Annotable target, Annotable... others) {
          String className =
              getWithDefault("resources", "onResourceClassCreation", null, target, others);
          return createExtension(className, null);
        }
      };

  public static Annotations<ResourceClassExtension<GResource>> ON_RESOURCE_CLASS_FINISH =
      new Annotations<ResourceClassExtension<GResource>>() {

        @Override
        public ResourceClassExtension<GResource> get(Annotable target, Annotable... others) {
          String className =
              getWithDefault("resources", "onResourceClassFinish", null, target, others);
          return createExtension(className, ResourceClassExtension.NULL_EXTENSION);
        }
      };

  public static Annotations<ResourceMethodExtension<GMethod>> ON_METHOD_CREATION =
      new Annotations<ResourceMethodExtension<GMethod>>() {

        @Override
        public ResourceMethodExtension<GMethod> get(Annotable target, Annotable... others) {
          String className =
              getWithDefault("methods", "onResourceMethodCreation", null, target, others);
          return createExtension(className, ResourceMethodExtension.NULL_EXTENSION);
        }
      };

  public static Annotations<ResourceMethodExtension<GMethod>> ON_METHOD_FINISH =
      new Annotations<ResourceMethodExtension<GMethod>>() {

        @Override
        public ResourceMethodExtension<GMethod> get(Annotable target, Annotable... others) {
          String className =
              getWithDefault("methods", "onResourceMethodFinish", null, target, others);
          return createExtension(className, ResourceMethodExtension.NULL_EXTENSION);
        }
      };

  public static Annotations<ResponseClassExtension<GMethod>> ON_RESPONSE_CLASS_CREATION =
      new Annotations<ResponseClassExtension<GMethod>>() {

        @Override
        public ResponseClassExtension<GMethod> get(Annotable target, Annotable... others) {
          String className =
              getWithDefault("methods", "onResponseClassCreation", null, target, others);
          return createExtension(className, ResponseClassExtension.NULL_EXTENSION);
        }
      };

  public static Annotations<ResponseClassExtension<GMethod>> ON_RESPONSE_CLASS_FINISH =
      new Annotations<ResponseClassExtension<GMethod>>() {

        @Override
        public ResponseClassExtension<GMethod> get(Annotable target, Annotable... others) {
          String className =
              getWithDefault("methods", "onResponseClassFinish", null, target, others);
          return createExtension(className, ResponseClassExtension.NULL_EXTENSION);
        }
      };

  public static Annotations<ResponseMethodExtension<GResponse>> ON_RESPONSE_METHOD_CREATION =
      new Annotations<ResponseMethodExtension<GResponse>>() {

        @Override
        public ResponseMethodExtension<GResponse> get(Annotable target, Annotable... others) {
          String className =
              getWithDefault("responses", "onResponseMethodCreation", null, target, others);
          return createExtension(className, ResponseMethodExtension.NULL_EXTENSION);
        }
      };

  public static Annotations<ResponseMethodExtension<GResponse>> ON_RESPONSE_METHOD_FINISH =
      new Annotations<ResponseMethodExtension<GResponse>>() {

        @Override
        public ResponseMethodExtension<GResponse> get(Annotable target, Annotable... others) {
          String className =
              getWithDefault("responses", "onResponseMethodFinish", null, target, others);
          return createExtension(className, ResponseMethodExtension.NULL_EXTENSION);
        }
      };

  private static <T> T createExtension(String className, T nullExtension) {
    if (className == null) {

      return nullExtension;
    } else {

      try {
        return (T) Class.forName(className).newInstance();
      } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
        throw new GenerationException("Cannot find resource creation extension: " + className);
      }
    }
  }

  private static <T> T getWithDefault(String annotationName, String propName, T def,
                                      Annotable target, Annotable... others) {
    T b = Annotations.evaluate(annotationName, propName, target, others);
    if (b == null) {

      return def;
    } else {
      return b;
    }
  }

  private static <T> T evaluate(String annotationName, String parameterName, Annotable mandatory,
                                Annotable... others) {

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


    for (TypeInstanceProperty typeInstanceProperty : annotationRef.structuredValue().properties()) {
      if (typeInstanceProperty.name().equalsIgnoreCase(propName)) {
        return typeInstanceProperty.value().value();
      }
    }

    return null;
  }

  private static AnnotationRef findRef(Annotable annotable, String annotation) {

    for (AnnotationRef annotationRef : annotable.annotations()) {
      if (annotationRef.annotation().name().equalsIgnoreCase(annotation)) {

        return annotationRef;
      }
    }

    return null;
  }

  public abstract T get(Annotable target, Annotable... others);

  public T get(T def, Annotable annotable, Annotable... others) {

    T t = get(annotable, others);
    if (t == null) {

      return def;
    } else {
      return t;
    }
  }

  public T get(V10GType type) {

    return get(type.implementation());
  }

  public T get(V10GResource resource) {

    return get(resource.implementation());
  }

  public T get(V10GMethod method) {

    return get(method.implementation());
  }

  public T get(V10GResponse response) {

    return get(response.implementation());
  }

  public T get(T def, V10GType type) {

    return get(def, type.implementation());
  }
}
