package com.mulesoft.jaxrs.raml.jaxb;

import java.lang.annotation.Annotation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;
import com.mulesoft.jaxrs.raml.annotation.model.IBasicModel;

public class JAXBModelElement {

	private static final String NAMESPACE = "namespace";
	private static final String NAME = "name";
	protected IBasicModel originalType;
	protected String namespace;
	protected String typeName;
	protected String elementName;
	final JAXBRegistry registry;

	public JAXBModelElement(IBasicModel model,JAXBRegistry registry) {
		super();
		this.registry=registry;
		if (model==null){
			throw new IllegalArgumentException();
		}
		this.originalType=model;
		elementName=value(XmlElement.class, NAME);
		namespace=value(XmlElement.class, NAMESPACE);
		typeName=value(XmlType.class,NAME);
	}

	public String value(Class<? extends Annotation>cl,String name){
		IAnnotationModel annotation = originalType.getAnnotation(cl.getSimpleName());
		if( annotation!=null){
			return annotation.getValue(name);
		}
		return null;
	}
	
}