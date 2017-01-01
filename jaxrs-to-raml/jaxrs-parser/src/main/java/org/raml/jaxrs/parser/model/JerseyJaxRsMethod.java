package org.raml.jaxrs.parser.model;

import com.google.common.base.Optional;

import org.glassfish.jersey.server.model.ResourceMethod;
import org.raml.jaxrs.model.HttpVerb;
import org.raml.jaxrs.model.JaxRsHeaderParameter;
import org.raml.jaxrs.model.JaxRsMethod;
import org.raml.jaxrs.model.JaxRsQueryParameter;
import org.raml.jaxrs.parser.source.SourceParser;

import java.util.List;

import javax.ws.rs.core.MediaType;

import static com.google.common.base.Preconditions.checkNotNull;

class JerseyJaxRsMethod implements JaxRsMethod {

    private final ResourceMethod resourceMethod;
    private final SourceParser sourceParser;

    private JerseyJaxRsMethod(ResourceMethod resourceMethod, SourceParser sourceParser) {
        this.resourceMethod = resourceMethod;
        this.sourceParser = sourceParser;
    }

    public static JerseyJaxRsMethod create(ResourceMethod resourceMethod, SourceParser sourceParser) {
        checkNotNull(resourceMethod);
        checkNotNull(sourceParser);

        return new JerseyJaxRsMethod(resourceMethod, sourceParser);
    }

    @Override
    public HttpVerb getHttpVerb() {
        return HttpVerb.fromStringUnchecked(resourceMethod.getHttpMethod());
    }

    @Override
    public List<MediaType> getConsumedMediaTypes() {
        return resourceMethod.getConsumedTypes();
    }

    @Override
    public List<MediaType> getProducedMediaTypes() {
        return resourceMethod.getProducedTypes();
    }

    @Override
    public List<JaxRsQueryParameter> getQueryParameters() {
        return Utilities.toJaxRsQueryParameters(Utilities.getQueryParameters(resourceMethod)).toList();
    }

    @Override
    public List<JaxRsHeaderParameter> getHeaderParameters() {
        return Utilities.toJaxRsHeaderParameters(Utilities.getHeaderParameters(resourceMethod)).toList();
    }

    @Override
    public Optional<String> getDescription() {
        return sourceParser.getDocumentationFor(resourceMethod.getInvocable().getDefinitionMethod());
    }
}
