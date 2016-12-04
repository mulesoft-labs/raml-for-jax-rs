package org.raml.jaxrs.generator.v10;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.squareup.javapoet.TypeName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.MethodSignature;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.jaxrs.generator.builders.resources.MethodBuilder;
import org.raml.jaxrs.generator.builders.resources.ResourceGenerator;
import org.raml.jaxrs.generator.builders.resources.ResourceInterface;
import org.raml.jaxrs.generator.builders.resources.ResponseClassBuilder;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.bodies.MimeType;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.collect.Lists.transform;
import static org.raml.jaxrs.generator.MethodSignature.signature;

/**
 * Created by Jean-Philippe Belanger on 10/26/16.
 * These handlers take care of different model types (v08 vs v10).
 */
public class ResourceHandler {

    private final CurrentBuild build;

    public ResourceHandler(CurrentBuild build) {
        this.build = build;
    }

    public void handle(Api api, final Resource resource) {


        Multimap<Method, TypeDeclaration> incomingBodies = ArrayListMultimap.create();
        Multimap<Method, Response> responses = ArrayListMultimap.create();
        for (Method method : resource.methods()) {

            if ( method.body().size() == 0 ) {
                incomingBodies.put(method, null);
            } else {
                for (TypeDeclaration typeDeclaration : method.body()) {

                    incomingBodies.put(method, typeDeclaration);
                }
            }

            if ( method.responses().size() == 0 ) {
                incomingBodies.put(method, null);
            } else {
                for (Response response : method.responses()) {

                    if (response.body().size() == 0) {
                        responses.put(method, null);
                    } else {

                        responses.put(method, response);
                    }
                }
            }
        }

        ResourceGenerator rg = new ResourceInterface(build, resource, resource.displayName().value(),
                resource.relativeUri().value(), incomingBodies, responses);

        build.newResource(rg);
    }
}
