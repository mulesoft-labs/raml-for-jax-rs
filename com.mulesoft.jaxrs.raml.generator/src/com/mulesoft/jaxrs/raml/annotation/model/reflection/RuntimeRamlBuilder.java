package com.mulesoft.jaxrs.raml.annotation.model.reflection;

import com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor;

public class RuntimeRamlBuilder {

	
	protected ResourceVisitor visitor=new RuntimeResourceVisitorFactory(null,null).createResourceVisitor();
	
	public void addClass(Class<?>clazz){
		visitor.visit(new ReflectionType(clazz));
	}
	
	public void addClasses(Class<?>... clazz){
		for (Class<?> c:clazz){
			visitor.visit(new ReflectionType(c));
		}
	}
	
	public String toRAML(){
		return visitor.getRaml();
	}
}
