package org.raml.jaxrs.generator.v10;

import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.api.Library;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 12/6/16.
 * Just potential zeroes and ones
 */
public class TypeFinder {

    private Map<String, TypeDeclaration> declarations = new HashMap<>();

    public<T> T getDeclarations(String name) {
        return (T) declarations.get(name);
    }

    public TypeFinder findTypes(Api api) {

        if ( api.uses() != null ) {

            goThroughLibraries(api.uses());
        }

        localTypes(api.types());
        resourceTypes(api.resources());

        return this;
    }

    private void resourceTypes(List<Resource> resources) {

        for (Resource resource : resources) {

            resourceTypes(resource.resources());
            for (Method method : resource.methods()) {

                typesInBodies(resource, method, method.body());
            }
        }
    }

    private void typesInBodies(Resource resource, Method method, List<TypeDeclaration> body) {
        for (TypeDeclaration typeDeclaration : body) {
            System.err.println("ResourceBodyType: " + resource.resourcePath() + " " + method.method() + ":" + typeDeclaration.getClass().getInterfaces()[0] + " -> " + typeDeclaration.name() + ":" + typeDeclaration.type());
        }

        for (Response response : method.responses()) {
            for (TypeDeclaration typeDeclaration : response.body()) {
                System.err.println("ResourceResponseType: " + resource.resourcePath() + " " + method.method() + "(" + response.code().value() + "):" + typeDeclaration.getClass().getInterfaces()[0] + " -> " + typeDeclaration.name() + ":" + typeDeclaration.type());
            }
        }
    }

    private void localTypes(List<TypeDeclaration> types) {

        for (TypeDeclaration typeDeclaration : types) {

            System.err.println("Type: " + typeDeclaration.getClass().getInterfaces()[0] + " -> " + typeDeclaration.name() + ":" + typeDeclaration.type());
            declarations.put(typeDeclaration.name(), typeDeclaration);
        }
    }

    private void goThroughLibraries(List<Library> libraries) {

        for (Library library : libraries) {
            goThroughLibraries(library.uses());
            for (TypeDeclaration typeDeclaration : library.types()) {

                System.err.println("Type: " + typeDeclaration.getClass().getInterfaces()[0] + " -> " + typeDeclaration.name() + ":" + typeDeclaration.type());
                declarations.put(typeDeclaration.name(), typeDeclaration);
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
            new TypeFinder().findTypes(api);
        }
    }

    public List<TypeDeclaration> allTypes() {

        return new ArrayList<>(declarations.values());
    }
}
