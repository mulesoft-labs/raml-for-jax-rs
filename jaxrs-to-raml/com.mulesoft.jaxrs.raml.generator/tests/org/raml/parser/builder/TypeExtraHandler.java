package org.raml.parser.builder;

import org.yaml.snakeyaml.nodes.SequenceNode;

public class TypeExtraHandler extends TemplatesExtraHandler{

	public TypeExtraHandler() {
		super("typeModel");
	}
	
	
	public void handle(Object pojo, SequenceNode node) {
		super.handle(pojo, node);
	}

}
