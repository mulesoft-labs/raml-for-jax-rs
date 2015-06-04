package com.mulesoft.jaxrs.raml.jsonschema;

import java.util.HashMap;

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.raml.schema.model.ISchemaProperty;
import org.raml.schema.model.ISchemaType;
import org.raml.schema.model.SimpleType;
import org.raml.schema.model.serializer.ISerializationNode;
import org.raml.schema.model.serializer.StructuredModelSerializer;

public class JsonSchemaModelSerializer extends StructuredModelSerializer {
	
	private static final HashMap<SimpleType,String> typeMap = new HashMap<SimpleType, String>();
	static{
		
		typeMap.put(SimpleType.INTEGER  ,"number");
		typeMap.put(SimpleType.LONG     ,"number");
		typeMap.put(SimpleType.SHORT    ,"number");
		typeMap.put(SimpleType.BYTE     ,"number");
		typeMap.put(SimpleType.DOUBLE   ,"number");
		typeMap.put(SimpleType.FLOAT    ,"number");
		typeMap.put(SimpleType.BOOLEAN  ,"boolean");
		typeMap.put(SimpleType.CHARACTER,"string");
		typeMap.put(SimpleType.STRING   ,"string");
	}

	@Override
	protected ISerializationNode createNode(ISchemaType type, ISchemaProperty prop, ISerializationNode parent) {
		return new Node(type,prop);
	}
	
	private static class Node implements ISerializationNode {
		
		public Node(ISchemaType type, ISchemaProperty prop) {
			this.object = new JSONObject();
			try {
				if (prop != null) {
					if(prop.isCollection()){
						setType("array");
						this.isArray = true;
					}
					else{
						String typeString = detectType(type);
						setType(typeString);
					}
					object.put("required",prop.isRequired());
					
				} else {
					object.put("$schema","http://json-schema.org/draft-03/schema");					
					String typeString = detectType(type);
					setType(typeString);
					object.put("required",true);					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


		private JSONObject object;
		
		private boolean isArray=false;


		@Override
		public void processProperty(ISchemaProperty prop, ISerializationNode childNode) {
			
			try {
				JSONObject childObject = ((Node)childNode).object;
				if (this.isArray) {
					JSONArray items = null;
					try{
						items = this.object.getJSONArray("items");
					}
					catch(JSONException ex){
						items = new JSONArray();
						this.object.put("items", items);
					}
					items.put(childObject);
				} else {
					JSONObject properties = null;
					try{
						 properties = this.object.getJSONObject("properties");
					}
					catch(JSONException ex){
						properties = new JSONObject();
						this.object.put("properties", properties);
					}
					String propName = prop.getName();
					if(prop.isAttribute()){
						propName = "@" + propName;
					}
					properties.put(propName, childObject);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public String getStringValue() {

			if (this.object != null) {
				return JsonFormatter.format(StringEscapeUtils.unescapeJavaScript(this.object.toString()));
			} else {
				return null;
			}
		}
		
		private String detectType(ISchemaType type) {
			
			if(type instanceof SimpleType){
				return typeMap.get((SimpleType)type);
			}
			return "object";
		}

		private void setType(String type) throws JSONException {
			this.object.put("type", type);
		}
	}
}
