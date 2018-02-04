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
package org.raml.jaxrs.examples.resources;

import org.raml.jaxrs.common.Example;
import org.raml.jaxrs.common.Examples;
import org.raml.pojotoraml.plugins.RamlGenerator;
import org.raml.pojotoraml.plugins.RamlGeneratorPlugin;
import org.raml.jaxrs.examples.Secure;
import org.raml.jaxrs.handlers.BeanLikeClassParser;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.UUID;

/**
 * Created by Jean-Philippe Belanger on 3/26/17. Just potential zeroes and ones
 */
@XmlRootElement
@Secure(security = String.class, level = 17)
@RamlGenerator(
    parser = BeanLikeClassParser.class,
    plugins = {@RamlGeneratorPlugin(plugin = "core.changeTypeName", parameters = {"MyValue"})})
public interface HierarchyValue extends TopValue, AnotherTopValue {

  @Examples({
      @Example("qqchose"),
      @Example("qqchose d'autre")
  })
  UUID getUUID();

  String getName();

  int getId();

  SubType getSubType();

  List<String> getNames();

}
