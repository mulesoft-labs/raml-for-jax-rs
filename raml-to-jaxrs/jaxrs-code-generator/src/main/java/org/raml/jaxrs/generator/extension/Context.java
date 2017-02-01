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
package org.raml.jaxrs.generator.extension;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

/**
 * Created by Jean-Philippe Belanger on 1/14/17. Just potential zeroes and ones
 */
public interface Context {

  String getResourcePackage();

  String getModelPackage();

  String getSupportPackage();

  /**
   * Rename a method defined in a JavaPoet method builder.
   *
   * This creates an identical method builder with a new name.
   * 
   * @param builder
   * @param name
   * @return
   */
  MethodSpec.Builder rename(MethodSpec.Builder builder, String name);

  /**
   * Rename a class/interface defined in a JavaPoet type builder.
   *
   * This creates an identical method builder with a new name.
   * 
   * @param builder
   * @param name
   * @return
   */

  TypeSpec.Builder rename(TypeSpec.Builder builder, String name);
}
