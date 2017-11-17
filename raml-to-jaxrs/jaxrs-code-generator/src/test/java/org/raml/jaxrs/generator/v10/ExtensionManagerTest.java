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
package org.raml.jaxrs.generator.v10;

import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created. There, you have it.
 */
public class ExtensionManagerTest {

  @Test
  public void twoIdenticalExtensions() throws Exception {

    ExtensionManager em = ExtensionManager.createExtensionManager("org/raml/jaxrs/generator/v10/test1.properties");
    Iterable<Class> list = em.getClassesForName("core.one");

    assertEquals(org.raml.jaxrs.generator.builders.extensions.resources.TrialResourceClassExtension.class, list.iterator().next());
  }

  @Test
  public void twoClasses() throws Exception {

    ExtensionManager em = ExtensionManager.createExtensionManager("org/raml/jaxrs/generator/v10/test1.properties");
    Iterable<Class> list1 = em.getClassesForName("core.sub.two");
    Iterator<Class> iterator = list1.iterator();
    assertEquals(org.raml.jaxrs.generator.builders.extensions.resources.TrialResponseClassExtension.class, iterator.next());
    assertEquals(org.raml.jaxrs.generator.builders.extensions.resources.TrialResponseMethodExtension.class, iterator.next());
  }

  @Test
  public void duplicate() throws Exception {

    ExtensionManager em = ExtensionManager.createExtensionManager("org/raml/jaxrs/generator/v10/test1.properties");
    Set<Class> list1 = em.getClassesForName("core.sub.duplicate");
    Iterator<Class> iterator = list1.iterator();

    assertEquals(1, list1.size());
    assertEquals(org.raml.jaxrs.generator.builders.extensions.resources.TrialResponseClassExtension.class, iterator.next());
  }

}
