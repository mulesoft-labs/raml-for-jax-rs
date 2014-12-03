package com.mulesoft.jaxrs.raml.jaxb;

import com.mulesoft.jaxrs.raml.annotation.model.IBasicModel;

public class JAXBProperty extends JAXBModelElement{

	String propertyName;
	
	public JAXBProperty(IBasicModel model,JAXBRegistry r, String name) {
		super(model,r);
		this.propertyName=name;
	}
	
	JAXBType getType(){
		return null;		
	}

	public String name() {
		return elementName!=null?elementName:propertyName.toLowerCase();
	}

	public Class<?> asJavaType() {
		return String.class;
	}
}
