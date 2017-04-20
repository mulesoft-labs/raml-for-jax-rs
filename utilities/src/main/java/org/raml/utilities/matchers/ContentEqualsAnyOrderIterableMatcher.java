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
package org.raml.utilities.matchers;

import com.google.common.collect.Iterables;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

class ContentEqualsAnyOrderIterableMatcher<T> extends TypeSafeMatcher<Iterable<T>> {

  private final Iterable<T> iterable;

  private ContentEqualsAnyOrderIterableMatcher(Iterable<T> iterable) {
    this.iterable = iterable;
  }

  public static <T> ContentEqualsAnyOrderIterableMatcher<T> create(Iterable<T> iterable) {
    checkNotNull(iterable);

    return new ContentEqualsAnyOrderIterableMatcher<>(iterable);
  }

  @Override
  protected boolean matchesSafely(Iterable<T> toMatch) {
    Map<T, Integer> itemsOcurrences = new HashMap<>(Iterables.size(iterable));

    for (T item : iterable) {
      int itemOcurrences = 1;

      if (itemsOcurrences.containsKey(item)) {
        itemOcurrences = itemsOcurrences.get(item) + 1;
      }

      itemsOcurrences.put(item, itemOcurrences);
    }

    for (T item : toMatch) {
      if (!itemsOcurrences.containsKey(item)) { // Not in the current iterable.
        return false;
      }

      int itemOccurrences = itemsOcurrences.get(item) - 1;

      if (itemOccurrences == 0) {
        itemsOcurrences.remove(item);
      } else {
        itemsOcurrences.put(item, itemOccurrences);
      }
    }


    return itemsOcurrences.isEmpty();
  }

  @Override
  public void describeTo(Description description) {

  }
}
