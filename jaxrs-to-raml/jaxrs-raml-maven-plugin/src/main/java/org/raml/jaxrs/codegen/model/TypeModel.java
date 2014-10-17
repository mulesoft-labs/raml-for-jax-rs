package org.raml.jaxrs.codegen.model;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

public class TypeModel extends BasicModel implements ITypeModel{

	public TypeModel() {
	}
	
	private String qualifiedName;
	
	private ArrayList<IMethodModel> methods = new ArrayList<IMethodModel>();
	
	public IMethodModel[] getMethods() {
		return methods.toArray(new IMethodModel[methods.size()]);
	}
	
	public void addMethod(IMethodModel method){
		methods.add(method);
	}

	
	public String getFullyQualifiedName() {
		return qualifiedName;
	}
	
	public void setFullyQualifiedName(String qualifiedName) {
		this.qualifiedName = qualifiedName;
	}
}
