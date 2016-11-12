package org.raml.jaxrs.generator.v10;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.builders.MethodBuilder;
import org.raml.jaxrs.generator.builders.ResourceBuilder;
import org.raml.jaxrs.generator.builders.ResponseClassBuilder;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.bodies.MimeType;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.transform;

/**
 * Created by Jean-Philippe Belanger on 10/26/16.
 * These handlers take care of different model types (v08 vs v10).
 */
public class ResourceHandler {

    public void handle(String packageName, CurrentBuild build, Api api, Resource resource) {

        ResourceBuilder creator = build
                .createResource(resource.displayName().value(), resource.relativeUri().value());
        if ( resource.description() != null ) {
                creator.withDocumentation(resource.description().value() + "\n");
        }

        if ( ! api.mediaType().isEmpty()) {
            creator.mediaType(transform(api.mediaType(), new Function<MimeType, String>() {
                @Nullable
                @Override
                public String apply(@Nullable MimeType mimeType) {
                    return mimeType.value();
                }
            }));
        }

        for (Method method : resource.methods()) {
            handleMethod(packageName, creator, method, "", resource.uriParameters());
        }

        handleSubResources(packageName, build, api, resource, creator, "");
    }

    private void handleSubResources(String packageName, CurrentBuild build, Api api, Resource resource, ResourceBuilder creator, String subresourcePath) {

        for (Resource subresource : resource.resources()) {

            for (Method method : resource.methods()) {
                handleMethod(packageName, creator, method, subresourcePath + subresource.relativeUri().value(), subresource.uriParameters());
            }

            handleSubResources(packageName, build, api, subresource, creator, subresourcePath + subresource.relativeUri().value());
        }


    }

    private void handleMethod(String packageName, ResourceBuilder creator, Method method, String path, List<TypeDeclaration> pathParameters) {
        String pathSuffix = "".equals(path) ? "": Names.buildTypeName(path);
        String queryParameterSuffix = Names.parameterNameMethodSuffix(Lists.transform(method.queryParameters(),
                queryParameterToString()));

        Map<String, MethodBuilder> seenTypes = new HashMap<>();
        ResponseClassBuilder response = creator.createResponseClassBuilder(packageName, method.method(),
                pathSuffix + queryParameterSuffix);
        setupResponses(method, response);

        if (method.body().isEmpty()) {

            buildMethodReceivingType(null, creator, method, path, pathParameters, pathSuffix,
                    queryParameterSuffix,
                    seenTypes, response);

        } else {
            for (TypeDeclaration requestTypeDeclaration : method.body()) {

                buildMethodReceivingType(requestTypeDeclaration, creator, method, path, pathParameters, pathSuffix,
                        queryParameterSuffix,
                        seenTypes, response);


            }
        }
    }

    private void buildMethodReceivingType(TypeDeclaration requestTypeDeclaration, ResourceBuilder creator, Method method,
            String path, List<TypeDeclaration> pathParameters, String pathSuffix, String queryParameterSuffix,
            Map<String, MethodBuilder> seenTypes, ResponseClassBuilder response) {
        if ( ! seenTypes.containsKey(method.method() + ((requestTypeDeclaration == null ) ? "void": requestTypeDeclaration.type())) ) {

            MethodBuilder mb = creator.createMethod(method.method(), pathSuffix + queryParameterSuffix, response.name());
            seenTypes.put(method.method() + ((requestTypeDeclaration == null ) ? "void": requestTypeDeclaration.type()), mb);

            for (TypeDeclaration queryTypeDeclaration : method.queryParameters()) {
                mb.addQueryParameter(queryTypeDeclaration.name(), queryTypeDeclaration.type());
            }

            for (TypeDeclaration pathTypeDeclaration : pathParameters) {
                mb.addPathParameter(pathTypeDeclaration.name(), pathTypeDeclaration.type());
            }

            if ( ! "".equals(path) ) {
                mb.addPathAnnotation(path);
            }

            if ( requestTypeDeclaration != null ) {
                mb.addEntityParameter("entity", requestTypeDeclaration.type());
            }
            if ( requestTypeDeclaration != null ) {
                mb.addConsumeAnnotation(requestTypeDeclaration.name());
            }

        } else {

            MethodBuilder builder = seenTypes.get(method.method() + ((requestTypeDeclaration == null ) ? "void": requestTypeDeclaration.type()));
            if ( requestTypeDeclaration != null ) {
                builder.addConsumeAnnotation(requestTypeDeclaration.name());
            }
        }
    }

    private void setupResponses(Method method, ResponseClassBuilder responseBuilder) {

        for (Response response : method.responses()) {

            if ( response.body().size() == 0 ) {
                responseBuilder.withResponse(response.code().value());
            } else {
                for (TypeDeclaration typeDeclaration : response.body()) {
                    responseBuilder.withResponse(response.code().value(), typeDeclaration.name(), typeDeclaration.type());
                }
            }
        }
    }


    private static Function<TypeDeclaration, String> queryParameterToString() {

        return new TypeDeclarationToString();
    }

    private static class TypeDeclarationToString implements Function<TypeDeclaration, String> {
        @Nullable
        @Override
        public String apply(@Nullable TypeDeclaration input) {
            return input.name();
        }
    }
}
