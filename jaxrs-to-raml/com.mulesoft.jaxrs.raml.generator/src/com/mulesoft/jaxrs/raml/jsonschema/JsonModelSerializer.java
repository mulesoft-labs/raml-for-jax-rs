package com.mulesoft.jaxrs.raml.jsonschema;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.raml.schema.model.DefaultValueFactory;
import org.raml.schema.model.IMapSchemaProperty;
import org.raml.schema.model.ISchemaProperty;
import org.raml.schema.model.ISchemaType;
import org.raml.schema.model.SimpleType;
import org.raml.schema.model.impl.TypeModelImpl;
import org.raml.schema.model.serializer.ISerializationNode;
import org.raml.schema.model.serializer.StructuredModelSerializer;

import com.mulesoft.jaxrs.raml.annotation.model.StructureType;

public class JsonModelSerializer extends StructuredModelSerializer {

	@Override
	protected ISerializationNode createNode(ISchemaType type, ISchemaProperty prop, ISerializationNode parent) {		
		return new Node(type,prop);
	}
	
	private static class Node implements ISerializationNode {

		public Node(ISchemaType type, ISchemaProperty prop) {
			
			this.structureType = prop!=null ? prop.getStructureType() : type.getParentStructureType();
			if(structureType==StructureType.COLLECTION){
				this.array = new JSONArray();
				if(type.isComplex()){
					array.put(new JSONObject());
					array.put(new JSONObject());
				}
				else{
					Object defaultValue = prop!=null
							? DefaultValueFactory.getDefaultValue(prop)
							: DefaultValueFactory.getDefaultValue(type);
					array.put(defaultValue);
					array.put(defaultValue);
				}
			}
			else if(structureType==StructureType.MAP){
				this.object = new JSONObject();
				
				ISchemaType keyType = SimpleType.STRING;
				ISchemaType valueType = new TypeModelImpl("Object", "java.lang.Object", null, StructureType.COMMON,null);
				if(prop!=null && prop instanceof IMapSchemaProperty){
					keyType = ((IMapSchemaProperty)prop).getKeyType();
					valueType = ((IMapSchemaProperty)prop).getValueType();
					if(!keyType.getClassName().equals(SimpleType.STRING.getClassName())){
						StringBuilder bld = new StringBuilder("Invalid map key type. Only String is available as key type.");
						if(type!=null){
							bld.append(" Type: " + type.getClassQualifiedName());
						}
						if(prop!=null){
							bld.append(" Property: " + prop.getName());
						}
						throw new IllegalArgumentException(bld.toString());
					}
				}
				String key = DefaultValueFactory.getDefaultValue(keyType).toString();
				try {
					if (valueType.isComplex()) {
						this.object.put(key + "_1", new JSONObject());
						this.object.put(key + "_2", new JSONObject());
					} else {
						Object defaultValue = DefaultValueFactory.getDefaultValue(valueType);
						this.object.put(key + "_1", defaultValue);
						this.object.put(key + "_2", defaultValue);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			else if(type.isComplex()){
				this.object = new JSONObject();
			}
		}

		private JSONObject object;
	
		private JSONArray array;
		
		private StructureType structureType;

		@Override
		public void processProperty(ISchemaType type,ISchemaProperty prop, ISerializationNode childNode, Set<String> processedTypes) {
			
			if(this.structureType == StructureType.COLLECTION){
				int l = this.array.length();
				for(int i = 0 ; i < l ; i++){
					JSONObject item;
					try {
						item = (JSONObject) this.array.get(i);
						appendProperty(item,type,prop,childNode);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			else if(this.structureType==StructureType.MAP){
				try {
					for (Iterator<?> iter = this.object.keys(); iter.hasNext();) {
						String key = iter.next().toString();
						JSONObject value = this.object.getJSONObject(key);
						appendProperty(value, type, prop, childNode);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			else{
				appendProperty(this.object,type,prop, childNode);
			}
		}

		private void appendProperty(JSONObject item, ISchemaType type, ISchemaProperty prop,ISerializationNode childNode)
		{
			Node n = (Node) childNode;
			ISchemaType propType = prop.getType();
			String propName = type.getQualifiedPropertyName(prop);
			try {
				if(prop.isAttribute()){
					propName = "@" + propName;
					if(prop.getStructureType()==StructureType.MAP){
						IMapSchemaProperty mapProp = (IMapSchemaProperty) prop;
						Object defaultValue = DefaultValueFactory.getDefaultValue(mapProp.getValueType());
						item.put(propName+"_1", defaultValue+"_1");
						item.put(propName+"_2", defaultValue+"_2");
					}
					else{
						Object defaultValue = DefaultValueFactory.getDefaultValue(propType);
						item.put(propName, defaultValue);
					}
				}				
				else if(prop.getStructureType()==StructureType.COLLECTION){
					item.put(propName, n.array);					
				}
				else if(propType==null||propType.isComplex()){
					item.put(propName, n.object);
				}
				else { 
					Object defaultValue = DefaultValueFactory.getDefaultValue(prop);
					item.put(propName, defaultValue);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public String getStringValue() {

			if (this.object != null) {
				return JsonFormatter.format(StringEscapeUtils.unescapeJavaScript(this.object.toString()));
			} else if (this.array != null) {
				return JsonFormatter.format(StringEscapeUtils.unescapeJavaScript(this.array.toString()));
			} else {
				return null;
			}
		}

	}


}
