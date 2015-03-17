package org.raml.parser.annotation;

import org.yaml.snakeyaml.nodes.SequenceNode;

/**
 * <p>ExtraHandler interface.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public interface ExtraHandler {

	/**
	 * <p>handle.</p>
	 *
	 * @param pojo a {@link java.lang.Object} object.
	 * @param node a {@link org.yaml.snakeyaml.nodes.SequenceNode} object.
	 */
	void handle(Object pojo,SequenceNode node);
}
