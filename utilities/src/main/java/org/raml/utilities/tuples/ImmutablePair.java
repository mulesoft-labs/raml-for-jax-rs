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
package org.raml.utilities.tuples;

public class ImmutablePair<L, R> implements Pair<L, R> {

  private final L left;
  private final R right;

  private ImmutablePair(L left, R right) {
    this.left = left;
    this.right = right;
  }

  /**
   * @param left Left value, can be null
   * @param right Right value, can be null
   * @return a new {@link ImmutablePair} with the given fields.
   */
  public static <L, R> ImmutablePair<L, R> create(L left, R right) {
    return new ImmutablePair<>(left, right);
  }

  @Override
  public L getLeft() {
    return left;
  }

  @Override
  public R getRight() {
    return right;
  }
}
