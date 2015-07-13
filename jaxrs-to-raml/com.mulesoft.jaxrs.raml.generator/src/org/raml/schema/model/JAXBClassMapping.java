package org.raml.schema.model;

import java.util.HashMap;
import java.util.Map;

public class JAXBClassMapping {
	
	private static Map<String,String> map = new HashMap<String,String>();
	static{	
		map.put("java.math.BigIntegerxs", "xs:integer");
		map.put("java.math.BigDecimalxs", "xs:decimal");
		map.put("java.util.Calendarxs", "xs:dateTime");
		map.put("java.util.Datexs", "xs:dateTime");
		map.put("javax.xml.namespace.QNamexs", "xs:QName");
		map.put("java.net.URIxs", "xs:string");
		map.put("javax.xml.datatype.XMLGregorianCalendarxs", "xs:anySimpleType");
		map.put("javax.xml.datatype.Durationxs", "xs:duration");
		map.put("java.awt.Imagexs", "xs:base64Binary");
		map.put("javax.activation.DataHandlerxs", "xs:base64Binary");
		map.put("javax.xml.transform.Sourcexs", "xs:base64Binary");
		map.put("java.util.UUIDxs", "xs:string");
	}

}
