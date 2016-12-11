package org.raml.jaxrs.generator.v10;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.raml.jaxrs.generator.GAbstractionFactory;
import org.raml.jaxrs.generator.GMethod;
import org.raml.jaxrs.generator.GParameter;
import org.raml.jaxrs.generator.GResource;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 12/10/16.
 * Just potential zeroes and ones
 */
public class V10GResource implements GResource {
    private final GAbstractionFactory factory;
    private final GResource parent;
    private final Resource resource;
    private final List<GResource> subResources;
    private final List<GParameter> uriParameters;
    private final List<GMethod> methods;

    public V10GResource(GAbstractionFactory factory, Resource resource) {

        this(factory, null, resource);
    }

    public V10GResource(final GAbstractionFactory factory, GResource parent, Resource resource) {
        this.factory = factory;
        this.parent = parent;
        this.resource = resource;
        this.subResources = Lists.transform(resource.resources(), new Function<Resource, GResource>() {
            @Nullable
            @Override
            public GResource apply(@Nullable Resource input) {

                return factory.newResource(V10GResource.this, input);
            }
        });

        this.uriParameters = Lists.transform(resource.uriParameters(), new Function<TypeDeclaration, GParameter>() {
            @Nullable
            @Override
            public GParameter apply(@Nullable TypeDeclaration input) {
                return new V10PGParameter(input);
            }
        });

        this.methods = Lists.transform(resource.methods(), new Function<Method, GMethod>() {
            @Nullable
            @Override
            public GMethod apply(@Nullable Method input) {
                return new V10GMethod(V10GResource.this, input);
            }
        });

    }

    @Override
    public List<GResource> resources() {

        return subResources;
    }

    @Override
    public List<GMethod> methods() {
        return methods;
    }

    @Override
    public List<GParameter> uriParameters() {
        return uriParameters;
    }

    @Override
    public String resourcePath() {
        return resource.resourcePath();
    }

    @Override
    public GResource parentResource() {
        return parent;
    }

    @Override
    public Resource implementation() {
        return resource;
    }


}
