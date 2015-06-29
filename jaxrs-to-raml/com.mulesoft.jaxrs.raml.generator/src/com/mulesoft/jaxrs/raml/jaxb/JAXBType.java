package com.mulesoft.jaxrs.raml.jaxb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.mulesoft.jaxrs.raml.annotation.model.IFieldModel;
import com.mulesoft.jaxrs.raml.annotation.model.IMember;
import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

/**
 * <p>JAXBType class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class JAXBType extends JAXBModelElement {

	/**
	 * <p>Constructor for JAXBType.</p>
	 *
	 * @param model a {@link com.mulesoft.jaxrs.raml.annotation.model.ITypeModel} object.
	 * @param r a {@link com.mulesoft.jaxrs.raml.jaxb.JAXBRegistry} object.
	 */
	public JAXBType(ITypeModel model,JAXBRegistry r) {
		super(model,r);
		IMethodModel[] methods = model.getMethods();
		String value = value(XmlAccessorType.class, "value");
		XmlAccessType type = XmlAccessType.PUBLIC_MEMBER;
		this.className = model.getFullyQualifiedName();
		if (value != null) {
			type = extractType(value);
		}
		if(this.className.equals("java.lang.Object")){
			return;
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
				String methodName = get ? m.getName().substring(3)
						: m.getName().substring(2);
				if(!methodName.isEmpty()){
					properties.add(createProperty(methodName, m));
				}
			}
		}
		for (IFieldModel f : model.getFields()) {
			if (!f.isStatic()) {
				boolean needToConsume = needToConsume(type, f);
				if (!needToConsume) {
					continue;
				}
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
			if (type == XmlAccessType.PROPERTY && m instanceof IMethodModel) {
				needToConsume = true;
			}
			if (type == XmlAccessType.FIELD && m instanceof IFieldModel) {
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

	private XmlAccessType extractType(String value) {
		try{
		int ind=value.lastIndexOf('.');
		if(ind!=-1){
			return XmlAccessType.valueOf(value.substring(ind+1));
		}
		
		return XmlAccessType.valueOf(value);
		}catch(Exception e){
			e.printStackTrace();
			return XmlAccessType.NONE;
		}
	}

	private JAXBProperty createProperty(String string, IMember m) {
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
	
	protected String className;

	public String getClassName() {
		return className;
	}

	protected ArrayList<JAXBProperty> properties = new ArrayList<JAXBProperty>();

	/**
	 * <p>getXMLName.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getXMLName() {
		return elementName!=null?elementName:originalType.getName().toLowerCase();
	}

	/**
	 * <p>gatherNamespaces.</p>
	 *
	 * @return a {@link java.util.HashMap} object.
	 */
	public HashMap<String, String> gatherNamespaces() {
		int n=0;
		HashMap<String, String>map=new HashMap<String, String>();
		fillNamespaceMap(map,n,null);
		return map;
	}

	private int fillNamespaceMap(HashMap<String, String> map, int n,Set<String> processed) {
		if(processed==null){
			processed = new HashSet<String>();
		}
		for (JAXBProperty p:properties){
			if (p.namespace!=null){
				map.put(p.namespace, "n"+(n++));
			}
			JAXBType type = p.getType();
//			if(type==null){
//				continue;
//			}
			String qName = type.getClassName();
			if(!processed.contains(qName)){
				processed.add(qName);			
				if (type!=null){
					n=type.fillNamespaceMap(map, n, processed);
				}
				processed.remove(qName);
			}
		}
		return n;
	}

}
