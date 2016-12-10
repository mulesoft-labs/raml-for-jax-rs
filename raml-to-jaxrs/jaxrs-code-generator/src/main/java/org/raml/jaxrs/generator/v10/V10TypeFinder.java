package org.raml.jaxrs.generator.v10;

import org.raml.jaxrs.generator.TypeFinder;
import org.raml.jaxrs.generator.TypeFinderListener;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.api.Library;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Jean-Philippe Belanger on 12/6/16.
 * Just potential zeroes and ones
 */
public class V10TypeFinder implements TypeFinder<V10GeneratorContext> {

    private final Api api;

    public V10TypeFinder(Api api) {
        this.api = api;
    }

    @Override
    public V10TypeFinder findTypes(TypeFinderListener<V10GeneratorContext> listener) {

        if ( api.uses() != null ) {

            goThroughLibraries(new HashSet<String>(), api.uses(), listener);
        }

        localTypes(api.types(), listener);
        resourceTypes(api.resources(), listener);

        return this;
    }

    private void resourceTypes(List<Resource> resources, TypeFinderListener<V10GeneratorContext> listener) {

        for (Resource resource : resources) {

            resourceTypes(resource.resources(), listener);
            for (Method method : resource.methods()) {

                typesInBodies(resource, method, method.body(), listener);
            }
        }
    }

    private void typesInBodies(Resource resource, Method method, List<TypeDeclaration> body, TypeFinderListener<V10GeneratorContext> listener) {
        for (TypeDeclaration typeDeclaration : body) {

            listener.newType(new V10GeneratorContext(api, resource, method, typeDeclaration));
        }

        for (Response response : method.responses()) {
            for (TypeDeclaration typeDeclaration : response.body()) {
                listener.newType(new V10GeneratorContext(api, resource, method, response, typeDeclaration));
            }
        }
    }

    private void localTypes(List<TypeDeclaration> types, TypeFinderListener<V10GeneratorContext> listener) {

        for (TypeDeclaration typeDeclaration : types) {

            listener.newType(new V10GeneratorContext(api, typeDeclaration));
       }
    }

    private void goThroughLibraries(Set<String> visitedLibraries, List<Library> libraries, TypeFinderListener<V10GeneratorContext> listener) {

        for (Library library : libraries) {
            if (visitedLibraries.contains(library.name())) {

                continue;
            } else {

                visitedLibraries.add(library.name());
            }

            goThroughLibraries(visitedLibraries, library.uses(), listener);
            for (TypeDeclaration typeDeclaration : library.types()) {

                listener.newType(new V10GeneratorContext(api, typeDeclaration));
            }
        }

    }
}
