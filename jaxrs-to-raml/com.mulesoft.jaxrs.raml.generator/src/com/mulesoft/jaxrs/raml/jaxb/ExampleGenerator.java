package com.mulesoft.jaxrs.raml.jaxb;

public class ExampleGenerator {

	public ExampleGenerator(IExampleWriter writer) {
		super();
		this.writer = writer;
	}

	protected IExampleWriter writer;
	
	public void generateXML(JAXBType type){
		String xmlName = type.getXMLName();
		writer.startEntity(xmlName);
		for (JAXBProperty p:type.properties){
			writeProperty(p);
		}
		writer.endEntity(xmlName);
	}

	private void writeProperty(JAXBProperty p) {
		String name=p.name();
		if (p instanceof JAXBAttributeProperty){
			JAXBAttributeProperty ap=(JAXBAttributeProperty) p;
			writer.generateAttribute(name, ap.asJavaType());
		}
		if (p instanceof JAXBElementProperty){
			JAXBElementProperty el=(JAXBElementProperty) p;
			JAXBType jaxbType = el.getJAXBType();
			if (jaxbType!=null){
				generateXML(jaxbType);
			}
			else{
				writer.generateElement(name, el.asJavaType());
			}
			//writer.generateAttribute(p.getElementName(), type);
		}
		if (p instanceof JAXBValueProperty){
			writer.addValueSample(p.asJavaType());
			//writer.generateAttribute(p.getElementName(), type);
		}
	}
}
