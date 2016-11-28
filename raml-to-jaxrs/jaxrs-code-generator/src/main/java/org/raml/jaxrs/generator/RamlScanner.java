package org.raml.jaxrs.generator;


import org.raml.jaxrs.generator.v10.ModelFixer;
import org.raml.jaxrs.generator.v10.ResourceHandler;
import org.raml.jaxrs.generator.v10.TypeHandler;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;


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

        CurrentBuild build = new CurrentBuild(packageName);

        TypeHandler typeHandler = new TypeHandler(build);
        ResourceHandler resourceHandler = new ResourceHandler(build, typeHandler);
        for (TypeDeclaration type: api.types()) {

            typeHandler.handle(api, type);
        }

        // Find types in resources.
        for (Resource resource : api.resources()) {

            findPrivateTypes(api, resource, typeHandler);
        }

        for (Resource resource : api.resources()) {
            resourceHandler.handle(api, resource);
        }

        build.generate(destDir);
    }

    private void findPrivateTypes(Api api, Resource resource, TypeHandler typeHandler) {
        for (Method method : resource.methods()) {

            for (TypeDeclaration typeDeclaration : method.body()) {

                typeHandler.handlePrivateType(api, resource, typeDeclaration);
            }

            for (Response response : method.responses()) {

                for (TypeDeclaration typeDeclaration : response.body()) {

                    typeHandler.handlePrivateType(api, resource, response, typeDeclaration);
                }
            }
        }

        for (Resource subresource : resource.resources()) {
            findPrivateTypes(api, subresource, typeHandler);
        }
    }


    public void handle(org.raml.v2.api.model.v08.api.Api api) {

    }

}
