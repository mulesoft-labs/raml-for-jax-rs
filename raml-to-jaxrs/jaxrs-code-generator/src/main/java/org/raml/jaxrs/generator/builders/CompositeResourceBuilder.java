package org.raml.jaxrs.generator.builders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public ResourceBuilder mediaType(List<String> mimeTypes) {

        for (ResourceBuilder builder : builders) {
            builder.mediaType(mimeTypes);
        }

        return this;
    }

    @Override
    public MethodBuilder createMethod(String method, String additionalNames) {

        List<MethodBuilder> list = new ArrayList<MethodBuilder>();
        for (ResourceBuilder builder : builders) {
            list.add(builder.createMethod(method, additionalNames));
        }

        return new CompositeMethodBuilder(list);
    }

    @Override
    public void output(String rootDir) throws IOException {

        for (ResourceBuilder builder : builders) {
            builder.output(rootDir);
        }
    }
}
