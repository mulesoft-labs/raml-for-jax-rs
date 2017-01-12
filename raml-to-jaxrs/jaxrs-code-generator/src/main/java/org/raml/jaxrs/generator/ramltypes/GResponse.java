package org.raml.jaxrs.generator.ramltypes;

import org.raml.jaxrs.generator.GAbstraction;

import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 12/10/16.
 * Just potential zeroes and ones
 */
public interface GResponse extends GAbstraction {
    List<GResponseType> body();
    String code();
}
