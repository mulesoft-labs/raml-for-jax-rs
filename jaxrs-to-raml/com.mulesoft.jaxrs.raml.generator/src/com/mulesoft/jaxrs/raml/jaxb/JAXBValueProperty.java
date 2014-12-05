package com.mulesoft.jaxrs.raml.jaxb;

import javax.xml.bind.annotation.XmlValue;

import com.mulesoft.jaxrs.raml.annotation.model.IBasicModel;

public class JAXBValueProperty extends JAXBProperty{

	public JAXBValueProperty(IBasicModel model,JAXBRegistry r,String name) {
		super(model,r,name);
	}

	@Override
	protected String getPropertyAnnotation() {
		return XmlValue.class.getSimpleName();
	}

}
