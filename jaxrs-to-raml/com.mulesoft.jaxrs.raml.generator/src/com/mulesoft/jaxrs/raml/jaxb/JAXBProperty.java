package com.mulesoft.jaxrs.raml.jaxb;

import java.util.Collection;

import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;
import com.mulesoft.jaxrs.raml.annotation.model.IBasicModel;
import com.mulesoft.jaxrs.raml.annotation.model.IMember;

public abstract class JAXBProperty extends JAXBModelElement{

	String propertyName;
	boolean required;
	private boolean isCollection;
	
	public JAXBProperty(IBasicModel model,JAXBRegistry r, String name) {
		super(model,r);
		this.propertyName=name;
		IAnnotationModel annotation = model.getAnnotation(getPropertyAnnotation());
		if (annotation!=null){
			String value = annotation.getValue("required");
			if (value!=null&&value.equals("true")){
				this.required=true;
			}
		}
	}
	
	protected abstract String getPropertyAnnotation();

	JAXBType getType(){
		return null;		
	}

	public String name() {
		return elementName!=null?elementName:propertyName.toLowerCase();
	}

	public Class<?> asJavaType() {
		if (originalType instanceof IMember){
			IMember or=(IMember) originalType;
			return or.getJavaType();
		}
		return null;
	}

	public boolean isCollection() {
		boolean b = isCollection||asJavaType()!=null&&Collection.class.isAssignableFrom(asJavaType());
		return b;
	}
}
