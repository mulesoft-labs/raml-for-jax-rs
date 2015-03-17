package org.raml.emitter;


/**
 * <p>IFilter interface.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public interface IFilter<A> {

	/**
	 * <p>accept.</p>
	 *
	 * @param element a A object.
	 * @return a boolean.
	 */
	boolean accept(A element);
}
