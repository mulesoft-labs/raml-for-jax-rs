package org.raml.jaxrs.parser.model;

import com.google.common.base.Optional;

import org.glassfish.jersey.server.model.Parameter;
import org.raml.jaxrs.model.JaxRsHeaderParameter;

import java.lang.reflect.Type;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

class JerseyJaxRsHeaderParameter implements JaxRsHeaderParameter {
    private final Parameter parameter;

    private JerseyJaxRsHeaderParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    static JerseyJaxRsHeaderParameter create(Parameter parameter) {
        checkNotNull(parameter);
        checkArgument(Utilities.isHeaderParameterPredicate().apply(parameter), "invalid header parameter %s", parameter);

        return new JerseyJaxRsHeaderParameter(parameter);
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
