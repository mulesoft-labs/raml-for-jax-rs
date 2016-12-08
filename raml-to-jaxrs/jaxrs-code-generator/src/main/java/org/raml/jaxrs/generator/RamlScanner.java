package org.raml.jaxrs.generator;


import org.raml.jaxrs.generator.v10.ResourceHandler;
import org.raml.jaxrs.generator.v10.TypeFactory;
import org.raml.jaxrs.generator.v10.TypeFinder;
import org.raml.jaxrs.generator.v10.TypeUtils;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.JSONTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.XMLTypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;


/**
 * Created by Jean-Philippe Belanger on 10/26/16.
 * Just potential zeroes and ones
 */
public class RamlScanner {

    private final String destDir;
    private final String packageName;

    public RamlScanner(String destDir, String packageName) {
        this.destDir = destDir;
        this.packageName = packageName;
    }

    public void handle(String resourceName) throws IOException, GenerationException {

        handle(RamlScanner.class.getResource(resourceName), ".");
    }

    public void handle(String resourceName, String directory) throws IOException, GenerationException {

        handle(RamlScanner.class.getResource(resourceName), directory);
    }

    public void handle(File resource) throws IOException, GenerationException {

        handle(new FileInputStream(resource), resource.getParentFile().getAbsolutePath() + "/");
    }

    public void handle(URL resourceName, String directory) throws IOException, GenerationException {

        handle(resourceName.openStream(), directory);
    }

    public void handle(InputStream stream, String directory) throws GenerationException, IOException {

        RamlModelResult result = new RamlModelBuilder().buildApi(new InputStreamReader(stream), directory);
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

        CurrentBuild build = new CurrentBuild(new TypeFinder(), packageName);
        build.constructClasses(api, new TypeFactory(build));

        ResourceHandler resourceHandler = new ResourceHandler(build);

        // handle resources.
        for (Resource resource : api.resources()) {
            resourceHandler.handle(api, resource);
        }

        build.generate(destDir);
    }


    public void handle(org.raml.v2.api.model.v08.api.Api api) {

    }

}
