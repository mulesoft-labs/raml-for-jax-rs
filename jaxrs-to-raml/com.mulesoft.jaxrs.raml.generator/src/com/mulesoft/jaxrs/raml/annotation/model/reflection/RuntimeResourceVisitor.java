
package com.mulesoft.jaxrs.raml.annotation.model.reflection;

import java.io.File;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.raml.schema.model.ISchemaType;

import com.mulesoft.jaxrs.raml.annotation.model.IRamlConfig;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;
import com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor;
import com.mulesoft.jaxrs.raml.annotation.model.StructureType;
import com.mulesoft.jaxrs.raml.jaxb.JAXBRegistry;
import com.mulesoft.jaxrs.raml.jaxb.JAXBType;
import com.mulesoft.jaxrs.raml.jaxb.SchemaModelBuilder;
import com.mulesoft.jaxrs.raml.jaxb.XMLModelSerializer;
import com.mulesoft.jaxrs.raml.jsonschema.JsonFormatter;
import com.mulesoft.jaxrs.raml.jsonschema.JsonModelSerializer;
import com.mulesoft.jaxrs.raml.jsonschema.JsonSchemaModelSerializer;
import com.mulesoft.jaxrs.raml.jsonschema.JsonUtil;
import com.mulesoft.jaxrs.raml.jsonschema.SchemaGenerator;

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
	 * @param preferencesConfig a {@link com.mulesoft.jaxrs.raml.annotation.model.IRamlConfig} object.
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
	@Override
	protected boolean generateXMLSchema(ITypeModel t, StructureType st) {
		String name = t.getFullyQualifiedName();
		if(name.equals("void")||name.equals("java.lang.Void")){
			return false;
		}
		Class<?> element = null;
		if (t instanceof ReflectionType) {
			element = ((ReflectionType) t).getElement();
			
		}
		else if (t.getFullyQualifiedName() != null && classLoader != null) {
			try {
				element = classLoader.loadClass(t.getFullyQualifiedName());				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		if(element==null){
			return false;
		}
		if(st == null || st == StructureType.COMMON){
			generateXSDForClass(element);
		}
		afterSchemaGen(t,st);
		return true;
	}


	
	/**
	 * <p>createResourceVisitor.</p>
	 *
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor} object.
	 */
	protected ResourceVisitor createResourceVisitor() {
		return new RuntimeResourceVisitor(outputFile, classLoader);
	}

}
