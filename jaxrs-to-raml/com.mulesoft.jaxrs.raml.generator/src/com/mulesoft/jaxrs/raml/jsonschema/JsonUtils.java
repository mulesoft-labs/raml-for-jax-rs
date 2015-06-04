package com.mulesoft.jaxrs.raml.jsonschema;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * <p>JsonUtils class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class JsonUtils {
	
	/** Constant <code>EMPTY_SCHEMA="new String[]{{,  \"type\" : \"object\" "{trunked}</code> */
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

	/**
	 * <p>isJson.</p>
	 *
	 * @param example a {@link java.lang.String} object.
	 * @return a boolean.
	 */
	public static boolean isJson(String example) {		 
		return example.trim().startsWith("{")||example.trim().startsWith("[");
	}
	
	/**
	 * <p>createSchemaNode.</p>
	 *
	 * @param text a {@link java.lang.String} object.
	 * @return a {@link com.mulesoft.jaxrs.raml.jsonschema.JsonSchemaNode} object.
	 */
	public static JsonSchemaNode createSchemaNode(String text){
		
		try {
			
			JSONObject object = new JSONObject(text);
			JsonSchemaNode schemaNode = new JsonSchemaNode("", object, null);
			return schemaNode;
			
		} catch (JSONException e) {
		}
		return null;
	}

	/**
	 * <p>transformObjectToString.</p>
	 *
	 * @param object a {@link org.codehaus.jettison.json.JSONObject} object.
	 * @return a {@link java.lang.String} object.
	 * @throws org.codehaus.jettison.json.JSONException if any.
	 * @throws java.io.IOException if any.
	 */
	static public String transformObjectToString(JSONObject object)	throws JSONException, IOException
	{
		StringWriter sw = new StringWriter() ;
		object.write(sw) ;
		StringWriter sw1 = new StringWriter() ;
		StringEscapeUtils.unescapeJavaScript( sw1, sw.toString() );
		String result = sw1.toString() ;
		return result;
	}
	
	static public String transformObjectToString(JSONArray object)	throws JSONException, IOException
	{
		StringWriter sw = new StringWriter() ;
		object.write(sw) ;
		StringWriter sw1 = new StringWriter() ;
		StringEscapeUtils.unescapeJavaScript( sw1, sw.toString() );
		String result = sw1.toString() ;
		return result;
	}
	
	/**
	 * <p>transformObjectToStringAndFormat.</p>
	 *
	 * @param object a {@link org.codehaus.jettison.json.JSONObject} object.
	 * @return a {@link java.lang.String} object.
	 * @throws org.codehaus.jettison.json.JSONException if any.
	 * @throws java.io.IOException if any.
	 */
	static public String transformObjectToStringAndFormat(JSONObject object)
			throws JSONException, IOException
	{		
		String content = transformObjectToString(object);
		String formatted = JsonFormatter.format(content);
		return formatted;
	}

	/**
	 * <p>getEmptySchema.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
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
