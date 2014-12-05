package com.mulesoft.jaxrs.raml.jaxb;

import javax.xml.bind.annotation.XmlElement;

import com.mulesoft.jaxrs.raml.annotation.model.IBasicModel;
import com.mulesoft.jaxrs.raml.annotation.model.IMember;

public class JAXBElementProperty extends JAXBProperty{

	public JAXBElementProperty(IBasicModel model,JAXBRegistry r, String name) {
		super(model,r,name);			
	}

	public Class<?> asJavaType() {
		return null;
	}

	public JAXBType getJAXBType() {
		return registry.getJAXBModel(((IMember)originalType).getJAXBType());		
	}

	@Override
	protected String getPropertyAnnotation() {
		return XmlElement.class.getSimpleName();
	}

}
