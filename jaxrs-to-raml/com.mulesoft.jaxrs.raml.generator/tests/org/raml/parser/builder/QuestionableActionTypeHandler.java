package org.raml.parser.builder;

import org.raml.parser.resolver.TupleHandler;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class QuestionableActionTypeHandler implements TupleHandler {

	@Override
	public boolean handles(NodeTuple tuple) {
		if (tuple.getKeyNode() instanceof ScalarNode) {
			ScalarNode keyNode = (ScalarNode) tuple.getKeyNode();
			return keyNode.getValue().endsWith("?");
		} else {
			return false;
		}
	}
}
