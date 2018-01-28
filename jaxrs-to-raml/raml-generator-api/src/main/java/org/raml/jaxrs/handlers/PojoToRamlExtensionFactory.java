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
import org.raml.ramltopojo.plugin.PluginManager;
import org.raml.jaxrs.common.RamlGenerator;
import org.raml.jaxrs.common.RamlGeneratorPlugin;
import org.raml.pojotoraml.RamlAdjuster;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created. There, you have it.
 */
public class PojoToRamlExtensionFactory {

  private static PluginManager pluginManager = PluginManager.createPluginManager("META-INF/jaxrstoraml.properties");

  public static RamlAdjuster createAdjusters(Class<?> clazz, final RamlAdjuster... ramlAdjusters) {

    RamlGenerator generator = clazz.getAnnotation(RamlGenerator.class);
    if (generator != null) {
      return new RamlAdjuster.Composite(FluentIterable.of(generator.plugins())
          .transform(new Function<RamlGeneratorPlugin, RamlAdjuster>() {

            @Nullable
            @Override
            public RamlAdjuster apply(@Nullable RamlGeneratorPlugin ramlGeneratorPlugin) {
              Set<RamlAdjuster> adjuster =
                  pluginManager.getClassesForName(ramlGeneratorPlugin.plugin(),
                                                  Arrays.asList(ramlGeneratorPlugin.parameters()), RamlAdjuster.class);
              return new RamlAdjuster.Composite(adjuster);
            }
          }).append(ramlAdjusters).toList());
    } else {

      return new RamlAdjuster.Composite(Arrays.asList(ramlAdjusters));
    }

  }
}
