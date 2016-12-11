package org.raml.jaxrs.generator.v10;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.raml.jaxrs.generator.GMethod;
import org.raml.jaxrs.generator.GParameter;
import org.raml.jaxrs.generator.GRequest;
import org.raml.jaxrs.generator.GResource;
import org.raml.jaxrs.generator.GResponse;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 12/10/16.
 * Just potential zeroes and ones
 */
class V10GMethod implements GMethod {
    private V10GResource v10GResource;
    private final GResource resource;
    private final Method input;
    private final List<GParameter> queryParameters;
    private final List<GResponse> responses;
    private List<GRequest> requests;

    public V10GMethod(final V10GResource v10GResource, GResource resource, Method input) {
        this.v10GResource = v10GResource;
        this.resource = resource;
        this.input = input;
        this.requests = Lists.transform(input.body(), new Function<TypeDeclaration, GRequest>() {
            @Nullable
            @Override
            public GRequest apply(@Nullable TypeDeclaration input) {
                return new V10GRequest(input);
            }
        });

        this.responses = Lists.transform(input.responses(), new Function<Response, GResponse>() {
            @Nullable
            @Override
            public GResponse apply(@Nullable Response input) {
                return new V10GResponse(v10GResource, input);
            }
        });

        this.queryParameters = Lists.transform(input.queryParameters(), new Function<TypeDeclaration, GParameter>() {
            @Nullable
            @Override
            public GParameter apply(@Nullable TypeDeclaration input) {

                return new V10PGParameter(input);
            }
        });
    }

    @Override
    public Method implementation() {
        return input;
    }

    @Override
    public List<GRequest> body() {
        return requests;
    }

    @Override
    public GResource resource() {
        return resource;
    }

    @Override
    public String method() {
        return input.method();
    }

    @Override
    public List<GParameter> queryParameters() {
        return queryParameters;
    }

    @Override
    public List<GResponse> responses() {
        return responses;
    }
}
