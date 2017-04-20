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
import org.raml.jaxrs.handlers.BeanLikeTypes;
import org.raml.jaxrs.common.RamlGenerator;
import org.raml.jaxrs.examples.Secure;

import javax.ws.rs.Path;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

import static org.raml.v2.api.model.v10.declarations.AnnotationTarget.Example;

/**
 * Created by Jean-Philippe Belanger on 3/26/17. Just potential zeroes and ones
 */
@XmlRootElement
@RamlGenerator(BeanLikeTypes.class)
@Secure(security = String.class, level = 17)
public interface HierarchyValue extends TopValue, AnotherTopValue {

  @Examples({
      @Example("qqchose"),
      @Example("qqchose d'autre")
  })
  String getName();

  int getId();

  SubType getSubType();

  List<String> getNames();

}
