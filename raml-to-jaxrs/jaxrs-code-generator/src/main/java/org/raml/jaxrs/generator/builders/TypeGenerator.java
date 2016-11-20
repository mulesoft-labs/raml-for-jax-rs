package org.raml.jaxrs.generator.builders;

/**
 * Created by Jean-Philippe Belanger on 11/20/16.
 * Just potential zeroes and ones
 */
public interface TypeGenerator extends Generator {

    String getGeneratedJavaType();
    // is a property declared here or in my parents ?
    boolean declaresProperty(String name);

}
