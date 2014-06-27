/*
 * Copyright (c) MuleSoft, Inc.
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
package org.raml.parser.loader;

import java.io.InputStream;

public class CompositeResourceLoader implements ResourceLoader
{

    private ResourceLoader[] resourceLoaders;

    public CompositeResourceLoader(ResourceLoader... resourceLoaders)
    {
        this.resourceLoaders = resourceLoaders;
    }

    @Override
    public InputStream fetchResource(String resourceName)
    {
        InputStream inputStream = null;
        for (ResourceLoader loader : resourceLoaders)
        {
            inputStream = loader.fetchResource(resourceName);
            if (inputStream != null)
            {
                break;
            }
        }
        return inputStream;
    }
}
