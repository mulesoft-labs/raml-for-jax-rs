package org.raml.jaxrs.common;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Jean-Philippe Belanger on 4/9/17.
 * Just potential zeroes and ones
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodExamples {

    Example[] consumed() default  {};
    Example[] produced() default {};
}
