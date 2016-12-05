package org.raml.jaxrs.parser.model;

import com.google.common.base.Optional;

import org.glassfish.jersey.server.model.Parameter;
import org.raml.jaxrs.model.JaxRsQueryParameter;

import java.lang.reflect.Type;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class JerseyJaxRsQueryParameter implements JaxRsQueryParameter {
    private final Parameter parameter;

    private JerseyJaxRsQueryParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    static JerseyJaxRsQueryParameter create(Parameter parameter) {
        checkNotNull(parameter);
        checkArgument(Utilities.isQueryParameterPredicate().apply(parameter), "invalid query parameter %s", parameter);

        return new JerseyJaxRsQueryParameter(parameter);
    }

    @Override
    public String getName() {
        return this.parameter.getSourceName();
    }

    @Override
    public Optional<String> getDefaultValue() {
        return Optional.fromNullable(this.parameter.getDefaultValue());
    }

    @Override
    public Type getType() {
        return this.parameter.getType();
    }
}
