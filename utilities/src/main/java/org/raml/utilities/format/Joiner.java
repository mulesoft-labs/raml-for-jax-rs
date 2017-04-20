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
package org.raml.utilities.format;

import com.google.common.collect.Iterables;

public class Joiner {

  private final com.google.common.base.Joiner joiner;
  private final String prefix;
  private final String suffix;
  private final String ifEmpty;

  private Joiner(com.google.common.base.Joiner joiner, String prefix, String suffix, String ifEmpty) {
    this.joiner = joiner;
    this.prefix = prefix;
    this.suffix = suffix;
    this.ifEmpty = ifEmpty;
  }

  public static Joiner on(String separator) {
    return new Joiner(com.google.common.base.Joiner.on(separator), "", "", "");
  }

  public Joiner withPrefix(String prefix) {
    return new Joiner(this.joiner, prefix, this.suffix, this.ifEmpty);
  }

  public Joiner withSuffix(String suffix) {
    return new Joiner(this.joiner, this.prefix, suffix, this.ifEmpty);
  }

  public Joiner ifEmpty(String ifEmpty) {
    return new Joiner(this.joiner, this.prefix, this.suffix, ifEmpty);
  }

  public String join(Iterable<?> stuff) {
    if (stuff == null || Iterables.isEmpty(stuff)) {
      // TODO: if ifEmpty is not specified default to prefix + suffix instead.
      return ifEmpty;
    }

    StringBuilder builder = new StringBuilder(this.prefix);
    this.joiner.appendTo(builder, stuff);
    builder.append(suffix);
    return builder.toString();
  }


}
