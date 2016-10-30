package org.raml.jaxrs.generator;


import org.raml.jaxrs.generator.v10.ResourceHandler;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.resources.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;


/**
 * Created by Jean-Philippe Belanger on 10/26/16.
 * Just potential zeroes and ones
 */
public class RamlScanner {

    public void handle(String resourceName) throws IOException, GenerationException {

        handle(RamlScanner.class.getResource(resourceName));
    }

    public void handle(URL resourceName) throws IOException, GenerationException {

        handle(resourceName.openStream());
    }

    public void handle(InputStream stream) throws GenerationException, IOException {

        RamlModelResult result = new RamlModelBuilder().buildApi(new InputStreamReader(stream), ".");
        if ( result.hasErrors() ) {
            throw new GenerationException(result.getValidationResults());
        }

        if ( result.isVersion08() ) {
            handle(result.getApiV08());
        } else {
            handle(result.getApiV10());
        }
    }

    public void handle(org.raml.v2.api.model.v10.api.Api api) throws IOException {

        CurrentBuild build = new CurrentBuild("jp.fun");
        for (Resource resource : api.resources()) {
            ResourceHandler handler = new ResourceHandler();
            handler.handle(build, api, resource);
        }

        build.generate("/tmp/fun");
    }

    public void handle(org.raml.v2.api.model.v08.api.Api api) {

    }

}
