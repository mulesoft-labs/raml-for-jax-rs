package com.mulesoft.jaxrs.raml.jaxb;

import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.mulesoft.jaxrs.raml.annotation.model.IMember;
import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;
import com.mulesoft.jaxrs.raml.annotation.model.StructureType;
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
	public JAXBElementProperty(IMember model,IMethodModel setter,ITypeModel ownerType,JAXBRegistry r, String name) {
		super(model,setter,ownerType,r,name);			
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

	/**
	 * <p>getJAXBType.</p>
	 *
	 * @return a {@link com.mulesoft.jaxrs.raml.jaxb.JAXBType} object.
	 */
	public List<JAXBType> getJAXBTypes() {
		if(this.originalModel.hasAnnotation(XmlJavaTypeAdapter.class.getSimpleName())){
			ArrayList<JAXBType> list = new ArrayList<JAXBType>();
			if(this.getStructureType()==StructureType.MAP){
				list.add(registry.getJAXBModel(new ReflectionType(String.class)));
				list.add(registry.getJAXBModel(new ReflectionType(Object.class)));
			}
			else{
				String adapter = originalModel.getAnnotationValue(XmlJavaTypeAdapter.class.getSimpleName());
				ITypeModel adapterClass = this.ownerType.resolveClass(adapter);
				if(adapterClass==null){
					list.add(registry.getJAXBModel(new ReflectionType(Object.class)));
				}
				else{
					IMethodModel[] methods = adapterClass.getMethods();
					for(IMethodModel m : methods){
						if(m.getName().equals("marshal")){
							ITypeModel returnedType = m.getReturnedType();
							if(returnedType!=null){
								list.add(registry.getJAXBModel(returnedType));
								break;
							}
						}
					}
				}
			}
			return list;
		}
//		else if(this.originalType.hasAnnotation(XmlAnyAttribute.class.getSimpleName())){
//			ArrayList<JAXBType> list = new ArrayList<JAXBType>();
//			list.add(registry.getJAXBModel(new ReflectionType(String.class)));
//			list.add(registry.getJAXBModel(new ReflectionType(String.class)));
//			return list;
//		}
//		else if(this.originalType.hasAnnotation(XmlAnyElement.class.getSimpleName())){
//			ArrayList<JAXBType> list = new ArrayList<JAXBType>();
//			list.add(registry.getJAXBModel(new ReflectionType(Object.class)));
//			return list;
//		}
		return registry.getJAXBModels(((IMember)originalModel).getJAXBTypes());		
	}

	/** {@inheritDoc} */
	@Override
	protected String getPropertyAnnotation() {
		return XmlElement.class.getSimpleName();
	}

}
