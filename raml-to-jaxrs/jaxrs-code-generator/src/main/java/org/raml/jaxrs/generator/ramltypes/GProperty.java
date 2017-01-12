package org.raml.jaxrs.generator.ramltypes;

import org.raml.jaxrs.generator.GAbstraction;

/**
 * Created by Jean-Philippe Belanger on 12/10/16.
 * Just potential zeroes and ones
 */
public interface GProperty extends GAbstraction {
    String name();
    GType type();

    boolean isInline();
    GProperty overrideType(GType type);
}
