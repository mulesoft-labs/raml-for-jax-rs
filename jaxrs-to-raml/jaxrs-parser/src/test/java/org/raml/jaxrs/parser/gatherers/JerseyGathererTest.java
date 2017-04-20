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
package org.raml.jaxrs.parser.gatherers;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

public class JerseyGathererTest {

  @Mock
  ResourceConfig resourceConfig;

  private JerseyGatherer jerseyGatherer;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    jerseyGatherer = new JerseyGatherer(resourceConfig);
  }


  @Test
  public void testConstructor() {
    assertSame(resourceConfig, jerseyGatherer.getResourceConfig());
  }
}
