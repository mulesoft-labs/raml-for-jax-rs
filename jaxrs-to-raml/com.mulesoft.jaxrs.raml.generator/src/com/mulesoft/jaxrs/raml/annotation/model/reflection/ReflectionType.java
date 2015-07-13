package com.mulesoft.jaxrs.raml.annotation.model.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import com.mulesoft.jaxrs.raml.annotation.model.IFieldModel;
import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeParameter;

/**
 * <p>ReflectionType class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class ReflectionType extends ReflectionGenericElement<Class<?>> implements ITypeModel{

	/**
	 * <p>Constructor for ReflectionType.</p>
	 *
	 * @param element a {@link java.lang.Class} object.
	 */
	public ReflectionType(Class<?> element) {
		super(element);
	
	}

	
	/**
	 * <p>getMethods.</p>
	 *
	 * @return an array of {@link com.mulesoft.jaxrs.raml.annotation.model.IMethodModel} objects.
	 */
	public IMethodModel[] getMethods() {
		Method[] declaredMethods = element.getDeclaredMethods();
		IMethodModel[] methods=new IMethodModel[declaredMethods.length];
		int a=0;
		for (Method m:declaredMethods){
			methods[a++]=new ReflectionMethod(m);
		}
		return methods;
	}

	
	/**
	 * <p>getName.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getName() {
		return element.getSimpleName();
	}

	
	/**
	 * <p>getFullyQualifiedName.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getFullyQualifiedName() {
		return element.getCanonicalName();
	}


	/** {@inheritDoc} */
	@Override
	public IFieldModel[] getFields() {
		Field[] declaredFields= element.getDeclaredFields();
		IFieldModel[] fields=new IFieldModel[declaredFields.length];
		int a=0;
		for (Field m:declaredFields){
			fields[a++]=new ReflectionField(m);
		}
		return fields;
	}


	@Override
	public ITypeModel getSuperClass() {
		Class<?> superClass = this.element.getSuperclass();		
		return superClass!=null ? new ReflectionType(superClass) : null;
	}


	@Override
	public ITypeModel[] getImplementedInterfaces() {
		Class<?>[] interfaces = this.element.getInterfaces();
		if(interfaces==null||interfaces.length==0){
			return new ITypeModel[0];
		}
		ITypeModel[] arr = new ITypeModel[interfaces.length];
		for(int i = 0 ; i < interfaces.length ; i++){
			arr[i] = new ReflectionType(interfaces[i]);
		}
		return arr;
	}


	@Override
	public ITypeModel resolveClass(String qualifiedName) {
		try {
			Class<?> clazz = this.element.getClassLoader().loadClass(qualifiedName);
			if(clazz==null){
				return null;
			}
			return new ReflectionType(clazz);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

}
