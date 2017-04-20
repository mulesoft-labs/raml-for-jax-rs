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
package org.raml.utilities.iterables;

import com.google.common.base.Supplier;

import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;

class SupplierIterator<T> implements Iterator<T> {

  private final Supplier<? extends T> supplier;

  private SupplierIterator(Supplier<? extends T> supplier) {
    this.supplier = supplier;
  }

  public static <T> SupplierIterator<T> create(Supplier<? extends T> supplier) {
    checkNotNull(supplier);

    return new SupplierIterator(supplier);
  }

  @Override
  public boolean hasNext() {
    return true;
  }

  @Override
  public T next() {
    return supplier.get();
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("cannot remove on a " + this.getClass().getSimpleName());
  }
}
