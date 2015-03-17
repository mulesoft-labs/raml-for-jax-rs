package org.raml.parser.builder;

import org.yaml.snakeyaml.nodes.SequenceNode;

/**
 * <p>TypeExtraHandler class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class TypeExtraHandler extends TemplatesExtraHandler{

	/**
	 * <p>Constructor for TypeExtraHandler.</p>
	 */
	public TypeExtraHandler() {
		super("typeModel");
	}
	
	
	/** {@inheritDoc} */
	public void handle(Object pojo, SequenceNode node) {
		super.handle(pojo, node);
	}

}
