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
package org.raml.jaxrs.parser.analyzers;

import org.raml.jaxrs.model.JaxRsApplication;

/**
 * {@link Analyzer}s are classes that are meant to make sense of a given configuration.
 *
 * Meaning, for example, an analyzer is meant to construct a {@link JaxRsApplication} from a set of files, or classes.
 */
public interface Analyzer {

  /**
   * @return A {@link JaxRsApplication} constructed by the specified implementation configuration.
   */
  JaxRsApplication analyze();
}
