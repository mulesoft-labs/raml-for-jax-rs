package org.raml.jaxrs.generator.v08;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.raml.jaxrs.generator.GResponse;
import org.raml.jaxrs.generator.GResponseType;
import org.raml.v2.api.model.v08.bodies.BodyLike;
import org.raml.v2.api.model.v08.bodies.Response;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * Created by Jean-Philippe Belanger on 12/11/16.
 * Just potential zeroes and ones
 */
public class V08Response implements GResponse {

    private final Response response;
    private final List<GResponseType> bodies;

    public V08Response(Response input, Set<String> globalSchemas) {

        this.response = input;
        this.bodies = Lists.transform(input.body(), new Function<BodyLike, GResponseType>() {
            @Nullable
            @Override
            public GResponseType apply(@Nullable BodyLike input) {

                return new V08GResponseType(input);
            }
        });
    }

    @Override
    public Object implementation() {
        return response;
    }

    @Override
    public List<GResponseType> body() {
        return bodies;
    }

    @Override
    public String code() {
        return response.code().value();
    }
}
