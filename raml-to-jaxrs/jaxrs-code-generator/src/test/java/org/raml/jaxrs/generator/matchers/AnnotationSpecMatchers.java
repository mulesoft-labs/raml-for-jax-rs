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
package org.raml.jaxrs.generator.matchers;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.CodeBlock;
import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 3/4/17. Just potential zeroes and ones
 */
public class AnnotationSpecMatchers {

  public static Matcher<AnnotationSpec> hasMember(final String member) {

    return new TypeSafeMatcher<AnnotationSpec>() {

      @Override
      protected boolean matchesSafely(AnnotationSpec item) {
        return item.members.containsKey(member);
      }

      @Override
      public void describeTo(Description description) {

        description.appendText("has member " + member);
      }
    };
  }

  public static Matcher<AnnotationSpec> member(final String member, Matcher<Iterable<? extends CodeBlock>> memberMatcher) {

    return new FeatureMatcher<AnnotationSpec, Iterable<? extends CodeBlock>>(memberMatcher, "member", "member") {

      @Override
      protected Iterable<? extends CodeBlock> featureValueOf(AnnotationSpec actual) {

        return actual.members.get(member);
      }
    };
  }
}
