package org.raml.jaxrs.generator.builders;

import org.raml.jaxrs.generator.CurrentBuild;

/**
 * Created by Jean-Philippe Belanger on 11/13/16.
 * Just potential zeroes and ones
 */
public interface TypeDescriber {

    void asJavaType(CurrentBuild currentBuild, Class c);
    void asBuiltType(CurrentBuild currentBuild, String name);
}
