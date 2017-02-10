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

import com.google.common.collect.Lists;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

public class HttpVerbTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void testVerbs() {
    for (HttpVerb verb : HttpVerb.values()) {
      switch (verb) {
        case DELETE:
          assertEquals("DELETE", verb.getString());
          break;
        case GET:
          assertEquals("GET", verb.getString());
          break;
        case HEAD:
          assertEquals("HEAD", verb.getString());
          break;
        case OPTIONS:
          assertEquals("OPTIONS", verb.getString());
          break;
        case PATCH:
          assertEquals("PATCH", verb.getString());
          break;
        case POST:
          assertEquals("POST", verb.getString());
          break;
        case PUT:
          assertEquals("PUT", verb.getString());
          break;
        default:
          throw new RuntimeException("extend this classe's tests with the missing item");
      }
    }
  }

  @Test
  public void testFromStringValidValues() {
    Map<String, HttpVerb> expectedPairings = new HashMap<>(HttpVerb.values().length);

    for (HttpVerb verb : HttpVerb.values()) {
      expectedPairings.put(verb.getString(), verb);
    }


    for (Map.Entry<String, HttpVerb> entry : expectedPairings.entrySet()) {
      HttpVerb verb = entry.getValue();
      String string = entry.getKey();
      assertSame(verb, HttpVerb.fromString(string).get());
      assertSame(verb, HttpVerb.fromStringUnchecked(string));
    }
  }

  @Test
  public void testFromStringInvalidValues() {
    Iterable<String> invalidValues =
        Lists.newArrayList("header", "glock", "fackalacabeza", "put", "poute");

    for (String value : invalidValues) {
      expectedException.expect(NoSuchElementException.class);
      HttpVerb.fromStringUnchecked(value);
      expectedException = ExpectedException.none();
      assertFalse(HttpVerb.fromString(value).isPresent());
    }
  }

}
