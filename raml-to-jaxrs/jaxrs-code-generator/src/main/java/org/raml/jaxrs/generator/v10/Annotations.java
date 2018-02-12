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
import org.raml.jaxrs.generator.GAbstraction;
import org.raml.ramltopojo.PluginDef;
import org.raml.v2.api.model.v10.api.Api;
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

      return getWithDefault("types", "className", null, target, others);
    }
  };


  public static Annotations<List<PluginDef>> RESOURCE_PLUGINS = new Annotations<List<PluginDef>>() {

    @Override
    public List<PluginDef> getWithContext(CurrentBuild currentBuild, Annotable target, Annotable... others) {
      return Annotations.getWithDefault(new TypeInstanceToPluginDefFunction(), "resources", "plugins",
                                        Collections.<PluginDef>emptyList(),
                                        target, others);
    }
  };

  public static Annotations<List<PluginDef>> METHOD_PLUGINS = new Annotations<List<PluginDef>>() {

    @Override
    public List<PluginDef> getWithContext(CurrentBuild currentBuild, Annotable target, Annotable... others) {
      return Annotations.getWithDefault(new TypeInstanceToPluginDefFunction(), "methods", "plugins",
                                        Collections.<PluginDef>emptyList(),
                                        target, others);
    }
  };

  public static Annotations<List<PluginDef>> RESPONSE_PLUGINS = new Annotations<List<PluginDef>>() {

    @Override
    public List<PluginDef> getWithContext(CurrentBuild currentBuild, Annotable target, Annotable... others) {
      return Annotations.getWithDefault(new TypeInstanceToPluginDefFunction(), "responses", "plugins",
                                        Collections.<PluginDef>emptyList(),
                                        target, others);
    }
  };

  public static Annotations<List<PluginDef>> RESPONSE_CLASS_PLUGINS = new Annotations<List<PluginDef>>() {

    @Override
    public List<PluginDef> getWithContext(CurrentBuild currentBuild, Annotable target, Annotable... others) {
      return Annotations.getWithDefault(new TypeInstanceToPluginDefFunction(), "responseClasses", "plugins",
                                        Collections.<PluginDef>emptyList(),
                                        target, others);
    }
  };

  private static class TypeInstanceToPluginDefFunction implements Function<TypeInstance, PluginDef> {

    @Override
    public PluginDef apply(@Nullable TypeInstance input) {

      if (input.properties().size() == 0) {

        return new PluginDef((String) input.value(), Collections.<String>emptyList());
      } else {
        if (input.properties().size() == 1) {

          return new PluginDef((String) input.properties().get(0).value().value(), Collections.<String>emptyList());
        } else {
          return new PluginDef((String) input.properties().get(0).value().value(), Lists.transform(input.properties().get(1)
              .values(), new Function<TypeInstance, String>() {

            @Nullable
            @Override
            public String apply(@Nullable TypeInstance input) {
              return (String) input.value();
            }
          }
              ));
        }
      }
    }
  }

  private static <T, R> R getWithDefault(Function<TypeInstance, T> convert, String annotationName, String propName, R def,
                                         Annotable target,
                                         Annotable... others) {
    R b = org.raml.ramltopojo.Annotations.evaluate(convert, annotationName, propName, target, others);
    if (b == null) {

      return def;
    } else {
      return b;
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

  public T get(T def, Annotable type, Annotable... others) {

    return getValueWithDefault(def, type, others);
  }

  // this is not pretty
  public T get(T def, Api api, GAbstraction... others) {

    if (api == null) {
      return def;
    }

    return getValueWithDefault(def, api, FluentIterable.of(others).transform(new Function<GAbstraction, Annotable>() {

      @Nullable
      @Override
      public Annotable apply(@Nullable GAbstraction o) {
        return (Annotable) o.implementation();
      }
    }).toArray(Annotable.class));
  }

}
