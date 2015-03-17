package org.raml.parser.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.raml.model.TemplateUse;
import org.raml.parser.annotation.ExtraHandler;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

/**
 * <p>TemplatesExtraHandler class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class TemplatesExtraHandler implements ExtraHandler {

	String field;
	
	/**
	 * <p>Constructor for TemplatesExtraHandler.</p>
	 *
	 * @param field a {@link java.lang.String} object.
	 */
	public TemplatesExtraHandler(String field) {
		super();
		this.field = field;
	}

	
	/** {@inheritDoc} */
	public void handle(Object pojo, SequenceNode node) {
		List<Node> value = node.getValue();
		ArrayList<TemplateUse> str = new ArrayList<TemplateUse>();
		for (Node n : value) {
			if (n instanceof ScalarNode) {
				
				str.add(new TemplateUse(((ScalarNode) n).getValue()));
			}
			if (n instanceof MappingNode) {
				MappingNode m = (MappingNode) n;
				List<NodeTuple> value2 = m.getValue();
				for (NodeTuple q : value2) {
					TemplateUse t = null;
					Node keyNode = q.getKeyNode();
					if (keyNode instanceof ScalarNode) {
						String value3 = ((ScalarNode) keyNode).getValue();
						t = new TemplateUse(value3);
					}
					Node valueNode = q.getValueNode();

					if (valueNode instanceof MappingNode) {
						MappingNode zz = (MappingNode) valueNode;
						List<NodeTuple> value3 = zz.getValue();
						for (NodeTuple ma : value3) {
							Node keyNode2 = ma.getKeyNode();
							Node valueNode2 = ma.getValueNode();
							if (keyNode2 instanceof ScalarNode
									&& valueNode2 instanceof ScalarNode) {
								ScalarNode scalarNode = (ScalarNode) valueNode2;
								String value4 = scalarNode.getValue();
								if (scalarNode.getTag().equals(new Tag("!include"))){
									value4="!include "+value4;
								}								
								t.getParameters().put(
										((ScalarNode) keyNode2).getValue(),
										value4);
							} else {
								throw new IllegalStateException();
							}
						}

					}
					if (t != null) {
						str.add(t);
					}
				}
			}
		}
		try {
			PropertyUtils.setProperty(pojo, field, str);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}
