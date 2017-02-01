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
package org.raml.jaxrs.generator;

/**
 * Created by Jean-Philippe Belanger on 1/15/17. Just potential zeroes and ones
 */
public interface NameFixer {

  NameFixer CAMEL_LOWER = new NameFixer() {

    @Override
    public String fixFirst(String name) {

      return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    @Override
    public String fixOthers(String name) {
      return CAMEL_UPPER.fixOthers(name);
    }
  };

  NameFixer CAMEL_UPPER = new NameFixer() {

    @Override
    public String fixFirst(String name) {

      return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    @Override
    public String fixOthers(String name) {
      return fixFirst(name);
    }
  };

  NameFixer ALL_UPPER = new NameFixer() {

    @Override
    public String fixFirst(String name) {

      return name.toUpperCase();
    }

    @Override
    public String fixOthers(String name) {
      return name.toUpperCase();
    }
  };

  String fixFirst(String name);

  String fixOthers(String name);

}
