package com.mulesoft.jaxrs.raml.jaxb;

import java.io.StringWriter;
import java.util.HashMap;

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

/**
 * <p>XMLWriter class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class XMLWriter implements IExampleWriter{

	Document document;
	private Element currentElement;
	
	/**
	 * <p>Constructor for XMLWriter.</p>
	 */
	public XMLWriter() {
		try {
			document=DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException();
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
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
	
	/** {@inheritDoc} */
	@Override
	public void startEntity(String xmlName) {
		Element newElement = document.createElement(xmlName);
		onElement(newElement);
	}
	

	/** {@inheritDoc} */
	@Override
	public void endEntity(String xmlName) {
		Node parentNode = currentElement.getParentNode();
		if (parentNode instanceof Element){
		currentElement=(Element) parentNode;
		}
	}

	/** {@inheritDoc} */
	@Override
	public void generateAttribute(String name, Class<?> type, boolean required) {
		currentElement.setAttribute(name, getValueString(type));
	}

	private String getValueString(Class<?> type) {
		if (type==byte[].class){
			return "some base64 encoded binary";
		}
		if (type!=null){
				return "some "+type.getSimpleName().toLowerCase()+" value";
		}
		return "some value";
	}


	/** {@inheritDoc} */
	@Override
	public void generateElement(String name, Class<?> type, boolean required) {
		Element newElement = document.createElement(name);
		newElement.setTextContent(getValueString(type));
		currentElement.appendChild(newElement);
	}

	/** {@inheritDoc} */
	@Override
	public void addValueSample(Class<?> type, boolean required) {
		currentElement.setTextContent(getValueString(type));
	}

	/** {@inheritDoc} */
	@Override
	public void startEntityAndDeclareNamespaces(String xmlName,
			HashMap<String, String> prefixes) {
		Element newElement = document.createElement(xmlName);
		for (String url:prefixes.keySet()){
			newElement.setAttribute("xmlns:"+prefixes.get(url),url);
		}
		onElement(newElement);
	}

	private void onElement(Element newElement) {
		if (currentElement==null){
			document.appendChild(newElement);
		}
		else{
			currentElement.appendChild(newElement);
		}
		currentElement=newElement;
	}
}
