package com.mulesoft.jaxrs.raml.generator.popup.actions;

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
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class HrefSchemaHandler extends AbstractSchemaHandler{


	public HrefSchemaHandler(String originalSchemaURI) {
		super(originalSchemaURI);
	}

	protected File enhanceSchema(String originalSchemaURI) {
		File originalFile = uriToFile(originalSchemaURI);
		if (originalFile != null && originalFile.exists()) {
			String newFilePath = patchSchemaIfNeeded(originalSchemaURI, originalFile.getAbsolutePath());
			if (newFilePath == null) {
				return null;
			}
			
			return new File(newFilePath);
		}
		
		return null;
	}
	
	//////
	
	private String patchSchemaIfNeeded(String uri, String filePath) {
		File originalFile = new File(filePath);
		//it is assumed, that both uri and filePath end with ".xsd". For now this is always true.
		final String XSD_EXTENSON = ".xsd";
		final String PATCHED_POSTFIX = "PATCHED";
		
		String newFilePath = filePath.substring(0, filePath.length() - XSD_EXTENSON.length());
		newFilePath = newFilePath + PATCHED_POSTFIX + XSD_EXTENSON;
		
//		String newUri = uri.substring(0, uri.length() - XSD_EXTENSON.length());
//		newUri = newUri + PATCHED_POSTFIX + XSD_EXTENSON;
		
		if (doPatchXSD(originalFile, new File(newFilePath))) {
			return newFilePath;
		}
		else {
			return filePath;
		}
	}

	private boolean doPatchXSD(File originalFile, File targetFile) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db;
			try {
				db = dbf.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
				return false;
			}
			
			org.w3c.dom.Document doc = null;
			try {
				doc = db.parse(originalFile);
			} catch (SAXException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			XPathFactory factory = XPathFactory.newInstance();
			
			XPath xpath = initializeStandardXPath(factory);
			
			XPathExpression expr = null;
			try {
				String linkDef = "//xs:complexType" + "/xs:attribute[@name='href']/xs:annotation/xs:appinfo/space-appinfo/link";
				expr = xpath.compile(linkDef);
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Object result = null;
			try {
				result = expr.evaluate(doc,
						XPathConstants.NODESET);
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}
			
			if (result == null) {
				return false;
			}
			
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
				return false;
			}
			
			if (elements == null || elements.size() == 0) {
				return false;
			}
			
			for(Node linkNode : elements) {
				String linkValue = linkNode.getTextContent();
				Node attributeNode = linkNode.getParentNode().getParentNode().getParentNode().getParentNode();
				
				//removing "type" attribute
				if (attributeNode.getAttributes().getNamedItem("type") != null) {
					attributeNode.getAttributes().removeNamedItem("type");
				}
				//removing annotation element
				
				//adding xs:simpleType sublement
				Node annotationNode = linkNode.getParentNode().getParentNode().getParentNode();
				attributeNode.removeChild(annotationNode);
				addStringSingleValueEnumToNode(doc, attributeNode, linkValue);
			}
			
			//patched, serializing
			Transformer transformer1 = TransformerFactory.newInstance().newTransformer();
			transformer1.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer1.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer1.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer1.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StreamResult result1 = new StreamResult(new FileOutputStream(targetFile));
			DOMSource source1 = new DOMSource(doc);
			transformer1.transform(source1, result1);
			return true;
		} catch (Throwable th) {
			th.printStackTrace();
		}
		
		return false;
	}
	
	private void addStringSingleValueEnumToNode(org.w3c.dom.Document doc, Node attributeNode,
			String linkValue) {
		Node simpleTypeNode = getOrCreateChildElementNode(doc, attributeNode, "xs:simpleType", null, null);
		Node restrictionNode = getOrCreateChildElementNode(doc, simpleTypeNode, "xs:restriction", "base", "xs:string");
		Node enumerationNode = getOrCreateChildElementNode(doc, restrictionNode, "xs:enumeration", "value", linkValue);
	}
	
	/**
	 * Adds (or gets if already exist) node to the specified parent. If attribute name is not null, also adds attribute to the child
	 * if child does not exist.
	 * @param doc
	 * @param parent
	 * @param nodeName
	 * @param attribute
	 * @param value
	 * @return
	 */
	private static final Node getOrCreateChildElementNode(org.w3c.dom.Document doc, Node parent, String nodeName,
			String attribute, String value) {
		NodeList childNodes = parent.getChildNodes();
		Node result = null;
		
		//trying to find the node first
		for( int i = 0; i < childNodes.getLength(); i++){
			Node currentNode = childNodes.item(i);
			if( currentNode.getNodeType() == Node.ELEMENT_NODE && currentNode.getNodeName().equals(nodeName)){
				result = currentNode;
				break;
			}
		}
		
		
		if (result == null) {
			//not found, creating it
			result = doc.createElement(nodeName);
			if (attribute != null) {
				Attr attributeNode = doc.createAttribute(attribute);
				attributeNode.setValue(value);
				result.getAttributes().setNamedItem(attributeNode);
			}
			
			parent.appendChild(result);
		}
		
		return result;
	}

	protected static XPath initializeStandardXPath(XPathFactory factory) {
		XPath xpath = factory.newXPath();
		xpath.setNamespaceContext(new NamespaceContext() {
			
			public Iterator getPrefixes(String namespaceURI) {
				return null;
			}

			
			public String getPrefix(String namespaceURI) {
				return "xs";
			}

			
			public String getNamespaceURI(String prefix) {
				return "http://www.w3.org/2001/XMLSchema";
			}
		});
		return xpath;
	}
}
