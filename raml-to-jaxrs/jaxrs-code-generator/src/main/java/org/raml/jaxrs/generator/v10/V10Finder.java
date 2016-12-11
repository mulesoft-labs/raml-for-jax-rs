package org.raml.jaxrs.generator.v10;

import org.raml.jaxrs.generator.GAbstractionFactory;
import org.raml.jaxrs.generator.GFinder;
import org.raml.jaxrs.generator.GFinderListener;
import org.raml.jaxrs.generator.Names;
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
public class V10Finder implements GFinder {

    private final Api api;
    private final GAbstractionFactory factory;

    private Map<String, TypeDeclaration> foundTypes = new HashMap<>();

    public V10Finder(Api api, GAbstractionFactory factory) {
        this.api = api;
        this.factory =  factory;
    }

    @Override
    public GFinder findTypes(GFinderListener listener) {

        if ( api.uses() != null ) {

            goThroughLibraries(new HashSet<String>(), api.uses(), listener);
        }

        localTypes(api.types(), listener);
        resourceTypes(api.resources(), listener);

        return this;
    }

    private void resourceTypes(List<Resource> resources, GFinderListener listener) {

        for (Resource resource : resources) {

            resourceTypes(resource.resources(), listener);
            for (Method method : resource.methods()) {

                typesInBodies(resource, method, method.body(), listener);
            }
        }
    }

    private void typesInBodies(Resource resource, Method method, List<TypeDeclaration> body, GFinderListener listener) {
        for (TypeDeclaration typeDeclaration : body) {

            TypeDeclaration supertype = foundTypes.get(typeDeclaration.type());
            if (supertype == null || ! TypeUtils.shouldCreateNewClass(typeDeclaration, supertype)) {
                continue;
            }

            V10GType type = new V10GType(resource, method, typeDeclaration);
            listener.newTypeDeclaration(type);
        }
        for (Response response : method.responses()) {
            for (TypeDeclaration typeDeclaration : response.body()) {
                TypeDeclaration supertype = foundTypes.get(typeDeclaration.type());
                if (supertype == null || ! TypeUtils.shouldCreateNewClass(typeDeclaration, supertype)) {
                    continue;
                }

                V10GType type = new V10GType(resource, method, response, typeDeclaration);
                listener.newTypeDeclaration(type);
            }
        }
    }

    private void localTypes(List<TypeDeclaration> types, GFinderListener listener) {

        for (TypeDeclaration typeDeclaration : types) {

            foundTypes.put(typeDeclaration.name(), typeDeclaration);
            V10GType type = new V10GType(typeDeclaration);
            listener.newTypeDeclaration(type);
       }
    }

    private void goThroughLibraries(Set<String> visitedLibraries, List<Library> libraries, GFinderListener listener) {

        for (Library library : libraries) {
            if (visitedLibraries.contains(library.name())) {

                continue;
            } else {

                visitedLibraries.add(library.name());
            }

            goThroughLibraries(visitedLibraries, library.uses(), listener);
            for (TypeDeclaration typeDeclaration : library.types()) {

                V10GType type = new V10GType(typeDeclaration);
                listener.newTypeDeclaration(type);
            }
        }
    }
}
