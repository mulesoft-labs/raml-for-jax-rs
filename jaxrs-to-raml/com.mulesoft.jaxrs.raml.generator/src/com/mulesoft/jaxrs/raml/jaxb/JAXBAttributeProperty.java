package com.mulesoft.jaxrs.raml.jaxb;

import javax.xml.bind.annotation.XmlAttribute;

import com.mulesoft.jaxrs.raml.annotation.model.IBasicModel;
import com.mulesoft.jaxrs.raml.annotation.model.IMember;

public class JAXBAttributeProperty extends JAXBProperty{

	public JAXBAttributeProperty(IBasicModel model,JAXBRegistry r, String name) {
		super(model,r, name);		
	}

	public Class<?> asJavaType() {
		if (originalType instanceof IMember){
			IMember or=(IMember) originalType;
			return or.getJavaType();
		}
		return null;
	}

	@Override
	protected String getPropertyAnnotation() {
		return XmlAttribute.class.getSimpleName();
	}

}
