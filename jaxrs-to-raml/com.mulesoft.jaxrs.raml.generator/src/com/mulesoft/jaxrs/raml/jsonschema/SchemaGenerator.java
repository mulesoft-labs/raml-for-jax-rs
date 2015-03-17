package com.mulesoft.jaxrs.raml.jsonschema;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * <p>SchemaGenerator class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class SchemaGenerator {
	
	
	/**
	 * <p>main.</p>
	 *
	 * @param args an array of {@link java.lang.String} objects.
	 */
	public static void main(String[] args){
		
		String in="C:/workspaces/RAML-100apis/100apis/salesforce/examples";		
		String out="C:/workspaces/RAML-100apis/100apis/salesforce/schemes";
		
		new SchemaGenerator().processDir(in, out);		
	}
	
	/**
	 * <p>processDir.</p>
	 *
	 * @param src a {@link java.lang.String} object.
	 * @param dst a {@link java.lang.String} object.
	 */
	public void processDir(String src, String dst){
		
		File srcFolder = new File(src);
		if(!srcFolder.exists() || !srcFolder.isDirectory() )
			return;
		
		File dstFolder = new File(dst);
		if(dstFolder.exists() && ! dstFolder.isDirectory())
			return ;
		
		dstFolder.mkdirs();
		
		File[] srcFiles = srcFolder.listFiles( new FilenameFilter() {
			
			
			public boolean accept(File file, String name) {
				return name.endsWith(".json");
			}
		});
		for(File srcFile : srcFiles ){
			String name = srcFile.getName();
			if(name.endsWith("-example.json")){
				name = name.substring(0, name.length()-"-example.json".length())+"-schema.json";
			}
			File dstFile = new File(dstFolder, name);
			String srcPath = srcFile.getAbsolutePath();
			String dstPath = dstFile.getAbsolutePath();
			generateSchema(srcPath, dstPath);
		}
	}
	
	/**
	 * <p>generateSchema.</p>
	 *
	 * @param in a {@link java.lang.String} object.
	 * @param out a {@link java.lang.String} object.
	 */
	public void generateSchema(String in, String out){
		
		File file = new File(in);
		if(!file.exists())
			return;
		
		try {
			FileInputStream fis = new FileInputStream(file);
			DataInputStream dis = new DataInputStream(fis);
			int length = (int) file.length();
			byte[] bArr = new byte[length];
			dis.readFully( bArr );
			String content = new String(bArr,"UTF-8");
			dis.close();
			
			String sch = generateSchema(content);
			if(sch==null)
				return;
			
			sch = JsonFormatter.format(sch);
			if(sch==null)
				return;
			
			File oFile = new File(out);
			if(oFile.exists())
			{
				oFile.delete();
			}
			oFile.createNewFile();
			
			FileOutputStream fos = new FileOutputStream(oFile);
			fos.write( sch.getBytes("UTF-8"));
			fos.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * <p>generateSchema.</p>
	 *
	 * @param content a {@link java.lang.String} object.
	 * @return scehama content
	 */
	public String generateSchema(String content){
		
		try {
			JSONObject sch = new JSONObject();
			sch.put("required", true);
			sch.putOpt("$schema", "http://json-schema.org/draft-03/schema");
			
			if(content.startsWith("{")){
				JSONObject obj = new JSONObject(content);
				sch.put("type", "object");
				pass(obj,sch);
			}
			else if(content.startsWith("[")){
				JSONArray obj = new JSONArray(content);
				sch.put("type", "array");
				passArray(obj,sch);
			}
			else{
				return null;
			}
			String result = JsonUtils.transformObjectToString(sch);			
			return result ;
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void pass(JSONObject obj, JSONObject sch) {
		
		for( Iterator<?> iter = obj.keys() ; iter.hasNext() ; ){
			Object o = iter.next() ;
			String propName = o.toString();
			Object value;
			try {
				value = obj.get(propName);
				registerProperty(propName,value,sch);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		
	}

	private void registerProperty(String propName, Object value, JSONObject sch)
			throws JSONException {
		
		JSONObject properties = null;
		try{
			properties = sch.getJSONObject("properties");
		}
		catch(JSONException e){}
			
		if(properties==null){
			properties = new JSONObject();
			sch.put("properties", properties);
		}
			
		JSONObject property = null;
		try{
			property = properties.getJSONObject(propName);
		}
		catch(JSONException e){}
		
		if(property==null){
			property = new JSONObject();
			properties.put(propName, property);
		}
			
		String type = detectType(value);
		if(type==null){
			System.err.println("undetected type");
			System.err.println(value.toString());
			return;
		}
			
		property.put("type",type);
		property.put("required", false);
			
		if("array".equals(type)){
			passArray((JSONArray) value, property);
		}
		else if("object".equals(type)){
			if(value instanceof JSONObject){
				pass((JSONObject) value,property);
			}
			else if( value.equals(JSONObject.NULL)){
				
			}
		}
	}

	private void passArray(JSONArray array, JSONObject property) throws JSONException {

		JSONArray items = null;
		try{
			items = property.getJSONArray("items");
		}
		catch(JSONException e){}
		
		if(items==null)
		{
			items = new JSONArray();
			property.put("items", items);
		}
		
		int l = array.length();
		LinkedHashSet<JSONObjectWrapper> itemSet = new LinkedHashSet<JSONObjectWrapper>();
		for(int i = 0 ; i < l ; i++ ){
			Object value = array.get(i);
			JSONObject item = new JSONObject();						
			String type = detectType(value);
			if(type==null){
				System.err.println("undetected type for array member");
				System.err.println(value.toString());
				continue;
			}
			item.put("type", type);
			if( value instanceof JSONObject){
				pass((JSONObject)value,item);
			}
			JSONObjectWrapper wrapper = new JSONObjectWrapper(item);
			itemSet.add(wrapper);
		}
		
		for(JSONObjectWrapper wr: itemSet){
			items.put( wr.getObject() );
		}		
	}

	private String detectType(Object value)
	{
		if(value instanceof String){
			return "string";
		}
		if(value instanceof Number){
			return "number";
		}
		if(value instanceof Boolean){
			return "boolean";
		}
		if(value instanceof JSONObject){
			return "object";
		}
		if(value instanceof JSONArray){
			return "array";
		}
		if(value == JSONObject.NULL){
			return "object";
		}
		return null;
	}

}
