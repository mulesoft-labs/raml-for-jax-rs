package org.raml.jaxrs.converter.model;

import com.google.common.base.Optional;

import org.raml.api.RamlHeaderParameter;
import org.raml.jaxrs.model.JaxRsHeaderParameter;

import java.lang.reflect.Type;

import static com.google.common.base.Preconditions.checkNotNull;

class JaxRsRamlHeaderParameter implements RamlHeaderParameter {
    private final JaxRsHeaderParameter parameter;

    private JaxRsRamlHeaderParameter(JaxRsHeaderParameter parameter) {
        this.parameter = parameter;
    }

    public static RamlHeaderParameter create(JaxRsHeaderParameter jaxRsHeaderParameter) {
        checkNotNull(jaxRsHeaderParameter);

        return new JaxRsRamlHeaderParameter(jaxRsHeaderParameter);
    }

    @Override
    public String getName() {
        return parameter.getName();
    }

    @Override
    public Optional<String> getDefaultValue() {
        return parameter.getDefaultValue();
    }

    @Override
    public Type getType() {
        return parameter.getType();
    }
}
