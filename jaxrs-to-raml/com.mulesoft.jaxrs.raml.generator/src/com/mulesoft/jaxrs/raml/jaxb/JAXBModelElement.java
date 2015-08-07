package com.mulesoft.jaxrs.raml.jaxb;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;
import com.mulesoft.jaxrs.raml.annotation.model.IBasicModel;
import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

/**
 * <p>JAXBModelElement class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class JAXBModelElement {

	protected static final String NAMESPACE = "namespace";
	protected static final String NAME = "name";
	protected IBasicModel originalModel;
	protected ITypeModel ownerType;
	protected String namespace;
	protected String typeName;
	protected String elementName;
	final JAXBRegistry registry;
	protected ArrayList<IAnnotationModel> annotations;

	/**
	 * <p>Constructor for JAXBModelElement.</p>
	 *
	 * @param model a {@link com.mulesoft.jaxrs.raml.annotation.model.IBasicModel} object.
	 * @param registry a {@link com.mulesoft.jaxrs.raml.jaxb.JAXBRegistry} object.
	 */
	public JAXBModelElement(IBasicModel model,ITypeModel ownerType,JAXBRegistry registry) {
		super();
		this.registry=registry;
		if (model==null){
			throw new IllegalArgumentException();
		}
		this.originalModel=model;
		this.ownerType = ownerType;		
		elementName=value(XmlElement.class, NAME);
		namespace=value(XmlElement.class, NAMESPACE);
		typeName=value(XmlType.class,NAME);
		IAnnotationModel[] modelAnnotations = model.getAnnotations();
		this.annotations = modelAnnotations != null ? new ArrayList<IAnnotationModel>(Arrays.asList(modelAnnotations)) : new ArrayList<IAnnotationModel>();
	}

	/**
	 * <p>value.</p>
	 *
	 * @param cl a {@link java.lang.Class} object.
	 * @param name a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public String value(Class<? extends Annotation>cl,String name){
		IAnnotationModel annotation = originalModel.getAnnotation(cl.getSimpleName());
		if( annotation!=null){
			return annotation.getValue(name);
		}
		return null;
	}

	public List<IAnnotationModel> getAnnotations() {
		return annotations;
	}
	
}
