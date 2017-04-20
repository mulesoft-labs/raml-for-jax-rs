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
package org.raml.jaxrs.parser.gatherers;

import java.util.Set;

/**
 * An interface to define objects whose role is to extract the set of JaxRs related classes.
 */
public interface JaxRsClassesGatherer {

  /**
   * @return The set of JaxRs related classes. How those are extracted and from where depends entirely on the implementation.
   */
  Set<Class<?>> jaxRsClasses();
}
