package org.raml.parser.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.raml.parser.resolver.ITransformHandler;

/**
 * <p>TransformHandler class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface TransformHandler {
	Class<? extends ITransformHandler>value();
}
