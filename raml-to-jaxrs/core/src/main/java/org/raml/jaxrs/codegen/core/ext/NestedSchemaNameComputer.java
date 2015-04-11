/*
 * Copyright 2013-2015 (c) MuleSoft, Inc.
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
package org.raml.jaxrs.codegen.core.ext;

import org.raml.model.MimeType;

/**
 *
 * Created by Pavel Petrochenko on 12/04/15.
 */
public interface NestedSchemaNameComputer extends GeneratorExtension{

    /**
     *
     * @param mime mime type
     * @return null if nested schema name can not be computed by this extension, or null
     */
    String computeNestedSchemaName(MimeType mime);
}
