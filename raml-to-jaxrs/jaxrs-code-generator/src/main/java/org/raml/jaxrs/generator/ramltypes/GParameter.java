package org.raml.jaxrs.generator.ramltypes;

import org.raml.jaxrs.generator.GAbstraction;

/**
 * Created by Jean-Philippe Belanger on 12/10/16.
 * Just potential zeroes and ones
 */
public interface GParameter extends GAbstraction {
    String name();

    boolean isComposite();

    GType type();
}
