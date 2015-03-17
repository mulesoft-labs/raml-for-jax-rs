package org.raml.emitter;

/**
 * <p>IRamlHierarchyTarget interface.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public interface IRamlHierarchyTarget {

	/**
	 * <p>write.</p>
	 *
	 * @param path a {@link java.lang.String} object.
	 * @param content a {@link java.lang.String} object.
	 */
	void write(String path,String content);
	/**
	 * <p>writeRoot.</p>
	 *
	 * @param content a {@link java.lang.String} object.
	 */
	void writeRoot(String content);
	
}
