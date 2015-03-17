package org.raml.parser.rule;

import java.util.List;
import java.util.Map;

import org.raml.model.Raml2;
import org.raml.parser.annotation.ExtraHandler;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

/**
 * <p>GlobalSchemasHandler class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class GlobalSchemasHandler implements ExtraHandler {

	
	/** {@inheritDoc} */
	public void handle(Object pojo, SequenceNode node) {
		if (pojo instanceof Raml2) {
			Raml2 r=(Raml2) pojo;
			Map<String, String> schemaMap = r.getSchemaMap();
			for (Node n : node.getValue()) {
				if (n instanceof MappingNode) {
					MappingNode m = (MappingNode) n;
					List<NodeTuple> value = m.getValue();
					for (NodeTuple t : value) {
						Node keyNode = t.getKeyNode();
						if (keyNode instanceof ScalarNode){
							ScalarNode sc=(ScalarNode) keyNode;
							String value2 = sc.getValue();
							
							Node valueNode = t.getValueNode();
							if (valueNode instanceof ScalarNode){
								ScalarNode sm=(ScalarNode) valueNode;
								if (sm.getTag().getValue().equals("!include")){
									schemaMap.put(value2,sm.getValue());
								}
							}
						}
						
					}
				}
			}
		}
	}

}
