package org.raml.jaxrs.generator.builders;

import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 10/29/16.
 * Just potential zeroes and ones
 */
public class CompositeResourceBuilder  implements ResourceBuilder{

    private final ResourceBuilder[] builders;

    public CompositeResourceBuilder(ResourceBuilder... builders) {

        this.builders = builders;
    }

    @Override
    public ResourceBuilder withDocumentation(String docs) {
        for (ResourceBuilder builder : builders) {
            builder.withDocumentation(docs);
        }

        return this;
    }

    @Override
    public void output(Appendable appendable) throws IOException {

        for (ResourceBuilder builder : builders) {
            builder.output(appendable);
        }
    }
}
