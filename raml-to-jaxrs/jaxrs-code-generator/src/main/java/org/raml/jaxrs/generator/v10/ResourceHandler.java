package org.raml.jaxrs.generator.v10;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import joptsimple.internal.Strings;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.MethodSignature;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.jaxrs.generator.builders.resources.MethodBuilder;
import org.raml.jaxrs.generator.builders.resources.ResourceGenerator;
import org.raml.jaxrs.generator.builders.resources.ResponseClassBuilder;
import org.raml.jaxrs.generator.builders.types.RamlTypeGenerator;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.bodies.MimeType;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.XMLTypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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

    public void handle(Api api, Resource resource) {

        ResourceGenerator creator = build
                .createResource(resource.displayName().value(), resource.relativeUri().value());
        if (resource.description() != null) {
            creator.withDocumentation(resource.description().value() + "\n");
        }

        if (!api.mediaType().isEmpty()) {
            creator.mediaType(transform(api.mediaType(), new Function<MimeType, String>() {
                @Nullable
                @Override
                public String apply(@Nullable MimeType mimeType) {
                    return mimeType.value();
                }
            }));
        }

        for (Method method : resource.methods()) {
            handleMethod(api, resource, creator, method, "");
        }

        handleSubResources(api, resource, creator, "");
    }

    private void handleSubResources(Api api, Resource resource, ResourceGenerator creator, String subresourcePath) {

        for (Resource subresource : resource.resources()) {

            for (Method method : subresource.methods()) {
                handleMethod(api, subresource, creator, method, subresourcePath + subresource.relativeUri().value());
            }

            handleSubResources(api, subresource, creator, subresourcePath + subresource.relativeUri().value());
        }
    }

    private void handleMethod(Api api, Resource resource, ResourceGenerator creator, Method method, String resourcePath) {

        String fullMethodName = Names.methodName(method.method(), resourcePath, Lists.transform(method.queryParameters(),
                queryParameterToString()));

        String methodNameSuffix = Names.methodNameSuffix(resourcePath, Lists.transform(method.queryParameters(),
                queryParameterToString()));

        Map<MethodSignature, MethodBuilder> seenTypes = new HashMap<>();

        ResponseClassBuilder response = creator.createResponseClassBuilder(method.method(),
                methodNameSuffix);

        setupResponses(api, method, response);

        if (method.body().isEmpty()) {

            buildMethodReceivingType(resource, creator, resourcePath, method, fullMethodName, null, response, seenTypes, null);

        } else {
            for (TypeDeclaration requestTypeDeclaration : method.body()) {

                if (TypeUtils.isInlineTypeDeclarationFromResource(api, build, requestTypeDeclaration)) {

                    String methodAsTypeName = resource.resourcePath() + "_" + requestTypeDeclaration.name();

                    buildMethodReceivingType(resource, creator, resourcePath, method, fullMethodName, requestTypeDeclaration,
                            response, seenTypes, methodAsTypeName
                    );
                } else {

                    buildMethodReceivingType(resource, creator, resourcePath, method, fullMethodName, requestTypeDeclaration,
                            response, seenTypes, null
                    );
                }
            }
        }
    }

    private Function<TypeDeclaration, String> typeToTypeName() {
        return new Function<TypeDeclaration, String>() {
            @Nullable
            @Override
            public String apply(@Nullable TypeDeclaration input) {
                return input.name();
            }
        };
    }


    private void buildMethodReceivingType(Resource resource, ResourceGenerator creator, String path, Method method,
            String fullMethodName, TypeDeclaration requestTypeDeclaration,
            ResponseClassBuilder response, Map<MethodSignature, MethodBuilder> seenTypes, String internalTypeName) {

        MethodSignature sig = signature(method, resource.uriParameters(), requestTypeDeclaration);

        if (!seenTypes.containsKey(sig)) {

            MethodBuilder mb = creator.createMethod(method.method(), fullMethodName, response.name());

            for (TypeDeclaration queryTypeDeclaration : method.queryParameters()) {
                mb.addQueryParameter(queryTypeDeclaration.name(), queryTypeDeclaration.type());
            }

            for (TypeDeclaration pathTypeDeclaration : resource.uriParameters()) {
                mb.addPathParameter(pathTypeDeclaration.name(), pathTypeDeclaration.type());
            }

            if (!"".equals(path)) {
                mb.addPathAnnotation(path);
            }

            if (requestTypeDeclaration != null) {
                TypeName name = build.checkJavaType(requestTypeDeclaration.type(), new HashMap<String, JavaPoetTypeGenerator>());
                if ( name != null ) {

                    mb.addEntityParameter("entity", requestTypeDeclaration.type());
                } else {

                    mb.addEntityParameter("entity", Names.ramlTypeName(resource, method, requestTypeDeclaration));
                }

                mb.addConsumeAnnotation(requestTypeDeclaration.name());
            }

            seenTypes.put(sig, mb);


        } else {

            MethodBuilder builder = seenTypes.get(sig);
            if (requestTypeDeclaration != null) {
                builder.addConsumeAnnotation(requestTypeDeclaration.name());
            }
        }
    }

    private void setupResponses(Api api, Method method, ResponseClassBuilder responseBuilder) {

        for (Response response : method.responses()) {

            if (response.body().size() == 0) {
                responseBuilder.withResponse(response.code().value());
            } else {
                for (TypeDeclaration typeDeclaration : response.body()) {


                    if ( TypeUtils.isInlineTypeDeclarationFromResource(api, build,  typeDeclaration)) {


                        responseBuilder.withResponse(response.code().value(), typeDeclaration.name(), Names.ramlTypeName(method.resource(), method, response, typeDeclaration));
                    } else {

                        responseBuilder.withResponse(response.code().value(), typeDeclaration.name(), typeDeclaration.type());
                    }
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
