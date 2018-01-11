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

import org.raml.jaxrs.generator.ramltypes.GResource;
import org.raml.jaxrs.generator.v08.V08GResource;
import org.raml.jaxrs.generator.v08.V08TypeRegistry;
import org.raml.jaxrs.generator.v10.V10GResource;
import org.raml.v2.api.model.v08.resources.Resource;
import java.util.Set;

/**
 * Created by Jean-Philippe Belanger on 12/10/16. Just potential zeroes and ones
 */
public class GAbstractionFactory {

  public GResource newResource(CurrentBuild currentBuild,
                               final org.raml.v2.api.model.v10.resources.Resource resource) {

    return new V10GResource(currentBuild, this, resource);
  }

  public GResource newResource(CurrentBuild api, final GResource parent,
                               final org.raml.v2.api.model.v10.resources.Resource resource) {

    return new V10GResource(api, this, parent, resource);
  }


  public GResource newResource(Set<String> globalSchemas, V08TypeRegistry registry,
                               Resource resource) {
    return new V08GResource(this, resource, globalSchemas, registry);
  }

  public GResource newResource(Set<String> globalSchemas, V08TypeRegistry registry,
                               GResource parent, org.raml.v2.api.model.v08.resources.Resource resource) {
    return new V08GResource(this, parent, resource, globalSchemas, registry);
  }

}
