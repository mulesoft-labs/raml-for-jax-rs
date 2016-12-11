package org.raml.jaxrs.generator.v10;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.raml.jaxrs.generator.GResponse;
import org.raml.jaxrs.generator.GType;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 12/10/16.
 * Just potential zeroes and ones
 */
class V10GResponse implements GResponse {
    private V10GResource v10GResource;
    private final Response input;
    private final List<GType> bodies;

    public V10GResponse(V10GResource v10GResource, Response input) {
        this.v10GResource = v10GResource;
        this.input = input;
        this.bodies = Lists.transform(input.body(), new Function<TypeDeclaration, GType>() {
            @Nullable
            @Override
            public GType apply(@Nullable TypeDeclaration input) {
                return new V10GType(input);
            }
        });
    }

    @Override
    public Response implementation() {
        return input;
    }

    @Override
    public List<GType> body() {
        return bodies;
    }

    @Override
    public String code() {
        return input.code().value();
    }
}
