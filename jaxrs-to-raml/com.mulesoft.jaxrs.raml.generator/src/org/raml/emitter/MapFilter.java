package org.raml.emitter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>MapFilter class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MapFilter {

	Class<? extends IFilter<?>>value();
}
