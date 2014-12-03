package com.mulesoft.jaxrs.raml.jaxb;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.mulesoft.jaxrs.raml.annotation.model.IBasicModel;
import com.mulesoft.jaxrs.raml.annotation.model.IFieldModel;
import com.mulesoft.jaxrs.raml.annotation.model.IMember;
import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

public class JAXBType extends JAXBModelElement {

	public JAXBType(ITypeModel model,JAXBRegistry r) {
		super(model,r);
		IMethodModel[] methods = model.getMethods();
		String value = value(XmlAccessorType.class, "value");
		XmlAccessType type = XmlAccessType.PUBLIC_MEMBER;
		if (value != null) {
			type = extractType(type);
		}
		for (IMethodModel m : methods) {
			boolean needToConsume = needToConsume(type, m);
			if (!needToConsume) {
				continue;
			}
			boolean get = m.getName().startsWith("get");
			boolean is = m.getName().startsWith("is");
			if (get || is) {
				// it is potential property method
				properties.add(createProperty(get ? m.getName().substring(3)
						: m.getName().substring(2), m));
			}
		}
		for (IFieldModel f : model.getFields()) {
			if (!f.isStatic()) {
				properties.add(createProperty(f.getName(), f));
			}
		}
	}

	private boolean needToConsume(XmlAccessType type, IMember m) {
		boolean needToConsume = false;
		if (!m.isStatic()) {

			if (type == XmlAccessType.PUBLIC_MEMBER && m.isPublic()) {
				needToConsume = true;
			}
			if (type == XmlAccessType.PROPERTY==m instanceof IMethodModel) {
				needToConsume = true;
			}
			if (type == XmlAccessType.FIELD==m instanceof IFieldModel) {
				needToConsume = true;
			}			
			if (m.hasAnnotation(XmlTransient.class.getSimpleName())) {
				needToConsume = false;
				return false;
			}
			boolean isElement = m.hasAnnotation(XmlElement.class.getSimpleName());
			boolean isAttribute = m.hasAnnotation(XmlAttribute.class
					.getSimpleName());
			boolean isValue = m
					.hasAnnotation(javax.xml.bind.annotation.XmlValue.class
							.getSimpleName());
			if(isElement||isAttribute||isValue){
				needToConsume=true;
			}
		}
		return needToConsume;
	}

	private XmlAccessType extractType(XmlAccessType type) {
		return null;
	}

	private JAXBProperty createProperty(String string, IBasicModel m) {
		boolean isElement = m.hasAnnotation(XmlElement.class.getSimpleName());
		boolean isAttribute = m.hasAnnotation(XmlAttribute.class
				.getSimpleName());
		boolean isValue = m
				.hasAnnotation(javax.xml.bind.annotation.XmlValue.class
						.getSimpleName());
		if (isElement) {
			return new JAXBElementProperty(m,registry, string);
		}
		if (isAttribute) {
			return new JAXBAttributeProperty(m, registry,string);
		}
		if (isValue) {
			return new JAXBAttributeProperty(m, registry,string);
		}
		return new JAXBElementProperty(m,registry, string);
	}

	protected JAXBType superClass;

	protected ArrayList<JAXBProperty> properties = new ArrayList<JAXBProperty>();

	public String getXMLName() {
		return elementName!=null?elementName:originalType.getName().toLowerCase();
	}

}
