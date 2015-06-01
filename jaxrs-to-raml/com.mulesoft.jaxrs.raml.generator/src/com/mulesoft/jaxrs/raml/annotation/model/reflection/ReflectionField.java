package com.mulesoft.jaxrs.raml.annotation.model.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.mulesoft.jaxrs.raml.annotation.model.IFieldModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;
import com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor;

/**
 * <p>ReflectionField class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class ReflectionField extends BasicReflectionMember<Field> implements
		IFieldModel {

	/**
	 * <p>Constructor for ReflectionField.</p>
	 *
	 * @param element a {@link java.lang.reflect.Field} object.
	 */
	public ReflectionField(Field element) {
		super(element);
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return element.getName();
	}

	/** {@inheritDoc} */
	@Override
	public ITypeModel getType() {
		Class<?> returnType = element.getType();
		return new ReflectionType(returnType);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isStatic() {
		return Modifier.isStatic(element.getModifiers());
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPublic() {
		return Modifier.isPublic(element.getModifiers());		
	}

	/** {@inheritDoc} */
	@Override
	public ITypeModel getJAXBType() {
		ITypeModel type = getType();
		if(ResourceVisitor.isJAXBType(type)){
			return type;
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public Class<?> getJavaType() {
		return element.getType();
	}
}
