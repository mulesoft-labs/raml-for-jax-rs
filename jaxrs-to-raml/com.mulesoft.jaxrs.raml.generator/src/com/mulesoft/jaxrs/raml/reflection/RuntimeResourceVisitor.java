package com.mulesoft.jaxrs.raml.reflection;

import java.io.File;

import org.aml.typesystem.ITypeModel;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mulesoft.jaxrs.raml.IRamlConfig;
import com.mulesoft.jaxrs.raml.ResourceVisitor;
import com.mulesoft.jaxrs.raml.jsonschema.JsonFormatter;
import com.mulesoft.jaxrs.raml.jsonschema.JsonUtil;

/**
 * <p>RuntimeResourceVisitor class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class RuntimeResourceVisitor extends ResourceVisitor {


	/**
	 * <p>Constructor for RuntimeResourceVisitor.</p>
	 *
	 * @param outputFile a {@link java.io.File} object.
	 * @param classLoader a {@link java.lang.ClassLoader} object.
	 */
	public RuntimeResourceVisitor(File outputFile, ClassLoader classLoader) {
		super(outputFile, classLoader);
	}

	/**
	 * <p>Constructor for RuntimeResourceVisitor.</p>
	 *
	 * @param outputFile a {@link java.io.File} object.
	 * @param classLoader a {@link java.lang.ClassLoader} object.
	 * @param preferencesConfig a {@link IRamlConfig} object.
	 */
	public RuntimeResourceVisitor(File outputFile, ClassLoader classLoader, IRamlConfig preferencesConfig) {
		super(outputFile, classLoader);
		setPreferences(preferencesConfig);
	}

	/**
	 * <p>getProperJSONExampleFromXML.</p>
	 *
	 * @param generateXMLExampleJAXB a {@link java.lang.String} object.
	 * @param t 
	 * @return a {@link java.lang.String} object.
	 */
	protected String getProperJSONExampleFromXML(String generateXMLExampleJAXB, ITypeModel t) {		
		
		String jsonText = JsonUtil.convertToJSON(generateXMLExampleJAXB, true);
		JSONObject c;
		try {
			c = new JSONObject(jsonText);
			jsonText=c.get((String) c.keys().next()).toString();
		} catch (JSONException e) {
			//should never happen
			throw new IllegalStateException(e);
		}
		jsonText = JsonFormatter.format(jsonText);
		return jsonText;
	}
	
	/** {@inheritDoc} */
//	@Override
//	protected boolean generateType(ITypeModel t, StructureType st) {
//		String name = t.getFullyQualifiedName();
//		if(name.equals("void")||name.equals("java.lang.Void")){
//			return false;
//		}
//		Class<?> element = null;
//		if (t instanceof ReflectionType) {
//			element = ((ReflectionType) t).getElement();
//			
//		}
//		else if (t.getFullyQualifiedName() != null && classLoader != null) {
//			try {
//				element = classLoader.loadClass(t.getFullyQualifiedName());				
//			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
//			}
//		}
//		if(element==null){
//			return false;
//		}
//		if(st == null || st == StructureType.COMMON){			
//			generateXSDForClass(element);
//		}
//		afterSchemaGen(t,st);
//		return true;
//	}


	
	/**
	 * <p>createResourceVisitor.</p>
	 *
	 * @return a {@link ResourceVisitor} object.
	 */
	protected ResourceVisitor createResourceVisitor() {
		return new RuntimeResourceVisitor(outputFile, classLoader);
	}
}