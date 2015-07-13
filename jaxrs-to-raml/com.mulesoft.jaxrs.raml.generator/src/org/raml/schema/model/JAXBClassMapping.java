package org.raml.schema.model;

import java.util.HashMap;
import java.util.Map;

public enum JAXBClassMapping {
	
	BIG_INTEGER(
			"java.math.BigInteger",
			"java.lang.Integer",
			"xs:integer",
			"123456789123456789"),
	BIG_DECIMAL(
			"java.math.BigDecimal",
			"java.lang.Double",
			"xs:decimal",
			"123456789123456789.123456789123456789"),
	CALENDAR(
			"java.util.Calendar",
			"java.lang.String",
			"xs:dateTime",
			"2002-09-24+06:00"),
	DATE(
			"java.util.Date",
			"java.lang.String",
			"xs:dateTime",
			"2002-09-24+06:00"),
	Q_NAME(
			"javax.xml.namespace.QName",
			"java.lang.String",
			"xs:QName",
			"data:someClassReferencedByQName"),
	URI(
			"java.net.URI",
			"java.lang.String",
			"xs:string",
			"http://www.raml.renerator.example.uri.com"),
	XML_GREGORIAN_CALENDAR(
			"javax.xml.datatype.XMLGregorianCalendar",
			"java.lang.String",
			"xs:anySimpleType",
			"1991-01-24'PSL'13:05:33"),
	DURATION(
			"javax.xml.datatype.Duration",
			"java.lang.String",
			"xs:duration",
			"P1Y2M3DT5H20M30.123S"),
	IMAGE(
			"java.awt.Image",
			"java.lang.String",
			"xs:base64Binary",
			"U29tZSBkYXRh"),
	DATA_HANDLER(
			"javax.activation.DataHandler",
			"java.lang.String",
			"xs:base64Binary",
			"U29tZSBkYXRh"),
	SOURCE(
			"javax.xml.transform.Source",
			"java.lang.String",
			"xs:base64Binary",
			"U29tZSBkYXRh"),
	UUID(
			"java.util.UUID",
			"java.lang.String",
			"xs:string",
			"d9515ea0-1638-4de9-8983-339f72e94fc6");

	
	private JAXBClassMapping(String originalClass, String mappingClass,
			String xsType, String example) {
		this.originalClass = originalClass;
		this.mappingClass = mappingClass;
		this.xsType = xsType;
		this.example = example;
	}
	
	private final String originalClass;
	
	private final String mappingClass;
	
	private final String xsType;
	
	private final String example;
	
	private static Map<String,String> jaxbMapping = new HashMap<String,String>();
	static{	
		jaxbMapping.put("java.math.BigInteger", "xs:integer");
		jaxbMapping.put("java.math.BigDecimal", "xs:decimal");
		jaxbMapping.put("java.util.Calendar", "xs:dateTime");
		jaxbMapping.put("java.util.Date", "xs:dateTime");
		jaxbMapping.put("javax.xml.namespace.QName", "xs:QName");
		jaxbMapping.put("java.net.URI", "xs:string");
		jaxbMapping.put("javax.xml.datatype.XMLGregorianCalendar", "xs:anySimpleType");
		jaxbMapping.put("javax.xml.datatype.Duration", "xs:duration");
		jaxbMapping.put("java.awt.Image", "xs:base64Binary");
		jaxbMapping.put("javax.activation.DataHandler", "xs:base64Binary");
		jaxbMapping.put("javax.xml.transform.Source", "xs:base64Binary");
		jaxbMapping.put("java.util.UUID", "xs:string");
	}
	
	private static Map<String,JAXBClassMapping> map;
	
	static void initMap(){
		if(map!=null){
			return;
		}
		map = new HashMap<String, JAXBClassMapping>();
		for(JAXBClassMapping mapping: JAXBClassMapping.values()){
			map.put(mapping.getOriginalClass(), mapping);			
		}
	}
	
	public static JAXBClassMapping getMapping(String str){
		initMap();
		return map.get(str);
	}

	public String getOriginalClass() {
		return originalClass;
	}

	public String getMappingClass() {
		return mappingClass;
	}

	public String getXsType() {
		return xsType;
	}

	public String getExample() {
		return example;
	}


}
