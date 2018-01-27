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
import com.google.common.collect.FluentIterable;
import org.raml.jaxrs.common.RamlGenerator;
import org.raml.pojotoraml.RamlAdjuster;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class PojoToRamlExtensionFactory {

  public static RamlAdjuster createAdjusters(Class<?> clazz, RamlAdjuster... ramlAdjusters) {

    RamlGenerator generator = clazz.getAnnotation(RamlGenerator.class);
    if (generator != null) {
      return new RamlAdjuster.Composite(FluentIterable.of(generator.adjuster())
          .transform(new Function<Class<? extends RamlAdjuster>, RamlAdjuster>() {

            @Nullable
            @Override
            public RamlAdjuster apply(@Nullable Class<? extends RamlAdjuster> aClass) {
              try {
                return aClass.newInstance();
              } catch (InstantiationException | IllegalAccessException e) {

                throw new IllegalArgumentException(e);
              }
            }
          }).append(ramlAdjusters).toList());
    } else {

      return new RamlAdjuster.Composite(Arrays.asList(ramlAdjusters));
    }

  }
}
