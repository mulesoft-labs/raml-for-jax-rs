package org.raml.jaxrs.converter.model;

import com.google.common.base.Optional;

import org.raml.jaxrs.model.JaxRsQueryParameter;
import org.raml.model.RamlQueryParameter;

import static com.google.common.base.Preconditions.checkNotNull;

class JaxRsRamlQueryParameter implements RamlQueryParameter {
    private final JaxRsQueryParameter queryParameter;

    private JaxRsRamlQueryParameter(JaxRsQueryParameter queryParameter) {
        this.queryParameter = queryParameter;
    }

    public static RamlQueryParameter create(JaxRsQueryParameter queryParameter) {
        checkNotNull(queryParameter);

        return new JaxRsRamlQueryParameter(queryParameter);
    }

    @Override
    public String getName() {
        return this.queryParameter.getName();
    }

    @Override
    public Optional<String> getDefaultValue() {
        return this.queryParameter.getDefaultValue();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(this.getClass().getSimpleName());
        builder.append("{ ");
        builder.append("name: ").append(this.getName());
        builder.append(" }");
        return builder.toString();
    }
}
