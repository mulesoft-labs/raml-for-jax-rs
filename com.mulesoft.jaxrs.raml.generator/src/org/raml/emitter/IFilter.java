package org.raml.emitter;


public interface IFilter<A> {

	boolean accept(A element);
}
