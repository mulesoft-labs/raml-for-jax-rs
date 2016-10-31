package org.raml.jaxrs.generator.builders;

import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 10/30/16.
 * Just potential zeroes and ones
 */
public class CompositeMethodBuilder implements MethodBuilder {


    private final List<MethodBuilder> list;

    public CompositeMethodBuilder(List<MethodBuilder> list) {
        this.list = list;
    }

    @Override
    public MethodBuilder addParameter(String name, String type) {

        for (MethodBuilder methodBuilder : list) {
            methodBuilder.addParameter(name, type);
        }

        return this;
    }
}
