package org.raml.emitter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Dumper {

	Class<? extends IRAMLFieldDumper>value();
}
