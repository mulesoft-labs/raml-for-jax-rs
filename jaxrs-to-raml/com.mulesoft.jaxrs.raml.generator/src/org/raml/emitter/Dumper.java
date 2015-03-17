package org.raml.emitter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>Dumper class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Dumper {

	Class<? extends IRAMLFieldDumper>value();
}
