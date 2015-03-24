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
package org.raml.jaxrs.example;

import java.io.Closeable;
import java.net.URI;
import java.util.Scanner;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.simple.SimpleContainerFactory;
import org.raml.jaxrs.example.impl.PresentationResourceImpl;

/**
 * <p>Main class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class Main
{
    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.lang.Exception if any.
     */
    @SuppressWarnings("resource")
    public static void main(final String[] args) throws Exception
    {
        final ResourceConfig config = new ResourceConfig();
        config.register(PresentationResourceImpl.class);
        config.register(MultiPartFeature.class);

        final Closeable simpleContainer = SimpleContainerFactory.create(new URI("http://0.0.0.0:8181"),
            config);

        System.out.println("Strike ENTER to stop...");
        new Scanner(System.in).nextLine();

        simpleContainer.close();

        System.out.println("Bye!");
        System.exit(0);
    }
}
