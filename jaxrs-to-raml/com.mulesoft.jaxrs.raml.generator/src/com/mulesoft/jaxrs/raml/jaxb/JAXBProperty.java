package com.mulesoft.jaxrs.raml.jaxb;

import java.lang.annotation.Annotation;
import java.util.Arrays;
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
	protected IMethodModel setter;
	
	/**
	 * <p>Constructor for JAXBProperty.</p>
	 *
	 * @param model a {@link com.mulesoft.jaxrs.raml.annotation.model.IBasicModel} object.
	 * @param r a {@link com.mulesoft.jaxrs.raml.jaxb.JAXBRegistry} object.
	 * @param name a {@link java.lang.String} object.
	 */
	public JAXBProperty(IMember model, IMethodModel setter,ITypeModel ownerType,JAXBRegistry r, String name) {
		super(model,ownerType,r);
		this.propertyName=name;
		this.isCollection = model.isCollection();
		this.isMap = model.isMap();
		this.setter = setter;
		IAnnotationModel annotation = model.getAnnotation(getPropertyAnnotation());
		if (annotation!=null){
			String value = annotation.getValue("required");
			if (value!=null&&value.equals("true")){
				this.required=true;
			}
		}
		if(setter!=null){
			IAnnotationModel[] setterAnnotations = setter.getAnnotations();
			if(setterAnnotations!=null){
				this.annotations.addAll(Arrays.asList(setterAnnotations));
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
		if(this.originalModel instanceof IFieldModel){
			ITypeModel type = ((IFieldModel)this.originalModel).getType();
			return registry.getJAXBModel(type);
		}
		else if(this.originalModel instanceof IMethodModel){
			ITypeModel type = ((IMethodModel)this.originalModel).getReturnedType();
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
		if (originalModel instanceof IMember){
			IMember or=(IMember) originalModel;
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
				||this.originalModel.hasAnnotation(XmlAnyAttribute.class.getSimpleName()))
			){
			return StructureType.MAP;
		}
		else{
			return StructureType.COMMON;
		}
	}
	
	public boolean isGeneric(){
		if(this.originalModel instanceof IFieldModel){
			return ((IFieldModel)this.originalModel).isGeneric();
		}
		else if(this.originalModel instanceof IMethodModel){
			return ((IMethodModel)this.originalModel).hasGenericReturnType();
		}
		else{
			return false;
		}
	}
	
	@Override
	public String value(Class<? extends Annotation> cl, String name) {
		String superValue = super.value(cl, name);
		if(superValue!=null){
			return superValue;
		}
		if(this.setter!=null){
			IAnnotationModel annotation = originalModel.getAnnotation(cl.getSimpleName());
			if(annotation!=null){			
				annotation = setter.getAnnotation(cl.getSimpleName());
				if( annotation!=null){
					return annotation.getValue(name);
				}
			}
		}
		return null;
	}
}
