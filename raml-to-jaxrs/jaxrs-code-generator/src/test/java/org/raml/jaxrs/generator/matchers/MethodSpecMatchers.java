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

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

/**
 * Created by Jean-Philippe Belanger on 3/4/17. Just potential zeroes and ones
 */
public class MethodSpecMatchers {

  public static Matcher<MethodSpec> methodName(Matcher<String> match) {

    return new FeatureMatcher<MethodSpec, String>(match, "method name", "method name") {

      @Override
      protected String featureValueOf(MethodSpec actual) {
        return actual.name;
      }
    };
  }

  public static Matcher<MethodSpec> codeContent(Matcher<String> match) {

    return new FeatureMatcher<MethodSpec, String>(match, "method content", "method content") {

      @Override
      protected String featureValueOf(MethodSpec actual) {
        return actual.code.toString();
      }
    };
  }

  public static Matcher<MethodSpec> parameters(Matcher<Iterable<? extends ParameterSpec>> memberMatcher) {

    return new FeatureMatcher<MethodSpec, Iterable<? extends ParameterSpec>>(memberMatcher, "parameter", "parameter") {

      @Override
      protected Iterable<? extends ParameterSpec> featureValueOf(MethodSpec actual) {

        return actual.parameters;
      }
    };
  }
}
