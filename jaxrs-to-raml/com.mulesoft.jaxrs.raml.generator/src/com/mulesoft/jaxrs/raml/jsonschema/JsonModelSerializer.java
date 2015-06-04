package com.mulesoft.jaxrs.raml.jsonschema;

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.raml.schema.model.DefaultValueFactory;
import org.raml.schema.model.ISchemaProperty;
import org.raml.schema.model.ISchemaType;
import org.raml.schema.model.serializer.ISerializationNode;
import org.raml.schema.model.serializer.StructuredModelSerializer;

public class JsonModelSerializer extends StructuredModelSerializer {

	@Override
	protected ISerializationNode createNode(ISchemaType type, ISchemaProperty prop, ISerializationNode parent) {
		boolean isArray = prop != null ? prop.isCollection() : false;
		return new Node(type,isArray);
	}
	
	private static class Node implements ISerializationNode {

		public Node(ISchemaType type, boolean isArray) {
			if(isArray){
				this.array = new JSONArray();
				if(type.isComplex()){
					array.put(new JSONObject());
					array.put(new JSONObject());
				}
				else{
					array.put(DefaultValueFactory.getDefaultValue(type));
					array.put(DefaultValueFactory.getDefaultValue(type));
				}
			}
			else if(type.isComplex()){
				this.object = new JSONObject();
			}
		}

		private JSONObject object;
		
		private JSONArray array;

		@Override
		public void processProperty(ISchemaProperty prop, ISerializationNode childNode) {
			
			if(this.array!=null){
				int l = this.array.length();
				for(int i = 0 ; i < l ; i++){
					JSONObject item;
					try {
						item = (JSONObject) this.array.get(i);
						appendProperty(item,prop,childNode);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			else{
				appendProperty(this.object, prop, childNode);
			}
		}

		private void appendProperty(JSONObject item, ISchemaProperty prop,ISerializationNode childNode)
		{
			Node n = (Node) childNode;
			ISchemaType type = prop.getType();
			String propName = prop.getName();
			try {				
				if(prop.isCollection()){
					item.put(propName, n.array);					
				}
				else if(type.isComplex()){
					item.put(propName, n.object);
				}
				else{
					if(prop.isAttribute()){
						propName = "@" + propName;
					}
					item.put(propName, DefaultValueFactory.getDefaultValue(type));
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
