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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((methods == null) ? 0 : methods.hashCode());
		result = prime * result
				+ ((qualifiedName == null) ? 0 : qualifiedName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TypeModel other = (TypeModel) obj;
		if (methods == null) {
			if (other.methods != null)
				return false;
		} else if (!methods.equals(other.methods))
			return false;
		if (qualifiedName == null) {
			if (other.qualifiedName != null)
				return false;
		} else if (!qualifiedName.equals(other.qualifiedName))
			return false;
		return true;
	}
}
