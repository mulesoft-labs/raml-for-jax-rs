package com.mulesoft.jaxrs.raml.jsonschema;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class JsonUtils {
	
	public static final String[] EMPTY_SCHEMA = new String[]{
		"{",
		"  \"type\" : \"object\" ,",
		"  \"required\" : true ,",
		"  \"$schema\" : \"http://json-schema.org/draft-03/schema\" ,",
		"  \"properties\" : {",
		"    ",
		"  }",
		"}"
	};

	public static boolean isJson(String example) {		 
		return example.trim().startsWith("{")||example.trim().startsWith("[");
	}
	
	public static JsonSchemaNode createSchemaNode(String text){
		
		try {
			
			JSONObject object = new JSONObject(text);
			JsonSchemaNode schemaNode = new JsonSchemaNode("", object, null);
			return schemaNode;
			
		} catch (JSONException e) {
		}
		return null;
	}

	static public String transformObjectToString(JSONObject object)
			throws JSONException, IOException
	{
		StringWriter sw = new StringWriter() ;
		object.write(sw) ;
		StringWriter sw1 = new StringWriter() ;
		StringEscapeUtils.unescapeJavaScript( sw1, sw.toString() );
		String result = sw1.toString() ;
		return result;
	}
	
	static public String transformObjectToStringAndFormat(JSONObject object)
			throws JSONException, IOException
	{		
		String content = transformObjectToString(object);
		String formatted = JsonFormatter.format(content);
		return formatted;
	}

	public static String getEmptySchema() {
		StringBuilder bld = new StringBuilder();
		for( int i = 0 ; i < EMPTY_SCHEMA.length ; i++ ){
			String str = EMPTY_SCHEMA[i];
			bld.append(str);
			
			if(i != EMPTY_SCHEMA.length-1)
				bld.append("\r\n");
		}
		String result = bld.toString();
		return result;
	}
}
