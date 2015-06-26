package com.mulesoft.jaxrs.raml.annotation.model.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.mulesoft.jaxrs.raml.annotation.model.IFieldModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

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
	public List<ITypeModel> getJAXBTypes() {		
		return Utils.getJAXBTypes(this.element);
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

	@Override
	public boolean isCollection() {
		return Collection.class.isAssignableFrom(this.element.getType());
	}

	@Override
	public boolean isMap() {
		return Map.class.isAssignableFrom(this.element.getType());
	}
}
