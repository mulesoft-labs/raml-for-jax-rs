package org.raml.jaxrs.parser.model;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.raml.jaxrs.model.JaxRsHeaderParameter;
import org.raml.jaxrs.model.JaxRsQueryParameter;

import javax.annotation.Nullable;

class Utilities {

    private static final Predicate<Parameter> IS_QUERY_PARAMETER_PREDICATE = new Predicate<Parameter>() {
        @Override
        public boolean apply(@Nullable Parameter parameter) {
            return parameter.getSource() == Parameter.Source.QUERY;
        }
    };

    public static final Predicate<Parameter> IS_HEADER_PARAMETER_PREDICATE = new Predicate<Parameter>() {
        @Override
        public boolean apply(@Nullable Parameter parameter) {
            return parameter.getSource() == Parameter.Source.HEADER;
        }
    };

    private Utilities() {}

    public static FluentIterable<Parameter> getQueryParameters(ResourceMethod resourceMethod) {
        return FluentIterable.from(resourceMethod.getInvocable().getParameters()).filter(
                isQueryParameterPredicate()
        );
    }

    public static FluentIterable<JaxRsQueryParameter> toJaxRsQueryParameters(Iterable<Parameter> parameters) {
        return FluentIterable.from(parameters).transform(
                new Function<Parameter, JaxRsQueryParameter>() {
                    @Nullable
                    @Override
                    public JaxRsQueryParameter apply(@Nullable Parameter parameter) {
                        return JerseyJaxRsQueryParameter.create(parameter);
                    }
                }
        );
    }

    public static Predicate<Parameter> isQueryParameterPredicate() {
        return IS_QUERY_PARAMETER_PREDICATE;
    }


    public static FluentIterable<Parameter> getHeaderParameters(ResourceMethod resourceMethod) {
        return FluentIterable.from(resourceMethod.getInvocable().getParameters()).filter(
                isHeaderParameterPredicate()
        );
    }

    public static Predicate<Parameter> isHeaderParameterPredicate() {
        return IS_HEADER_PARAMETER_PREDICATE;
    }

    public static FluentIterable<JaxRsHeaderParameter> toJaxRsHeaderParameters(Iterable<Parameter> headerParameters) {
        return FluentIterable.from(headerParameters).transform(
                new Function<Parameter, JaxRsHeaderParameter>() {
                    @Nullable
                    @Override
                    public JaxRsHeaderParameter apply(@Nullable Parameter parameter) {
                        return JerseyJaxRsHeaderParameter.create(parameter);
                    }
                }
        );
    }
}
