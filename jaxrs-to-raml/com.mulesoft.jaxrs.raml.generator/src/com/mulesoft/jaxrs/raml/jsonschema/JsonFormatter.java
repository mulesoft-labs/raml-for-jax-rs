package com.mulesoft.jaxrs.raml.jsonschema;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * <p>JsonFormatter class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class JsonFormatter {
	
	private static final String INDENT_INCREMENT = "  ";
	
	/**
	 * <p>formatExternal.</p>
	 *
	 * @param content a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String formatExternal(String content){
		
		String trim = content.trim();
		String canonic = null;
		try {
			if( trim.startsWith("{") ){	
				JSONObject obj = new JSONObject(content);
				StringWriter wr = new StringWriter(); 
				obj.write(wr);
				canonic = StringEscapeUtils.unescapeJavaScript(wr.toString());
			}
			else if( trim.startsWith("[") ){
				JSONArray obj = new JSONArray(content);
				StringWriter wr = new StringWriter(); 
				obj.write(wr);
				canonic = StringEscapeUtils.unescapeJavaScript(wr.toString());
			}			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(canonic==null)
			return null;
		
		String result = format(canonic);
		return result;
	}

	/**
	 * <p>format.</p>
	 *
	 * @param content a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String format(String content){
		
		if(content==null)
			return null;
		
		Map<Object, String> literalsMap = markLiterals(content);
		String str00=literalsMap.get(1);
		String str01 = str00.replaceAll("\\s", "");
		String str02 = str01.replace("},{","$$$seq2$$$");
		String str03 = str02.replace("},","$$$seq1$$$");
		String str04 = str03.replace("],","$$$seq3$$$");
		String str05 = str04.replaceAll("(\\{|\\[|\\,)", "$1\n");
		String str06 = str05.replaceAll("(\\}|\\])", "$1\n");
		String str07 = str06.replace("$$$seq1$$$", "},\n");
		String str08 = str07.replace("$$$seq3$$$", "],\n");
		String str09 = str08.replace("$$$seq2$$$", "} , {\n");
		String str10 = str09.replaceAll("([^\\s])(\\}|\\])", "$1\n$2");
		String str11 = str10.replace(":", " : ");
		String str12 = str11.replace(",\n", " ,\n");
		String str = insertLiterals(str12, literalsMap);
		String[] arr = str.split("\n");
		
		String indent = "";
		StringBuilder bld = new StringBuilder();
		for(String s : arr){
			if(s.startsWith("} , {")){
				int l = indent.length();
				if(l!=0)
					indent = indent.substring(0, l-INDENT_INCREMENT.length());
				bld.append(indent);
				bld.append(s);
				bld.append("\n");
				indent += INDENT_INCREMENT;
			}
			else if(s.endsWith("{")){
				bld.append(indent);
				bld.append(s);
				bld.append("\n");
				indent += INDENT_INCREMENT;
			}
			else if(s.endsWith("[")){
				bld.append(indent);
				bld.append(s);
				bld.append("\n");
				indent += INDENT_INCREMENT;
			}
			else if(s.endsWith("} ,")){
				int l = indent.length();
				if(l!=0)
					indent = indent.substring(0, l-INDENT_INCREMENT.length());
				
				bld.append(indent);
				bld.append(s);
				bld.append("\n");
			}
			else if(s.endsWith("] ,")){
				int l = indent.length();
				if(l!=0)
					indent = indent.substring(0, l-INDENT_INCREMENT.length());
				
				bld.append(indent);
				bld.append(s);
				bld.append("\n");
			}
			else if(s.endsWith(",")){
				bld.append(indent);
				bld.append(s);
				bld.append("\n");
			}
			else if(s.endsWith("}")){
				int l = indent.length();
				if(l!=0)
					indent = indent.substring(0, l-INDENT_INCREMENT.length());
				
				bld.append(indent);
				bld.append(s);
				bld.append("\n");
			}
			else if(s.endsWith("]")){
				int l = indent.length();
				if(l!=0)
					indent = indent.substring(0, l-INDENT_INCREMENT.length());
				
				bld.append(indent);
				bld.append(s);
				bld.append("\n");	
			}
			else{
				bld.append(indent);
				bld.append(s);
				bld.append("\n");				
			}
		}
		String result = bld.toString().trim();
		
		return result;
	}

	private static String insertLiterals(String string, Map<Object, String> literalsMap)
	{
		String str = string;
		for(Map.Entry<Object, String> entry : literalsMap.entrySet() ){
			Object key = entry.getKey();
			if(!(key instanceof String))
				continue;
			
			String replacement = key.toString();
			String literal = entry.getValue();
			str = str.replace(replacement, literal);
		}
		return str;
	}

	private static Map<Object, String> markLiterals(String content) {		
		
		if( content==null)
			return null;
		
		String replacement="$$$%%%replacement%%%$$$";
		
		HashMap<Object, String> map=new HashMap<Object, String>();
		int prev = 0 ;
		StringBuilder bld = new StringBuilder();
		for(int ind = content.indexOf("\""); ind>=0; ind = content.indexOf("\"",prev) ){
			bld.append( content.substring(prev, ind).replaceAll("\\s", "") );
			prev = content.indexOf("\"",ind+1);
			if(prev<0){
				prev=ind;
				break;
			}
			prev++;
			String literal = content.substring(ind,prev);
			String replStr = replacement+ind+"%%%$$$";
			map.put(replStr, literal);
			bld.append( replStr );
		}
		bld.append( content.substring(prev, content.length()).replaceAll("\\s", "") );
		String result = bld.toString();
		map.put(1, result);
		map.put(0, content);
		return map;
	}
}
