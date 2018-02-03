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
/**
 * Created. There, you have it.
 */
@RamlGenerators({
    @RamlGeneratorForClass(
        forClass = UUID.class,
        generator = @RamlGenerator(parser = BeanLikeClassParser.class,
            plugins = {@RamlGeneratorPlugin(plugin = "core.changeType", parameters = {"java.util.UUID", "string"})})
    )
})
package org.raml.jaxrs.examples.resources;

import org.raml.jaxrs.common.RamlGenerator;
import org.raml.jaxrs.common.RamlGeneratorForClass;
import org.raml.jaxrs.common.RamlGeneratorPlugin;
import org.raml.jaxrs.common.RamlGenerators;
import org.raml.jaxrs.handlers.BeanLikeClassParser;

import java.util.UUID;