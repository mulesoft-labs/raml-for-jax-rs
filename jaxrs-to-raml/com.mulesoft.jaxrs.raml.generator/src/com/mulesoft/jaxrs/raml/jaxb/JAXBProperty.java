package com.mulesoft.jaxrs.raml.jaxb;

import java.util.Collection;

import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;
import com.mulesoft.jaxrs.raml.annotation.model.IBasicModel;
import com.mulesoft.jaxrs.raml.annotation.model.IFieldModel;
import com.mulesoft.jaxrs.raml.annotation.model.IMember;
import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;

/**
 * <p>Abstract JAXBProperty class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public abstract class JAXBProperty extends JAXBModelElement{

	String propertyName;
	boolean required;
	private boolean isCollection;
	
	/**
	 * <p>Constructor for JAXBProperty.</p>
	 *
	 * @param model a {@link com.mulesoft.jaxrs.raml.annotation.model.IBasicModel} object.
	 * @param r a {@link com.mulesoft.jaxrs.raml.jaxb.JAXBRegistry} object.
	 * @param name a {@link java.lang.String} object.
	 */
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
	
	/**
	 * <p>getPropertyAnnotation.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	protected abstract String getPropertyAnnotation();

	JAXBType getType(){
		return null;		
	}

	/**
	 * <p>name.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String name() {
		return elementName!=null?elementName:propertyName;
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
	 * <p>isCollection.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isCollection() {
		boolean b = isCollection||asJavaType()!=null&&Collection.class.isAssignableFrom(asJavaType());
		return b;
	}
	
	public boolean isGeneric(){
		if(this.originalType instanceof IFieldModel){
			return ((IFieldModel)this.originalType).isGeneric();
		}
		else if(this.originalType instanceof IMethodModel){
			return ((IMethodModel)this.originalType).hasGenericReturnType();
		}
		else{
			return false;
		}
	}
}
