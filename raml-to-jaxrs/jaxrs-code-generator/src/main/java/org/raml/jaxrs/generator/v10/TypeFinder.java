package org.raml.jaxrs.generator.v10;

import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.api.Library;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jean-Philippe Belanger on 12/6/16.
 * Just potential zeroes and ones
 */
public class TypeFinder {

    public TypeFinder findTypes(Api api, TypeFinderListener listener) {

        if ( api.uses() != null ) {

            goThroughLibraries(new HashSet<String>(), api.uses(), listener);
        }

        localTypes(api.types(), listener);
        resourceTypes(api.resources(), listener);

        return this;
    }

    private void resourceTypes(List<Resource> resources, TypeFinderListener listener) {

        for (Resource resource : resources) {

            resourceTypes(resource.resources(), listener);
            for (Method method : resource.methods()) {

                typesInBodies(resource, method, method.body(), listener);
            }
        }
    }

    private void typesInBodies(Resource resource, Method method, List<TypeDeclaration> body, TypeFinderListener listener) {
        for (TypeDeclaration typeDeclaration : body) {

            listener.newType(resource, method, typeDeclaration);
        }

        for (Response response : method.responses()) {
            for (TypeDeclaration typeDeclaration : response.body()) {
                listener.newType(resource, method, response, typeDeclaration);
            }
        }
    }

    private void localTypes(List<TypeDeclaration> types, TypeFinderListener listener) {

        for (TypeDeclaration typeDeclaration : types) {

            listener.newType(typeDeclaration);
       }
    }

    private void goThroughLibraries(Set<String> visitedLibraries, List<Library> libraries, TypeFinderListener listener) {

        for (Library library : libraries) {
            if (visitedLibraries.contains(library.name())) {

                continue;
            } else {

                visitedLibraries.add(library.name());
            }

            goThroughLibraries(visitedLibraries, library.uses(), listener);
            for (TypeDeclaration typeDeclaration : library.types()) {

                listener.newType(typeDeclaration);
            }
        }

    }

    public static void main(String[] args) {

        showTypes("/home/ebeljea/LocalProjects/raml-for-jax-rs-fork/raml-to-jaxrs/jaxrs-generated-example/src/main/resources/world-music-api/api.raml");
        System.err.println("===========================================");
    //    showTypes("/home/ebeljea/LocalProjects/raml-for-jax-rs-fork/raml-to-jaxrs/jaxrs-generated-example/src/main/resources/marketing-cloud/api.raml");
    }

    private static void showTypes(String ramlLocation) {
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(ramlLocation);
        if (ramlModelResult.hasErrors())
        {
            for (ValidationResult validationResult : ramlModelResult.getValidationResults())
            {
                System.out.println(validationResult.getMessage());
            }
        }
        else
        {
            Api api = ramlModelResult.getApiV10();
            new TypeFinder().findTypes(api, new TypeFinderListener() {
                @Override
                public void newType(TypeDeclaration typeDeclaration) {

                    System.err.println("Type: " + typeDeclaration.getClass().getInterfaces()[0] + " -> " + typeDeclaration.name() + ":" + typeDeclaration.type());
                }

                @Override
                public void newType(Resource resource, Method method, Response response, TypeDeclaration typeDeclaration) {

                    System.err.println("Type: " + typeDeclaration.getClass().getInterfaces()[0] + " -> " + typeDeclaration.name() + ":" + typeDeclaration.type());
                }

                @Override
                public void newType(Resource resource, Method method, TypeDeclaration typeDeclaration) {

                    System.err.println("Type: " + typeDeclaration.getClass().getInterfaces()[0] + " -> " + typeDeclaration.name() + ":" + typeDeclaration.type());
                }
            });
        }
    }
}
