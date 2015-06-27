package com.mulesoft.jaxrs.raml.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.mulesoft.jaxrs.raml.annotation.model.IMember;
import com.mulesoft.jaxrs.raml.annotation.model.reflection.ReflectionType;

/**
 * <p>JAXBElementProperty class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class JAXBElementProperty extends JAXBProperty{

	/**
	 * <p>Constructor for JAXBElementProperty.</p>
	 *
	 * @param model a {@link com.mulesoft.jaxrs.raml.annotation.model.IBasicModel} object.
	 * @param r a {@link com.mulesoft.jaxrs.raml.jaxb.JAXBRegistry} object.
	 * @param name a {@link java.lang.String} object.
	 */
	public JAXBElementProperty(IMember model,JAXBRegistry r, String name) {
		super(model,r,name);			
	}

	/**
	 * <p>asJavaType.</p>
	 *
	 * @return a {@link java.lang.Class} object.
	 */
	public Class<?> asJavaType() {
		if (originalType instanceof IMember){
			IMember or=(IMember) originalType;
			return or.getJavaType();
		}
		return null;
	}

	/**
	 * <p>getJAXBType.</p>
	 *
	 * @return a {@link com.mulesoft.jaxrs.raml.jaxb.JAXBType} object.
	 */
	public List<JAXBType> getJAXBTypes() {
		if(this.originalType.hasAnnotation(XmlJavaTypeAdapter.class.getSimpleName())){
			ArrayList<JAXBType> list = new ArrayList<JAXBType>();
			list.add(registry.getJAXBModel(new ReflectionType(Object.class)));
			return list;
		}
		return registry.getJAXBModels(((IMember)originalType).getJAXBTypes());		
	}

	/** {@inheritDoc} */
	@Override
	protected String getPropertyAnnotation() {
		return XmlElement.class.getSimpleName();
	}

}
