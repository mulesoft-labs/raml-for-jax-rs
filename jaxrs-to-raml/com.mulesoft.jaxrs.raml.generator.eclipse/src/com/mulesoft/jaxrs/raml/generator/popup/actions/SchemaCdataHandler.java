package com.mulesoft.jaxrs.raml.generator.popup.actions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class SchemaCdataHandler extends AbstractSchemaHandler {

	private static final String NAME_ATTRIBUTE_NAME = "name";

	private static final String CDATA_FILE_SUFFIX = "_CDATA.";

	private static final String CDATA_INSIDE = "-CDATA-INSIDE";

	

	public SchemaCdataHandler(String originalSchemaURI) {
		super(originalSchemaURI);
	}

	public String enhanceGeneratedXML(String generatedXML) {
		if (enhancedSchema == null || !enhancedSchema.exists()) {
			return generatedXML;
		}
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			dbf.setNamespaceAware(true);
			Document doc = db.parse(new ByteArrayInputStream(generatedXML.getBytes("UTF-8")));
			
			XPathFactory factory = XPathFactory.newInstance();
			

			XPath xpath = factory.newXPath();
			xpath.setNamespaceContext(new NamespaceContext() {
				public Iterator getPrefixes(String namespaceURI) 
				{
					return null;
				}
				public String getPrefix(String namespaceURI) 
				{
					return "xs";
				}
				public String getNamespaceURI(String prefix) {
					return "http://www.w3.org/2001/XMLSchema";
				}
			}
			);

			XPathExpression expression = xpath.compile("//*[substring(name(),string-length(name())-string-length('-CDATA-INSIDE')+1)='-CDATA-INSIDE']");
			Object result = expression.evaluate(doc, XPathConstants.NODESET);
			List<Node> elements = null;
			
			if (result instanceof NodeList){
				NodeList nodes = (NodeList) result;
				elements = new ArrayList<Node>();
				for (int i = 0; i < nodes.getLength(); i++) {
					elements.add(nodes.item(i));
				}
			}
			else if (result instanceof List){
				elements = (List) result;
			}
			else {
				return generatedXML;
			}
			
			if (elements.size() == 0) {
				return generatedXML;
			}
			
			for (Node toRename : elements) {
				String elementName = toRename.getNodeName();
				doc.renameNode(toRename, toRename.getNamespaceURI(), 
						toRename.getNodeName().substring(0, elementName.length() - CDATA_INSIDE.length()));
				toRename.appendChild(doc.createCDATASection(" "));
			}
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			//transformer.setOutputProperty(OutputKeys.METHOD, "html");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			
			
			DOMSource domSource = new DOMSource(doc);
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			StreamResult streamResult = new StreamResult(outStream);
			transformer.transform(domSource, streamResult);
			
			String resultXml = (new String(outStream.toByteArray()));
			
			//converting self-closing elements created by transformer back to <element></element>
			//I don't like this solution, but the only other way I found was to use "html" mode, which inserts empty lines and 
			//ignores OMIT_XML_DECLARATION=="no"
			resultXml = resultXml.replaceAll("<([^\\s/]*)([^>]*)/>", "<$1$2></$1>");
			return resultXml;
		} catch (IOException ex){
			ex.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
		return generatedXML;
	}

	public void cleanup() {
		if (enhancedSchema != null && enhancedSchema.exists()) {
			enhancedSchema.delete();
		}
	}

	protected File enhanceSchema(String originalSchemaURI2) {

		File originalSchemaFile = uriToFile(originalSchemaURI2);
		if (originalSchemaFile == null || !originalSchemaFile.exists()) {
			return null;
		}

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			dbf.setNamespaceAware(true);
			Document doc = db.parse(originalSchemaFile);
			XPathFactory factory = XPathFactory.newInstance();
			

			XPath xpath = factory.newXPath();
			xpath.setNamespaceContext(new NamespaceContext() {
				public Iterator getPrefixes(String namespaceURI) 
				{
					return null;
				}
				public String getPrefix(String namespaceURI) 
				{
					return "xs";
				}
				public String getNamespaceURI(String prefix) {
					return "http://www.w3.org/2001/XMLSchema";
				}
			}
			);

			XPathExpression expression = xpath.compile("//xs:element[./xs:annotation/xs:appinfo/cdata]");
			Object result = expression.evaluate(doc, XPathConstants.NODESET);
			List<Node> elements = null;
			
			if (result instanceof NodeList){
				NodeList nodes = (NodeList) result;
				elements = new ArrayList<Node>();
				for (int i = 0; i < nodes.getLength(); i++) {
					elements.add(nodes.item(i));
				}
			}
			else if (result instanceof List){
				elements = (List) result;
			}
			else {
				return null;
			}
			
			if (elements.size() == 0) {
				return null;
			}
			
			for (Node element : elements) {
				renameElement(element, doc, CDATA_INSIDE);
			}
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			
			File toWriteTo = getEnhancedFile(originalSchemaFile);
			if (!toWriteTo.exists()) {
				toWriteTo.createNewFile();
			}
			
			DOMSource domSource = new DOMSource(doc);
			StreamResult streamResult = new StreamResult(new FileOutputStream(toWriteTo));
			transformer.transform(domSource, streamResult);
			
			return toWriteTo;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	private static final File getEnhancedFile(File originalFile) {
		IPath filePath = new Path(originalFile.getAbsolutePath());
		String fileName = filePath.lastSegment();
		String fileExtension = filePath.getFileExtension();
		String newFileName = fileName.substring(0, fileName.length() - (fileExtension.length()+1)) + CDATA_FILE_SUFFIX + fileExtension;
		IPath newFilePath = filePath.removeLastSegments(1).append(newFileName);
		return newFilePath.toFile();
	}
	
	private static final void renameElement(Node element, Document document, String suffix) {
		if (!(element instanceof Element)) {
			return;
		}
        Element elem = (Element)element;
        Node nameAttribute = elem.getAttributeNode(NAME_ATTRIBUTE_NAME);
        if (nameAttribute == null){
        	return;
        }
        String oldName = nameAttribute.getNodeValue();
        String newName = oldName + suffix;
        nameAttribute.setNodeValue(newName);
	}
}
