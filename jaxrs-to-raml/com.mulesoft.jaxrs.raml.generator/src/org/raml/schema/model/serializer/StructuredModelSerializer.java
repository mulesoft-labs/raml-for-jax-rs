package org.raml.schema.model.serializer;

import java.util.List;

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
		process(type,node);		
		return node.getStringValue();
	}

	private void process(ISchemaType type, ISerializationNode node) {
		
		List<ISchemaProperty> properties = type.getProperties();
		if(properties==null){
			return;
		}
		for(ISchemaProperty prop: properties){
			ISchemaType childType = prop.getType();
			ISerializationNode childNode = createNode(childType,prop,node);
			if(childNode!=null){
				process(childType, childNode);
			}
			node.processProperty(type,prop,childNode);
		}
	}

}
