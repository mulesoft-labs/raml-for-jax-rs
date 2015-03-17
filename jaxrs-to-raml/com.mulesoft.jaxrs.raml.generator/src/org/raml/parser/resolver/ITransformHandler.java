package org.raml.parser.resolver;

/**
 * <p>ITransformHandler interface.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public interface ITransformHandler {

	/**
	 * <p>handle.</p>
	 *
	 * @param value a {@link java.lang.Object} object.
	 * @param parent a {@link java.lang.Object} object.
	 * @return a {@link java.lang.Object} object.
	 */
	public Object handle(Object value,Object parent);	
}
