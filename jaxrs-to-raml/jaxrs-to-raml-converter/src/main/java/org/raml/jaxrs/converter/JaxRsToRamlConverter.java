package org.raml.jaxrs.converter;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import org.raml.jaxrs.model.JaxRsApplication;
import org.raml.jaxrs.model.JaxRsResource;
import org.raml.model.RamlApi;
import org.raml.model.impl.RamlApiImpl;
import org.raml.utilities.IndentedAppendable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

public class JaxRsToRamlConverter {

    private final static Logger logger = LoggerFactory.getLogger(JaxRsToRamlConverter.class);

    private JaxRsToRamlConverter() {
    }

    public static JaxRsToRamlConverter create() {
        return new JaxRsToRamlConverter();
    }

    public RamlApi convert(RamlConfiguration configuration, JaxRsApplication application) throws JaxRsToRamlConversionException {

        if (logger.isDebugEnabled()) {
            logger.debug("converting application: \n{}", jaxRsApplicationPrettyString(application));
        }

        Set<JaxRsResource> jaxRsResources = application.getResources();


        Iterable<org.raml.model.Resource> ramlResources = toRamlResources(jaxRsResources);

        return RamlApiImpl.create(configuration.getTitle(), configuration.getVersion(), configuration.getBaseUri(), ramlResources);
    }

    private static Iterable<org.raml.model.Resource> toRamlResources(Iterable<JaxRsResource> jaxRsResources) {
        return Iterables.transform(
                jaxRsResources,
                new Function<JaxRsResource, org.raml.model.Resource>() {
                    @Override
                    public org.raml.model.Resource apply(JaxRsResource resource) {
                        return org.raml.model.impl.ResourceImpl.create(resource.getPath().getStringRepresentation(), toRamlResources(resource.getChildren()));
                    }
                }
        );
    }

    private static String jaxRsApplicationPrettyString(JaxRsApplication application) {
        StringBuilder builder = new StringBuilder();

        try {
            appendApplication(IndentedAppendable.forNoSpaces(2, builder), application);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return builder.toString();
    }

    private static IndentedAppendable appendApplication(IndentedAppendable appendable, JaxRsApplication application) throws IOException {
        appendable.appendLine("JaxRsApplication {");

        appendable.indent();
        for (JaxRsResource resource : application.getResources()) {
            appendResource(appendable, resource);
        }
        appendable.outdent();
        appendable.withIndent().append("}");
        return appendable;
    }

    private static IndentedAppendable appendResource(IndentedAppendable appendable, JaxRsResource resource) throws IOException {
        appendable.appendLine("Resource {");
        appendable.indent();
        appendable.appendLine("path: " + resource.getPath().getStringRepresentation());
        appendable.outdent();
        appendable.appendLine("}");

        return appendable;
    }

}
