package com.mulesoft.jaxrs.raml.jaxb;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLWriter implements IExampleWriter{

	Document document;
	private Element currentElement;
	
	public XMLWriter() {
		try {
			document=DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException();
		}
	}
	
	@Override
	public String toString() {
		try{
		Transformer newTransformer = TransformerFactory.newInstance().newTransformer();
		newTransformer.setOutputProperty(OutputKeys.INDENT,"yes");
		StringWriter writer = new StringWriter();
		newTransformer.transform(new DOMSource(document),new StreamResult(writer));
		writer.close();
		return writer.toString();
		}catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	@Override
	public void startEntity(String xmlName) {
		Element newElement = document.createElement(xmlName);
		if (currentElement==null){
			document.appendChild(newElement);
		}
		else{
			currentElement.appendChild(newElement);
		}
		currentElement=newElement;
	}
	

	@Override
	public void endEntity(String xmlName) {
		Node parentNode = currentElement.getParentNode();
		if (parentNode instanceof Element){
		currentElement=(Element) parentNode;
		}
	}

	@Override
	public void generateAttribute(String name, Class<?> type) {
		currentElement.setAttribute(name, getValueString(type));
	}

	private String getValueString(Class<?> type) {
		return "some value";
	}


	@Override
	public void generateElement(String name, Class<?> type) {
		Element newElement = document.createElement(name);
		newElement.setTextContent(getValueString(type));
		currentElement.appendChild(newElement);
	}

	@Override
	public void addValueSample(Class<?> type) {
		currentElement.setTextContent(getValueString(type));
	}
}
