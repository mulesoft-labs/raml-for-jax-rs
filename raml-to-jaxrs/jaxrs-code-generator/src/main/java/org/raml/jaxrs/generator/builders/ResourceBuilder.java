package org.raml.jaxrs.generator.builders;

import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 10/29/16.
 * Just potential zeroes and ones
 */
public interface ResourceBuilder {

    ResourceBuilder withDocumentation(String docs);

    void output(Appendable appendable) throws IOException;
}
