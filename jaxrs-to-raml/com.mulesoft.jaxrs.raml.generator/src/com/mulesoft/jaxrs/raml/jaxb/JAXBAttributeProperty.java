package com.mulesoft.jaxrs.raml.jaxb;

import javax.xml.bind.annotation.XmlAttribute;

import com.mulesoft.jaxrs.raml.annotation.model.IBasicModel;
import com.mulesoft.jaxrs.raml.annotation.model.IMember;

/**
 * <p>JAXBAttributeProperty class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class JAXBAttributeProperty extends JAXBProperty{

	/**
	 * <p>Constructor for JAXBAttributeProperty.</p>
	 *
	 * @param model a {@link com.mulesoft.jaxrs.raml.annotation.model.IBasicModel} object.
	 * @param r a {@link com.mulesoft.jaxrs.raml.jaxb.JAXBRegistry} object.
	 * @param name a {@link java.lang.String} object.
	 */
	public JAXBAttributeProperty(IBasicModel model,JAXBRegistry r, String name) {
		super(model,r, name);		
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

	/** {@inheritDoc} */
	@Override
	protected String getPropertyAnnotation() {
		return XmlAttribute.class.getSimpleName();
	}

}
