package org.raml.schema.model.serializer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.raml.schema.model.IMapSchemaProperty;
import org.raml.schema.model.ISchemaProperty;
import org.raml.schema.model.ISchemaType;

public abstract class StructuredModelSerializer implements IModelSerializer {
	
	abstract protected ISerializationNode createNode(ISchemaType type, ISchemaProperty prop, ISerializationNode parent);

	@Override
	public String serialize(ISchemaType type) {
		
		if(type.isSimple()){
			return null;
		}		
		ISerializationNode node = createNode(type,null,null);
		process(type,node,null);
		return node.getStringValue();
	}

	protected void process(ISchemaType type, ISerializationNode node, Set<String> processedTypes) {		
		
		List<ISchemaProperty> properties = type.getProperties();
		if(properties==null){
			return;
		}
		
		if(processedTypes==null){
			processedTypes = new HashSet<String>();
		}
		
		for(ISchemaProperty prop: properties){
			ISchemaType childType = prop instanceof IMapSchemaProperty ? ((IMapSchemaProperty)prop).getValueType() : prop.getType();
			String qName = childType.getClassQualifiedName();
			ISerializationNode childNode = createNode(childType,prop,node);
			if(!processedTypes.contains(qName)){
				processedTypes.add(qName);
				if(childNode!=null){
					process(childType, childNode, processedTypes);
				}
				processedTypes.remove(qName);
			}
			node.processProperty(type,prop,childNode,processedTypes);
		}
	}

}
