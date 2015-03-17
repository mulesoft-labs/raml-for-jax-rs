package org.raml.emitter;

import java.lang.reflect.Field;

import org.raml.emitter.RamlEmitterV2;

/**
 * <p>IRAMLFieldDumper interface.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public interface IRAMLFieldDumper {

	/**
	 * <p>dumpField.</p>
	 *
	 * @param dump a {@link java.lang.StringBuilder} object.
	 * @param depth a int.
	 * @param declaredField a {@link java.lang.reflect.Field} object.
	 * @param pojo a {@link java.lang.Object} object.
	 * @param emitter a {@link org.raml.emitter.RamlEmitterV2} object.
	 */
	void dumpField(StringBuilder dump, int depth, Field declaredField,
			Object pojo,RamlEmitterV2 emitter);

}
