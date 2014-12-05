package com.mulesoft.jaxrs.raml.jaxb;

import javax.xml.bind.annotation.XmlAttribute;

import com.mulesoft.jaxrs.raml.annotation.model.IBasicModel;

public class JAXBAttributeProperty extends JAXBProperty{

	public JAXBAttributeProperty(IBasicModel model,JAXBRegistry r, String name) {
		super(model,r, name);		
	}

	public Class<?> asJavaType() {
		return int.class;
	}

	@Override
	protected String getPropertyAnnotation() {
		return XmlAttribute.class.getSimpleName();
	}

}
