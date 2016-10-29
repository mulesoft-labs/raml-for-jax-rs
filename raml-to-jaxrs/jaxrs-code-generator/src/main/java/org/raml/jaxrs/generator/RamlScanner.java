package org.raml.jaxrs.generator;


import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;

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

    public void handle(InputStream stream) throws GenerationException {

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

    public void handle(org.raml.v2.api.model.v10.api.Api api) {



    }

    public void handle(org.raml.v2.api.model.v08.api.Api api) {

    }

}
