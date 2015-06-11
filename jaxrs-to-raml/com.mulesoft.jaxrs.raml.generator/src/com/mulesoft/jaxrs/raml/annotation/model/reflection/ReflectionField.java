package com.mulesoft.jaxrs.raml.annotation.model.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

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
		
		Class<?> type = null;
		if(Utils.isCollection(element.getType())){
			Type gType = element.getGenericType();
			if(gType instanceof ParameterizedType){
				Type[] args = ((ParameterizedType)gType).getActualTypeArguments();
				if(args!=null&&args.length!=0){
					if(args[0] instanceof Class){
						type = (Class<?>) args[0];
					}
					else if(args[0] instanceof ParameterizedType){
						Type rawType = ((ParameterizedType)args[0]).getRawType();
						if(rawType instanceof Class){
							type = (Class<?>) rawType;
						}
					}
					
				}
			}
		}
		else{
			type = element.getType();
		}
		if(type==null){
			return null;
		}
		ITypeModel model = new ReflectionType(type);
		return model;
	}

	/** {@inheritDoc} */
	@Override
	public Class<?> getJavaType() {
		return element.getType();
	}

	public boolean isGeneric() {
		Type gType = element.getGenericType();
		String typeName = this.element.getType().getName();
		String gTypeName = gType.toString();
		if(gTypeName.startsWith("class ")){
			gTypeName = gTypeName.substring("class ".length());
		}
		
		if(!gTypeName.startsWith(typeName)){
			return true;
		}		
		if(gType instanceof ParameterizedType){
			Type[] args = ((ParameterizedType)gType).getActualTypeArguments();
			if(args!=null&&args.length!=0){
				for(Type arg : args){
					if(arg instanceof TypeVariable){
						return true;
					}
				}
			}
		}
		return false;
	}
}
