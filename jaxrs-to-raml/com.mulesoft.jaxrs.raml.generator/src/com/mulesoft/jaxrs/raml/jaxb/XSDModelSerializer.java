package com.mulesoft.jaxrs.raml.jaxb;

import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.raml.schema.model.ISchemaProperty;
import org.raml.schema.model.ISchemaType;
import org.raml.schema.model.serializer.IModelSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mulesoft.jaxrs.raml.annotation.model.StructureType;

public class XSDModelSerializer implements IModelSerializer {

	@Override
	public String serialize(ISchemaType type) {
		Element rootElement;
		try {
			rootElement = createRootElement();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
		appendElement(type,rootElement,null);
		appendType(type,rootElement);
		
		return getStringValue(rootElement.getOwnerDocument());
	}
	
	private void appendType(ISchemaType type, Element rootElement) {
		Element typeElement = rootElement.getOwnerDocument().createElement("xs:complexType");
		rootElement.appendChild(typeElement);
		
		List<ISchemaProperty> properties = type.getProperties();
		if(properties!=null){
			Element sequenceElement = rootElement.getOwnerDocument().createElement("xs:complexType");
			typeElement.appendChild(sequenceElement);
			for(ISchemaProperty prop : properties){
				ISchemaType propType = prop.getType();
				appendElement(propType, sequenceElement, prop);
				if(!propType.isSimple()){
					appendType(propType, rootElement);
				}
			}
		}
	}

	private void appendElement(ISchemaType type, Element parent, ISchemaProperty prop)
	{
		String typeName = type.getClassName().toLowerCase();
		if(prop==null){
			Element child = parent.getOwnerDocument().createElement("xs:element");
			parent.appendChild(child);
			child.setAttribute("name", type.getName());
			child.setAttribute("type", typeName);
		}
		else{
			if(prop.isGeneric()){
				typeName = "xs:anyType";
			}
			if(prop.isAttribute()){
				Element child = parent.getOwnerDocument().createElement("xs:attribute");
				parent.appendChild(child);
				String name = type.getQualifiedPropertyName(prop);
				child.setAttribute("name", name);
				child.setAttribute("type", typeName);
				if(prop.isRequired()){
					child.setAttribute("use", "required");
				}
			}
			else{
				Element child = parent.getOwnerDocument().createElement("xs:element");
				parent.appendChild(child);
				String name = type.getQualifiedPropertyName(prop);
				child.setAttribute("name", name);
				child.setAttribute("type", typeName);
				if(!prop.isRequired()){
					child.setAttribute("minOccurs", "0");
				}
				if(prop.getStructureType()==StructureType.COLLECTION){
					child.setAttribute("nillable", "true");
					child.setAttribute("maxOccurs", "unbounded");
				}
			}
		}
	}

	private Element createRootElement() throws ParserConfigurationException {
		Document document = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();
		
		Element rootElement = document.createElement("xs:schema");
		document.appendChild(rootElement);
		rootElement.setAttribute("version", "1.0");
		rootElement.setAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema");
		return rootElement;
	}
	
	private String getStringValue(Document document) {
		
		try{
			TransformerFactory newInstance = TransformerFactory.newInstance();
			newInstance.setAttribute("indent-number", 4);
			Transformer newTransformer = newInstance.newTransformer();
			
			newTransformer.setOutputProperty(OutputKeys.INDENT,"yes");
			
			StringWriter writer = new StringWriter();
			newTransformer.transform(new DOMSource(document),new StreamResult(writer));
			writer.close();
			return writer.toString();
		}catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
}
