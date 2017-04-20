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
package org.raml.jaxrs.parser.util;

import com.google.common.collect.FluentIterable;

import org.omg.PortableServer.THREAD_POLICY_ID;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.concurrent.Callable;

public class ClassLoaderUtils {

  private ClassLoaderUtils() {}

  public static ClassLoader classLoaderFor(URL firstUrl, URL... theRest) {

    URL[] allOfDem = FluentIterable.of(theRest).append(firstUrl).toArray(URL.class);

    // In the absence of specific parent, we use the current one as the parent.
    // Otherwise, some incongruities might happen when running from the maven
    // plugin for example.
    return new URLClassLoader(allOfDem, Thread.currentThread().getContextClassLoader());
  }

  public static ClassLoader classLoaderFor(Path path) throws MalformedURLException {
    return classLoaderFor(path.toUri().toURL());
  }
}
