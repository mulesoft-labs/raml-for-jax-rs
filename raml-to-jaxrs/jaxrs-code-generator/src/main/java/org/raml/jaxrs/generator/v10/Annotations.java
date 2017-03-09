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
import com.google.common.collect.Lists;
import org.raml.jaxrs.generator.GenerationException;
import org.raml.jaxrs.generator.extension.resources.ResourceClassExtension;
import org.raml.jaxrs.generator.extension.resources.ResourceMethodExtension;
import org.raml.jaxrs.generator.extension.resources.ResponseClassExtension;
import org.raml.jaxrs.generator.extension.resources.ResponseMethodExtension;
import org.raml.jaxrs.generator.extension.types.FieldExtension;
import org.raml.jaxrs.generator.extension.types.MethodExtension;
import org.raml.jaxrs.generator.extension.types.TypeExtension;
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

  /*
   * Resources.
   */
  public static Annotations<ResourceClassExtension<GResource>> ON_RESOURCE_CLASS_CREATION =
      new Annotations<ResourceClassExtension<GResource>>() {

        @Override
        public ResourceClassExtension<GResource> get(Annotable target, Annotable... others) {
          List<String> classNames = getWithDefault("resources", "onResourceClassCreation", null, target, others);

          List<ResourceClassExtension<GResource>> extension = createExtension(classNames);
          return new ResourceClassExtension.Composite(extension);
        }
      };

  public static Annotations<ResourceClassExtension<GResource>> ON_RESOURCE_CLASS_FINISH =
      new Annotations<ResourceClassExtension<GResource>>() {

        @Override
        public ResourceClassExtension<GResource> get(Annotable target, Annotable... others) {
          List<String> classNames = getWithDefault("resources", "onResourceClassFinish", null, target, others);

          List<ResourceClassExtension<GResource>> extension = createExtension(classNames);
          return new ResourceClassExtension.Composite(extension);
        }
      };

  public static Annotations<ResourceMethodExtension<GMethod>> ON_METHOD_CREATION =
      new Annotations<ResourceMethodExtension<GMethod>>() {

        @Override
        public ResourceMethodExtension<GMethod> get(Annotable target, Annotable... others) {

          List<String> classNames = getWithDefault("methods", "onResourceMethodCreation", null, target, others);

          List<ResourceMethodExtension<GMethod>> extension = createExtension(classNames);
          return new ResourceMethodExtension.Composite(extension);
        }
      };

  public static Annotations<ResourceMethodExtension<GMethod>> ON_METHOD_FINISH =
      new Annotations<ResourceMethodExtension<GMethod>>() {

        @Override
        public ResourceMethodExtension<GMethod> get(Annotable target, Annotable... others) {
          List<String> classNames = getWithDefault("methods", "onResourceMethodFinish", null, target, others);

          List<ResourceMethodExtension<GMethod>> extension = createExtension(classNames);
          return new ResourceMethodExtension.Composite(extension);
        }
      };



  public static Annotations<ResponseClassExtension<GMethod>> ON_RESPONSE_CLASS_CREATION =
      new Annotations<ResponseClassExtension<GMethod>>() {

        @Override
        public ResponseClassExtension<GMethod> get(Annotable target, Annotable... others) {
          List<String> classNames = getWithDefault("methods", "onResponseClassCreation", null, target, others);

          List<ResponseClassExtension<GMethod>> extension = createExtension(classNames);
          return new ResponseClassExtension.Composite(extension);
        }
      };

  public static Annotations<ResponseClassExtension<GMethod>> ON_RESPONSE_CLASS_FINISH =
      new Annotations<ResponseClassExtension<GMethod>>() {

        @Override
        public ResponseClassExtension<GMethod> get(Annotable target, Annotable... others) {
          List<String> classNames = getWithDefault("methods", "onResponseClassFinish", null, target, others);

          List<ResponseClassExtension<GMethod>> extension = createExtension(classNames);
          return new ResponseClassExtension.Composite(extension);
        }
      };



  public static Annotations<ResponseMethodExtension<GResponse>> ON_RESPONSE_METHOD_CREATION =
      new Annotations<ResponseMethodExtension<GResponse>>() {

        @Override
        public ResponseMethodExtension<GResponse> get(Annotable target, Annotable... others) {

          List<String> classNames = getWithDefault("responses", "onResponseMethodCreation", null, target, others);

          List<ResponseMethodExtension<GResponse>> extension = createExtension(classNames);
          return new ResponseMethodExtension.Composite(extension);
        }
      };

  public static Annotations<ResponseMethodExtension<GResponse>> ON_RESPONSE_METHOD_FINISH =
      new Annotations<ResponseMethodExtension<GResponse>>() {

        @Override
        public ResponseMethodExtension<GResponse> get(Annotable target, Annotable... others) {
          List<String> classNames = getWithDefault("responses", "onResponseMethodFinish", null, target, others);

          List<ResponseMethodExtension<GResponse>> extension = createExtension(classNames);
          return new ResponseMethodExtension.Composite(extension);
        }
      };


  /*
   * Types
   */
  public static Annotations<TypeExtension> ON_TYPE_CLASS_CREATION = new Annotations<TypeExtension>() {

    @Override
    public TypeExtension get(Annotable target, Annotable... others) {
      List<String> classNames = getWithDefault("types", "onTypeCreation", null, target, others);

      List<TypeExtension> extension = createExtension(classNames);
      return new TypeExtension.TypeExtensionComposite(extension);
    }
  };

  public static Annotations<TypeExtension> ON_TYPE_CLASS_FINISH = new Annotations<TypeExtension>() {

    @Override
    public TypeExtension get(Annotable target, Annotable... others) {
      List<String> classNames = getWithDefault("types", "onTypeFinish", null, target, others);

      List<TypeExtension> extension = createExtension(classNames);
      return new TypeExtension.TypeExtensionComposite(extension);
    }
  };

  public static Annotations<FieldExtension> ON_TYPE_FIELD_CREATION = new Annotations<FieldExtension>() {

    @Override
    public FieldExtension get(Annotable target, Annotable... others) {
      String className = getWithDefault("types", "onFieldCreation", null, target, others);
      return createExtension(className, FieldExtension.NULL_FIELD_EXTENSION);
    }
  };

  public static Annotations<FieldExtension> ON_TYPE_FIELD_FINISH = new Annotations<FieldExtension>() {

    @Override
    public FieldExtension get(Annotable target, Annotable... others) {
      String className = getWithDefault("types", "onFieldFinish", null, target, others);
      return createExtension(className, FieldExtension.NULL_FIELD_EXTENSION);
    }
  };

  public static Annotations<MethodExtension> ON_TYPE_METHOD_CREATION = new Annotations<MethodExtension>() {

    @Override
    public MethodExtension get(Annotable target, Annotable... others) {
      String className = getWithDefault("types", "onMethodCreation", null, target, others);
      return createExtension(className, MethodExtension.NULL_METHOD_EXTENSION);
    }
  };

  public static Annotations<MethodExtension> ON_TYPE_METHOD_FINISH = new Annotations<MethodExtension>() {

    @Override
    public MethodExtension get(Annotable target, Annotable... others) {
      String className = getWithDefault("types", "onMethodFinish", null, target, others);
      return createExtension(className, MethodExtension.NULL_METHOD_EXTENSION);
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

  private static <T> List<T> createExtension(List<String> classNames) {
    if (classNames == null) {

      return Collections.emptyList();
    } else {

      return Lists.transform(classNames, new Function<String, T>() {

        @Nullable
        @Override
        public T apply(@Nullable String className) {

          try {
            return (T) Class.forName(className).newInstance();
          } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new GenerationException("Cannot find resource creation extension: " + className);
          }
        }
      });
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
