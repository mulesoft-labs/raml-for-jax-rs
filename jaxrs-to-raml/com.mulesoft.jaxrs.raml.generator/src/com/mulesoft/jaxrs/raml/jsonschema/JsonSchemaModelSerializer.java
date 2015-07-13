package com.mulesoft.jaxrs.raml.jsonschema;

import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.raml.schema.model.IMapSchemaProperty;
import org.raml.schema.model.ISchemaProperty;
import org.raml.schema.model.ISchemaType;
import org.raml.schema.model.SimpleType;
import org.raml.schema.model.serializer.ISerializationNode;
import org.raml.schema.model.serializer.StructuredModelSerializer;

import com.mulesoft.jaxrs.raml.annotation.model.StructureType;

public class JsonSchemaModelSerializer extends StructuredModelSerializer {
	
	private static final HashMap<String,String> typeMap = new HashMap<String, String>();
	static{
		
		typeMap.put(SimpleType.INTEGER.getClassName()  ,"number");
		typeMap.put(SimpleType.LONG.getClassName()     ,"number");
		typeMap.put(SimpleType.SHORT.getClassName()    ,"number");
		typeMap.put(SimpleType.BYTE.getClassName()     ,"number");
		typeMap.put(SimpleType.DOUBLE.getClassName()   ,"number");
		typeMap.put(SimpleType.FLOAT.getClassName()    ,"number");
		typeMap.put(SimpleType.BOOLEAN.getClassName()  ,"boolean");
		typeMap.put(SimpleType.CHARACTER.getClassName(),"string");
		typeMap.put(SimpleType.STRING.getClassName()   ,"string");
	}

	@Override
	protected ISerializationNode createNode(ISchemaType type, ISchemaProperty prop, ISerializationNode parent) {
		return new Node(type,prop);
	}
	
	private static class Node implements ISerializationNode {
		
		private static final String ITEMS = "items";

		private static final String PROPERTIES = "properties";

		private static final String PATTERN_PROPERTIES = "patternProperties";

		private static final String DEFAULT_REGEXP = "[a-zA-Z0-9]+";


		public Node(ISchemaType type, ISchemaProperty prop) {
			this.object = new JSONObject();						
			try {				
				if(prop==null) {
					this.isRootArray = type.getParentStructureType() == StructureType.COLLECTION;
					this.isRootMap = type.getParentStructureType() == StructureType.MAP;
					object.put("$schema","http://json-schema.org/draft-03/schema");
					if(this.isRootArray){
						setType("array");
					}
					else{
						setType("object");				
					}
					object.put("required",true);					
				}
				else{
					this.isGeneric = prop.isGeneric();
					object.put("required",prop.isRequired());					
					StructureType st = prop.getStructureType();
					String typeString = null;
					if(st==StructureType.MAP){
						ISchemaType keyType = SimpleType.STRING;
						typeString = "object";
						if(prop instanceof IMapSchemaProperty){
							IMapSchemaProperty msp = (IMapSchemaProperty) prop;
							keyType = msp.getKeyType();
							typeString = detectType(msp.getValueType(),null);
						}
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
					else{
						typeString = detectType(type,prop);
					}
					setType(typeString);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}


		private JSONObject object;
		
		private boolean isRootArray=false;
		
		private boolean isRootMap=false;
		
		private boolean isGeneric = false;


		@Override
		public void processProperty(ISchemaType type, ISchemaProperty prop, ISerializationNode childNode, Set<String> processedTypes) {
			
			if(this.isGeneric){
				return;
			}
			if(prop.isAttribute()&&prop.getStructureType()==StructureType.MAP){
				try {
					this.object.getJSONObject(PATTERN_PROPERTIES);
					return;
				} catch (JSONException e) {}
				JSONObject patternProperties = new JSONObject();
				try {
					this.object.put(PATTERN_PROPERTIES, patternProperties);
					JSONObject property = new JSONObject();
					property.put("type","string");
					property.put("required",false);
					patternProperties.put("@"+DEFAULT_REGEXP, property);
				} catch (JSONException e) {}
				return;
			}
			
			String propName = type.getQualifiedPropertyName(prop);
			if(prop.isAttribute()){
				propName = "@" + propName;
			}
			
			try {
				JSONObject childObject = ((Node)childNode).object;
				
				StructureType st = prop.getStructureType();				
				JSONObject actualObject = null;
				if (this.isRootArray) {
					JSONObject item = null;
					JSONArray items = null;
					try{
						items = this.object.getJSONArray(ITEMS);
					}
					catch(JSONException ex){
						items = new JSONArray();
						this.object.put(ITEMS, items);
					}
					try{
						item = items.getJSONObject(0);
					}
					catch(JSONException ex){
						item = new JSONObject();
						items.put(item);
					}
					actualObject = item;
				}
				else if(this.isRootMap){
					JSONObject patternProperties = null;
					try{
						 patternProperties = this.object.getJSONObject(PATTERN_PROPERTIES);
					}
					catch(JSONException ex){
						patternProperties = new JSONObject();
						this.object.put(PATTERN_PROPERTIES, patternProperties);
					}
					JSONObject property = null;
					try{
						 property = patternProperties.getJSONObject(DEFAULT_REGEXP);
					}
					catch(JSONException ex){
						property = new JSONObject();
						property.put("type","object");
						property.put("required",false);
						patternProperties.put(DEFAULT_REGEXP, property);
					}
					actualObject = property;					
				}
				else {					
					actualObject = this.object;					
				}
				
				actualObject.put("type","object");
				
				JSONObject properties = null;
				try{
					 properties = actualObject.getJSONObject(PROPERTIES);
				}
				catch(JSONException ex){
					properties = new JSONObject();
					actualObject.put(PROPERTIES, properties);
				}
				if(st==StructureType.COLLECTION){
					JSONObject propObject;
					try{
						propObject = properties.getJSONObject(propName);
					}
					catch(JSONException e){
						propObject = new JSONObject();						
						propObject.put("type","array");
						propObject.put("required",false);
						properties.put(propName, propObject);
					}
					JSONArray items = null;
					try{
						items = propObject.getJSONArray(ITEMS);
					}
					catch(JSONException ex){
						items = new JSONArray();
						propObject.put(ITEMS, items);
					}
					items.put(childObject);
				}
				else if(st==StructureType.MAP){
					JSONObject propObject;
					try{
						propObject = properties.getJSONObject(propName);
					}
					catch(JSONException e){
						propObject = new JSONObject();						
						propObject.put("type","object");
						propObject.put("required",false);
						properties.put(propName, propObject);
					}					
					JSONObject patternProperties = null;
					try{
						 patternProperties = propObject.getJSONObject(PATTERN_PROPERTIES);
					}
					catch(JSONException ex){
						patternProperties = new JSONObject();
						propObject.put(PATTERN_PROPERTIES, patternProperties);
					}
					patternProperties.put(DEFAULT_REGEXP, childObject);							
				} else {
					properties.put(propName, childObject);
				}
				
			} catch (JSONException e) {
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
		
		private String detectType(ISchemaType type, ISchemaProperty prop) {
			
			if(prop!=null&&prop.isGeneric()){
				return "object";
			}
			
			if(type.isSimple()){
				return typeMap.get(type.getClassName());
			}
			return "object";
		}

		private void setType(String type) throws JSONException {
			this.object.put("type", type);
		}
	}
}
