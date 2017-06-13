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
package server.zoo;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.moxy.xml.MoxyXmlFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.raml.test.model.Animal;
import org.raml.test.model.AnimalImpl;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by Jean-Philippe Belanger on 11/3/16. Just potential zeroes and ones
 */
public class StartServer {

  public static void main(String[] args) throws Exception {

    URI baseUri = UriBuilder.fromUri("http://localhost/").port(9998).build();
    ResourceConfig config = new ResourceConfig(AnimalsImpl.class)
        .packages("example.model")
        .register(
                  new MoxyXmlFeature(
                                     new HashMap<String, Object>(), null, true,
                                     Animal.class, AnimalImpl.class // Classes to be bound.
                  )
        );
    Server server = JettyHttpContainerFactory.createServer(baseUri, config);
    server.start();
  }
}
