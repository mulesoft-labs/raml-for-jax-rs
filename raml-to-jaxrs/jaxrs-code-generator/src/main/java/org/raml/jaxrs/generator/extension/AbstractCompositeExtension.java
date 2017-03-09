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

import com.google.common.annotations.VisibleForTesting;

import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 3/9/17. Just potential zeroes and ones
 */
abstract public class AbstractCompositeExtension<T, B> {

  private List<T> elements;

  public AbstractCompositeExtension(List<T> elements) {
    this.elements = elements;
  }

  @VisibleForTesting
  public List<T> getElements() {
    return elements;
  }

  public B runList(B input, ElementJob<T, B> job) {

    B current = input;
    for (T element : elements) {
      current = job.doElement(element, current);
      if (current == null) {
        return null;
      }
    }

    return current;
  }

  protected interface ElementJob<T, B> {

    B doElement(T e, B builder);
  }
}
