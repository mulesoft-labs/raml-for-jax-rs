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
package org.raml.jaxrs.generator.builders.extensions.resources;

import com.squareup.javapoet.MethodSpec;
import org.raml.jaxrs.generator.extension.resources.ResourceContext;
import org.raml.jaxrs.generator.extension.resources.ResourceMethodExtension;
import org.raml.jaxrs.generator.ramltypes.GMethod;

/**
 * Created by Jean-Philippe Belanger on 1/6/17. Just potential zeroes and ones
 */
public class TrialResourceMethodExtension implements ResourceMethodExtension<GMethod> {

  @Override
  public MethodSpec.Builder onMethod(ResourceContext context, GMethod method, MethodSpec.Builder methodSpec) {
    return methodSpec;
  }


}
