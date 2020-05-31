/*
 * Copyright 2013-2018 (c) MuleSoft, Inc.
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

import amf.client.model.Annotable;
import amf.client.model.domain.ArrayNode;
import amf.client.model.domain.DataNode;
import amf.client.model.domain.ObjectNode;
import amf.client.model.domain.ScalarNode;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GAbstraction;
import org.raml.ramltopojo.AnnotationEngine;
import org.raml.ramltopojo.AnnotationUser;
import org.raml.ramltopojo.Annotations;
import org.raml.ramltopojo.PluginDef;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Jean-Philippe Belanger on 1/2/17. Just potential zeroes and ones
 */
public abstract class JaxRsAnnotations<T> implements AnnotationUser<T> {

  public static JaxRsAnnotations<List<PluginDef>> RESOURCE_PLUGINS = new JaxRsAnnotations<List<PluginDef>>() {

    @Override
    public List<PluginDef> getWithContext(Annotable target, Annotable... others) {
      return AnnotationEngine.getWithDefaultList("ramltojaxrs.resources", JaxRsAnnotations::mapToPluginDefs, target, others);
    }
  };

  public static JaxRsAnnotations<List<PluginDef>> METHODS_PLUGINS = new JaxRsAnnotations<List<PluginDef>>() {

    @Override
    public List<PluginDef> getWithContext(Annotable target, Annotable... others) {
      return AnnotationEngine.getWithDefaultList("ramltojaxrs.methods", JaxRsAnnotations::mapToPluginDefs, target, others);
    }
  };

  public static JaxRsAnnotations<List<PluginDef>> RESPONSECLASSES_PLUGINS = new JaxRsAnnotations<List<PluginDef>>() {

    @Override
    public List<PluginDef> getWithContext(Annotable target, Annotable... others) {
      return AnnotationEngine
          .getWithDefaultList("ramltojaxrs.responseClasses", JaxRsAnnotations::mapToPluginDefs, target, others);
    }
  };

  public static JaxRsAnnotations<List<PluginDef>> RESPONSEMETHODS_PLUGINS = new JaxRsAnnotations<List<PluginDef>>() {

    @Override
    public List<PluginDef> getWithContext(Annotable target, Annotable... others) {
      return AnnotationEngine
          .getWithDefaultList("ramltojaxrs.responses", JaxRsAnnotations::mapToPluginDefs, target, others);
    }
  };

  private static List<PluginDef> mapToPluginDefs(ArrayNode arrayNode) {
    return Optional.ofNullable(arrayNode).orElse(new ArrayNode()).members().stream()
        .filter(n -> n instanceof ObjectNode)
        .map(n -> (ObjectNode) n)
        .map(on -> create(
                          ((ScalarNode) on.properties().get("name")).value().value(),
                          Optional.ofNullable(on.properties().get("arguments")).orElseGet(ArrayNode::new)))
        .collect(Collectors.toList());
  }

  private static PluginDef create(String name, DataNode dataNode) {

    if (dataNode instanceof ArrayNode) {
      return new PluginDef(name,
                           ((ArrayNode) dataNode).members()
                               .stream()
                               .filter(o -> o instanceof ScalarNode)
                               .map(o -> ((ScalarNode) o).value().value())
                               .collect(Collectors.toList()));
    } else {

      return new PluginDef(name,
                           ((ObjectNode) dataNode).properties()
                               .entrySet().stream()
                               .filter(e -> e.getValue() instanceof ScalarNode)
                               .collect(Collectors.toMap(Map.Entry::getKey, e -> ((ScalarNode) e.getValue()).value().value())));
    }
  }
}
