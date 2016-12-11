package org.raml.jaxrs.generator.v08;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.raml.jaxrs.generator.GMethod;
import org.raml.jaxrs.generator.GParameter;
import org.raml.jaxrs.generator.GRequest;
import org.raml.jaxrs.generator.GResource;
import org.raml.jaxrs.generator.GResponse;
import org.raml.v2.api.model.v08.bodies.BodyLike;
import org.raml.v2.api.model.v08.bodies.Response;
import org.raml.v2.api.model.v08.methods.Method;
import org.raml.v2.api.model.v08.parameters.Parameter;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * Created by Jean-Philippe Belanger on 12/11/16.
 * Just potential zeroes and ones
 */
public class V08Method implements GMethod {

    private final V08GResource v08GResource;
    private final List<GParameter> queryParameters;
    private final List<GResponse> responses;
    private final Method input;
    private List<GRequest> requests;

    public V08Method(final V08GResource v08GResource, Method input, final Set<String> globalSchemas) {
        this.v08GResource = v08GResource;

        this.queryParameters = Lists.transform(input.queryParameters(), new Function<Parameter, GParameter>() {
            @Nullable
            @Override
            public GParameter apply(@Nullable Parameter input) {
                return new V08PGParameter(input);
            }
        });
        this.requests = Lists.transform(input.body(), new Function<BodyLike, GRequest>() {
            @Nullable
            @Override
            public GRequest apply(@Nullable BodyLike input) {

                return new V08GRequest(V08Method.this.v08GResource, V08Method.this, input, globalSchemas);
            }
        });

        this.responses = Lists.transform(input.responses(), new Function<Response, GResponse>() {
            @Nullable
            @Override
            public GResponse apply(@Nullable Response input) {
                return new V08Response(input, globalSchemas);
            }
        });
        this.input = input;
    }

    @Override
    public Object implementation() {
        return input;
    }

    @Override
    public List<GRequest> body() {
        return requests;
    }

    @Override
    public GResource resource() {
        return v08GResource;
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
