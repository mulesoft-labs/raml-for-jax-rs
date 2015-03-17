package com.mulesoft.jaxrs.raml.jaxb;

import javax.xml.bind.annotation.XmlValue;

import com.mulesoft.jaxrs.raml.annotation.model.IBasicModel;

/**
 * <p>JAXBValueProperty class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class JAXBValueProperty extends JAXBProperty{

	/**
	 * <p>Constructor for JAXBValueProperty.</p>
	 *
	 * @param model a {@link com.mulesoft.jaxrs.raml.annotation.model.IBasicModel} object.
	 * @param r a {@link com.mulesoft.jaxrs.raml.jaxb.JAXBRegistry} object.
	 * @param name a {@link java.lang.String} object.
	 */
	public JAXBValueProperty(IBasicModel model,JAXBRegistry r,String name) {
		super(model,r,name);
	}

	/** {@inheritDoc} */
	@Override
	protected String getPropertyAnnotation() {
		return XmlValue.class.getSimpleName();
	}

}
