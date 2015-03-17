package com.mulesoft.jaxrs.raml.annotation.model.reflection;

import com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor;

/**
 * <p>RuntimeRamlBuilder class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class RuntimeRamlBuilder {

	
	protected ResourceVisitor visitor=new RuntimeResourceVisitor(null, null);
	
	/**
	 * <p>addClass.</p>
	 *
	 * @param clazz a {@link java.lang.Class} object.
	 */
	public void addClass(Class<?>clazz){
		visitor.visit(new ReflectionType(clazz));
	}
	
	/**
	 * <p>addClasses.</p>
	 *
	 * @param clazz a {@link java.lang.Class} object.
	 */
	public void addClasses(Class<?>... clazz){
		for (Class<?> c:clazz){
			visitor.visit(new ReflectionType(c));
		}
	}
	
	/**
	 * <p>toRAML.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String toRAML(){
		return visitor.getRaml();
	}
}
