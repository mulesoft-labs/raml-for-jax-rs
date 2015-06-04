
package com.mulesoft.jaxrs.raml.annotation.model.reflection;

import java.io.File;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.raml.schema.model.ISchemaType;

import com.mulesoft.jaxrs.raml.annotation.model.IRamlConfig;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;
import com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor;
import com.mulesoft.jaxrs.raml.jaxb.JAXBRegistry;
import com.mulesoft.jaxrs.raml.jaxb.JAXBType;
import com.mulesoft.jaxrs.raml.jaxb.SchemaModelBuilder;
import com.mulesoft.jaxrs.raml.jaxb.XMLModelSerializer;
import com.mulesoft.jaxrs.raml.jsonschema.JsonFormatter;
import com.mulesoft.jaxrs.raml.jsonschema.JsonModelSerializer;
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
	 * <p>afterSchemaGen.</p>
	 *
	 * @param t a {@link com.mulesoft.jaxrs.raml.annotation.model.ITypeModel} object.
	 * @param collectionTag 
	 */
	protected void afterSchemaGen(ITypeModel t, String collectionTag) {
		
		String generateXMLExampleJAXB = generateXMLExampleJAXB(t);
		if (generateXMLExampleJAXB!=null){
				File file =outputFile;
				File parentDir = file.getParentFile();
				File examplesDir=new File(parentDir,"examples"); //$NON-NLS-1$
				File schemaFile=new File(parentDir,"schemas"); //$NON-NLS-1$
				if (!examplesDir.exists()){
					examplesDir.mkdir();
				}
				//String dummyXml = generator.generateDummyXmlFor(schemaFile.toURL().toExternalForm());
				writeString(generateXMLExampleJAXB, new File(examplesDir,t.getName()+".xml"));
				String jsonText = getProperJSONExampleFromXML(generateXMLExampleJAXB,t);
				writeString(jsonText, new File(examplesDir,t.getName().toLowerCase()+".json"));
				String generatedSchema = jsonText != null ? new SchemaGenerator().generateSchema(jsonText) : null;
				generatedSchema = generatedSchema != null ? JsonFormatter.format(generatedSchema) : null;
				if(generatedSchema != null){
					String schemaName = t.getName().toLowerCase()+"-jsonschema";
					spec.getCoreRaml().addGlobalSchema(schemaName, generatedSchema, true, false);
					writeString(generatedSchema, new File(schemaFile,schemaName+".json"));
				}
		}
	}

	/**
	 * <p>getProperJSONExampleFromXML.</p>
	 *
	 * @param generateXMLExampleJAXB a {@link java.lang.String} object.
	 * @param t 
	 * @return a {@link java.lang.String} object.
	 */
	protected String getProperJSONExampleFromXML(String generateXMLExampleJAXB, ITypeModel t) {		
		
		JAXBRegistry rs=new JAXBRegistry();
		JAXBType jaxbModel = rs.getJAXBModel(t);
		if (jaxbModel!=null){
			return new JsonModelSerializer().serialize(new SchemaModelBuilder().buildSchemaModel(jaxbModel));
		}
		return null;
//		String jsonText = JsonUtil.convertToJSON(generateXMLExampleJAXB, true);
//		JSONObject c;
//		try {
//			c = new JSONObject(jsonText);
//			jsonText=c.get((String) c.keys().next()).toString();
//		} catch (JSONException e) {
//			//should never happen
//			throw new IllegalStateException(e);
//		}
//		jsonText = JsonFormatter.format(jsonText);
//		return jsonText;
	}
	
	/** {@inheritDoc} */
	protected void generateXMLSchema(ITypeModel t, String collectionTag) {
		if (t instanceof ReflectionType) {
			Class<?> element = ((ReflectionType) t).getElement();
			generateXSDForClass(element);
		}
		else if (t.getFullyQualifiedName() != null && classLoader != null) {
			try {
				Class<?> element = classLoader.loadClass(t.getFullyQualifiedName());
				generateXSDForClass(element);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		afterSchemaGen(t,collectionTag);
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
