package org.raml.jaxrs.generator.v10;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.builders.MethodBuilder;
import org.raml.jaxrs.generator.builders.ResourceBuilder;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.bodies.MimeType;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.annotation.Nullable;

import static com.google.common.collect.Lists.transform;

/**
 * Created by Jean-Philippe Belanger on 10/26/16.
 * These handlers take care of different model types (v08 vs v10).
 */
public class ResourceHandler {

    public void handle(CurrentBuild build, Api api, Resource resource) {

        ResourceBuilder creator = build
                .createResource(resource.displayName().value(), resource.relativeUri().value());
        if ( resource.description() != null ) {
                creator.withDocumentation(resource.description().value() + "\n");
        }

        if ( api.mediaType() != null) {
            creator.mediaType(transform(api.mediaType(), new Function<MimeType, String>() {
                @Nullable
                @Override
                public String apply(@Nullable MimeType mimeType) {
                    return mimeType.value();
                }
            }));
        }

        for (Method method : resource.methods()) {
            handleMethod(creator, method);
        }
    }

    private void handleMethod(ResourceBuilder creator, Method method) {
        String methodNameSuffix = Names.parameterNameMethodSuffix(Lists.transform(method.queryParameters(),
                queryParameterToString()));

        MethodBuilder mb = creator.createMethod(method.method(), methodNameSuffix);

        if (method.queryParameters() != null ) {
            for (TypeDeclaration typeDeclaration : method.queryParameters()) {
                mb.addParameter(typeDeclaration.name(), typeDeclaration.type());
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
