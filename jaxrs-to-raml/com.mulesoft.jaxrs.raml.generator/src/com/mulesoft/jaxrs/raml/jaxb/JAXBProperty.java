package com.mulesoft.jaxrs.raml.jaxb;

import java.util.Collection;
import java.util.Map;

import javax.xml.bind.annotation.XmlAnyAttribute;

import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;
import com.mulesoft.jaxrs.raml.annotation.model.IFieldModel;
import com.mulesoft.jaxrs.raml.annotation.model.IMember;
import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;
import com.mulesoft.jaxrs.raml.annotation.model.StructureType;

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
	private boolean isMap;
	
	/**
	 * <p>Constructor for JAXBProperty.</p>
	 *
	 * @param model a {@link com.mulesoft.jaxrs.raml.annotation.model.IBasicModel} object.
	 * @param r a {@link com.mulesoft.jaxrs.raml.jaxb.JAXBRegistry} object.
	 * @param name a {@link java.lang.String} object.
	 */
	public JAXBProperty(IMember model,JAXBRegistry r, String name) {
		super(model,r);
		this.propertyName=name;
		this.isCollection = model.isCollection();
		this.isMap = model.isMap();
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
		if(this.originalType instanceof IFieldModel){
			ITypeModel type = ((IFieldModel)this.originalType).getType();
			return registry.getJAXBModel(type);
		}
		else if(this.originalType instanceof IMethodModel){
			ITypeModel type = ((IMethodModel)this.originalType).getReturnedType();
			return registry.getJAXBModel(type);
		}
		else{
			return null;
		}
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

	public StructureType getStructureType() {
		if(isCollection||asJavaType()!=null&&Collection.class.isAssignableFrom(asJavaType())){
			return StructureType.COLLECTION;
		}
		else if(isMap
				||(asJavaType()!=null&&Map.class.isAssignableFrom(asJavaType())
				||this.originalType.hasAnnotation(XmlAnyAttribute.class.getSimpleName()))
			){
			return StructureType.MAP;
		}
		else{
			return StructureType.COMMON;
		}
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
