package org.raml.jaxrs.common;

/**
 * Created by Jean-Philippe Belanger on 4/9/17.
 * Just potential zeroes and ones
 */
public @interface Example {
    String name() default "";
    String value();
}
