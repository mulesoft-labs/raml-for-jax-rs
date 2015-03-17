package com.mulesoft.jaxrs.raml.jsonschema;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jettison.AbstractXMLStreamWriter;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;
import org.codehaus.jettison.mapped.SimpleConverter;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * <p>JsonUtil class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class JsonUtil {

	/**
	 * <p>convertToJSON.</p>
	 *
	 * @param xmlContent a {@link java.lang.String} object.
	 * @param format a boolean.
	 * @return a {@link java.lang.String} object.
	 */
	public static String convertToJSON(String xmlContent, boolean format) {
		Document document = null;
		try {
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			document = dBuilder.parse(new InputSource(new StringReader(xmlContent)));
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		if (document == null)
			return null;
		try {
			Element rootElement = document.getDocumentElement();
			String result = convertToJSON(rootElement);
			if(format) result = formatJSON(result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String convertToJSON(Element rootElement) throws Exception {
		Configuration config = new Configuration();
		config.setTypeConverter(new SimpleConverter());
		MappedNamespaceConvention con = new MappedNamespaceConvention(config);
		StringWriter strWriter = new StringWriter();
		AbstractXMLStreamWriter w = new MappedXMLStreamWriter(con, strWriter);
		w.writeStartDocument();
		converToJSON(rootElement, w);
		w.writeEndDocument();
		w.close();
		strWriter.close();
		String jsonString = strWriter.toString();
		StringWriter wr = new StringWriter();
		StringEscapeUtils.unescapeJavaScript(wr, jsonString);
		String result = wr.toString();
		return result;
	}

	private static void converToJSON(Element element, AbstractXMLStreamWriter w) throws Exception {
		String elementName = element.getNodeName();
		w.writeStartElement(elementName);
		NamedNodeMap attrs = element.getAttributes();
		int attrsCount = attrs.getLength();
		for (int i = 0; i < attrsCount; i++) {
			Node nd = attrs.item(i);
			if (!(nd instanceof Attr))
				continue;
			Attr attr = (Attr) nd;
			String attrName = attr.getName();
			String attrValue = attr.getValue();
			w.writeAttribute(attrName, attrValue);
		}
		boolean gotChildren = false;
		NodeList childrenList = element.getChildNodes();
		int subnodesCount = childrenList.getLength();
		for (int i = 0; i < subnodesCount; i++) {
			Node nd = childrenList.item(i);
			if (!(nd instanceof Element))
				continue;
			Element el = (Element) nd;
			converToJSON(el, w);
			gotChildren = true;
		}
		if (gotChildren) {
			w.writeEndElement();
			return;
		}
		String textContent = element.getTextContent();
		w.writeCharacters(textContent);
		w.writeEndElement();
	}

	private static String formatJSON(final String str) {
		return str;
	}

}
