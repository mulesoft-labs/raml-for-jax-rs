package com.mulesoft.jaxrs.raml.jaxb;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.raml.schema.model.DefaultValueFactory;
import org.raml.schema.model.ISchemaProperty;
import org.raml.schema.model.ISchemaType;
import org.raml.schema.model.serializer.ISerializationNode;
import org.raml.schema.model.serializer.StructuredModelSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLModelSerializer extends StructuredModelSerializer {

	@Override
	protected ISerializationNode createNode(ISchemaType type, ISchemaProperty prop, ISerializationNode parent) {
		
		String name = prop != null ? prop.getName() : type.getName();
		return new Node(name,parent);
	}
	
	private static class Node implements ISerializationNode {

		public Node(String name, ISerializationNode parent) {
			if (parent != null) {
				this.document = ((Node)parent).document;
			} else {
				try {
					this.document = DocumentBuilderFactory.newInstance()
							.newDocumentBuilder().newDocument();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				}
			}
			this.element = this.document.createElement(name);
			if(parent==null){
				this.document.appendChild(this.element);
			}
		}

		private Document document;

		private Element element;

		@Override
		public void processProperty(ISchemaProperty prop, ISerializationNode childNode) {
			
			ISchemaType type = prop.getType();
			if(prop.isAttribute()){			
				this.element.setAttribute(prop.getName(), DefaultValueFactory.getDefaultValue(type).toString());
				return;
			}
			else{
				Element childElement = ((Node)childNode).element;
				if(type.isSimple()){
					childElement.setTextContent(DefaultValueFactory.getDefaultValue(type).toString());
				}			
				this.element.appendChild(childElement);
				if(prop.isCollection()){
					this.element.appendChild(childElement.cloneNode(true));
				}
			}		
		}

		@Override
		public String getStringValue() {
			
			try{
				TransformerFactory newInstance = TransformerFactory.newInstance();
				newInstance.setAttribute("indent-number", 4);
				Transformer newTransformer = newInstance.newTransformer();
				
				newTransformer.setOutputProperty(OutputKeys.INDENT,"yes");
				
				StringWriter writer = new StringWriter();
				newTransformer.transform(new DOMSource(this.document),new StreamResult(writer));
				writer.close();
				return writer.toString();
			}catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}

	}


}
