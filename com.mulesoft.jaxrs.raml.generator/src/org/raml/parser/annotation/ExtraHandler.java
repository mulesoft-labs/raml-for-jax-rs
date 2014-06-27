package org.raml.parser.annotation;

import org.yaml.snakeyaml.nodes.SequenceNode;

public interface ExtraHandler {

	void handle(Object pojo,SequenceNode node);
}
