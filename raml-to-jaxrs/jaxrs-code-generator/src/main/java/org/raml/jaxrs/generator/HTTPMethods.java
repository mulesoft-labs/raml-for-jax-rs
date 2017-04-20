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

import com.google.common.collect.ImmutableMap;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 11/3/16. Just potential zeroes and ones
 */
public class HTTPMethods {

  private static Map<String, Class<? extends Annotation>> nameToAnnotation = ImmutableMap.of("put",
                                                                                             PUT.class, "get", GET.class, "post",
                                                                                             POST.class, "delete", DELETE.class);

  public static Class<? extends Annotation> methodNameToAnnotation(String name) {

    String s = name.toLowerCase();
    return nameToAnnotation.get(s);
  }
}
