package org.raml.jaxrs.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Jean-Philippe Belanger on 3/26/17.
 * Just potential zeroes and ones
 */
@Retention(RUNTIME) @Target({ElementType.TYPE})
public @interface RamlGenerator {

    Class value();
}
