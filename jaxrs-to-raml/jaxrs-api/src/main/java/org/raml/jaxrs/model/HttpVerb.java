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
package org.raml.jaxrs.model;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import java.util.NoSuchElementException;

import static java.lang.String.format;

/**
 * An enum representing valid possible http verbs.
 */
public enum HttpVerb {
  GET("GET"), PUT("PUT"), POST("POST"), HEAD("HEAD"), OPTIONS("OPTIONS"), PATCH("PATCH"), DELETE(
      "DELETE");

  private static final ImmutableMap<String, HttpVerb> VERBS_BY_STRINGS;

  static {
    ImmutableMap.Builder<String, HttpVerb> tempMap = ImmutableMap.builder();

    for (HttpVerb verb : HttpVerb.values()) {
      tempMap.put(verb.getString(), verb);
    }

    VERBS_BY_STRINGS = tempMap.build();
  }

  private final String string;

  HttpVerb(String string) {
    this.string = string;
  }

  /**
   * @return The string representation of the verb, as seen in an HTTP message (upper case)
   */
  public String getString() {
    return string;
  }

  /**
   * Returns the corresponding http verb.
   *
   * Verbs are associated with their {@link #getString()} value. Therefore, the matching is case sensitive.
   *
   * @param httpMethod the method to pair
   * @return The corresponding http verb, or {@link Optional#absent()} if there are no matches.
   */
  public static Optional<HttpVerb> fromString(String httpMethod) {
    return Optional.fromNullable(VERBS_BY_STRINGS.get(httpMethod));
  }

  /**
   * Same as {@link #fromString(String)}, but throws if there is no corresponding verb.
   *
   * @param httpMethod the method to pair
   * @return the corresponding http verb
   * @throws NoSuchElementException if there is no corresponding verb
   */
  public static HttpVerb fromStringUnchecked(String httpMethod) {
    Optional<HttpVerb> verb = fromString(httpMethod);

    if (verb.isPresent())
      return verb.get();

    throw new NoSuchElementException(String.format("invalid http method: %s", httpMethod));
  }
}
