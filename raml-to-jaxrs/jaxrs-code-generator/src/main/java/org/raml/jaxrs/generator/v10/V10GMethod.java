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
import org.raml.v2.api.model.v10.resources.Resource;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 12/10/16.
 * Just potential zeroes and ones
 */
class V10GMethod implements GMethod {
    private V10GResource v10GResource;
    private final Method method;
    private final List<GParameter> queryParameters;
    private final List<GResponse> responses;
    private List<GRequest> requests;

    public V10GMethod(final V10GResource v10GResource, final Method method) {
        this.v10GResource = v10GResource;
        this.method = method;
        this.requests = Lists.transform(this.method.body(), new Function<TypeDeclaration, GRequest>() {
            @Nullable
            @Override
            public GRequest apply(@Nullable TypeDeclaration input) {

                if (TypeUtils.shouldCreateNewClass(input, input.parentTypes().toArray(new TypeDeclaration[0]))) {
                    return new V10GRequest(input, new V10GType(v10GResource.implementation(), method, input));
                } else {
                    return new V10GRequest(input, new V10GType(input.type(), input));
                }
            }
        });

        this.responses = Lists.transform(this.method.responses(), new Function<Response, GResponse>() {
            @Nullable
            @Override
            public GResponse apply(@Nullable Response input) {
                return new V10GResponse(v10GResource, method, input);
            }
        });

        this.queryParameters = Lists.transform(this.method.queryParameters(), new Function<TypeDeclaration, GParameter>() {
            @Nullable
            @Override
            public GParameter apply(@Nullable TypeDeclaration input) {

                return new V10PGParameter(input);
            }
        });
    }

    @Override
    public Method implementation() {
        return method;
    }

    @Override
    public List<GRequest> body() {
        return requests;
    }

    @Override
    public GResource resource() {
        return v10GResource;
    }

    @Override
    public String method() {
        return method.method();
    }

    @Override
    public List<GParameter> queryParameters() {
        return queryParameters;
    }

    @Override
    public List<GResponse> responses() {
        return responses;
    }

    @Override
    public boolean equals(Object obj) {

        if ( obj == null || ! (obj instanceof V10GMethod) ) {

            return false;
        }

        V10GMethod method = (V10GMethod) obj;
        return method.v10GResource.resourcePath().equals(v10GResource.resourcePath()) && method.method().equals(method());
    }

    @Override
    public int hashCode() {

        return method().hashCode() + v10GResource.resourcePath().hashCode();
    }

    @Override
    public String toString() {
        return "V10GMethod{" +
                "resource=" + v10GResource.resourcePath() +
                ", method=" + method.method() +
                '}';
    }
}
