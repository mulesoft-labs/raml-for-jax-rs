package org.raml.emitter;

import java.lang.reflect.Field;

import org.raml.emitter.RamlEmitterV2;

public interface IRAMLFieldDumper {

	void dumpField(StringBuilder dump, int depth, Field declaredField,
			Object pojo,RamlEmitterV2 emitter);

}
