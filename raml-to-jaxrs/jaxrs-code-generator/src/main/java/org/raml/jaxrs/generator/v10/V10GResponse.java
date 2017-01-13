package org.raml.jaxrs.generator.v10;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.raml.jaxrs.generator.ramltypes.GResponse;
import org.raml.jaxrs.generator.ramltypes.GResponseType;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 12/10/16.
 * Just potential zeroes and ones
 */
public class V10GResponse implements GResponse {
    private V10GResource v10GResource;
    private final Response response;
    private final List<GResponseType> bodies;

    public V10GResponse(final V10TypeRegistry registry, final V10GResource v10GResource, final Method method, final Response response) {
        this.v10GResource = v10GResource;
        this.response = response;
        this.bodies = Lists.transform(this.response.body(), new Function<TypeDeclaration, GResponseType>() {
            @Nullable
            @Override
            public GResponseType apply(@Nullable TypeDeclaration input) {
                if (TypeUtils.shouldCreateNewClass(input, input.parentTypes().toArray(new TypeDeclaration[0]))) {
                    return new V10GResponseType(input,
                            registry.fetchType(v10GResource.implementation(), method, response, input));
                } else {
//                    return new V10GResponseType(input, V10GTypeFactory
//                            .createExplicitlyNamedType(registry, input.type(), input));
                   return new V10GResponseType(input, registry.fetchType(input.type(), input));

                }
            }
        });
    }

    @Override
    public Response implementation() {
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
