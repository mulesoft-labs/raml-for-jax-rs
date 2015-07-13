package com.mulesoft.jaxrs.raml.jaxb;

import javax.xml.bind.annotation.XmlValue;

import com.mulesoft.jaxrs.raml.annotation.model.IMember;
import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

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
	public JAXBValueProperty(IMember model, IMethodModel setter, ITypeModel ownerType, JAXBRegistry r,String name) {
		super(model,setter,ownerType,r,name);
	}

	/** {@inheritDoc} */
	@Override
	protected String getPropertyAnnotation() {
		return XmlValue.class.getSimpleName();
	}

}
