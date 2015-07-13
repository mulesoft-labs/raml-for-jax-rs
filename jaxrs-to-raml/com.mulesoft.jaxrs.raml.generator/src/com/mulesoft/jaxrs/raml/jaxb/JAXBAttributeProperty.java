package com.mulesoft.jaxrs.raml.jaxb;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;

import com.mulesoft.jaxrs.raml.annotation.model.IMember;
import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

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
	public JAXBAttributeProperty(IMember model, IMethodModel setter, ITypeModel ownerType, JAXBRegistry r, String name) {
		super(model,setter, ownerType, r, name);
		this.isAnyAttribute = model.hasAnnotation(XmlAnyAttribute.class.getSimpleName());
	}
	/**
	 * indicates if corresponding field or method is annotated with '@XmlAnyAttribute'
	 */
	private boolean isAnyAttribute;

	/**
	 * <p>asJavaType.</p>
	 *
	 * @return a {@link java.lang.Class} object.
	 */
	public Class<?> asJavaType() {
		if (originalModel instanceof IMember){
			IMember or=(IMember) originalModel;
			return or.getJavaType();
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	protected String getPropertyAnnotation() {
		return XmlAttribute.class.getSimpleName();
	}

	public boolean isAnyAttribute() {
		return isAnyAttribute;
	}

}
