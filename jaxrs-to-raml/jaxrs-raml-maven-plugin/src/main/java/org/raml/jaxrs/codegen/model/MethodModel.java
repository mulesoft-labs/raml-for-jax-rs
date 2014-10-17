package org.raml.jaxrs.codegen.model;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import com.mulesoft.jaxrs.raml.annotation.model.IDocInfo;
import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.IParameterModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

public class MethodModel extends BasicModel implements IMethodModel {

	private ExecutableElement element;

	public MethodModel(ExecutableElement x) {
		this.element=x;
	}

	
	public IParameterModel[] getParameters() {
		List<? extends VariableElement> parameters = element.getParameters();
		ArrayList<IParameterModel>result=new ArrayList<IParameterModel>();
		for (VariableElement q:parameters){
			result.add(new ParameterModel(q));
		}
		return result.toArray(new IParameterModel[result.size()]);
	}

	
	public IDocInfo getBasicDocInfo() {
		return new IDocInfo() {
			
			
			public String getReturnInfo() {
				return ""; //$NON-NLS-1$
			}
			
			
			public String getDocumentation(String pName) {
				return ""; //$NON-NLS-1$
			}
			
			
			public String getDocumentation() {
				return ""; //$NON-NLS-1$
			}
		};
	}

	
	public Element element() {
		return element;
	}

	
	public ITypeModel getReturnedType() {
//		TypeMirror returnType = element.getReturnType();
//		if (returnType != null && returnType instanceof DeclaredType) {
//			DeclaredType declaredType = (DeclaredType) returnType;
//			TypeElement returnTypeElement = (TypeElement) declaredType.asElement();
//			return new APTType(returnTypeElement);
//		}
		return null;
	}

	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((element == null) ? 0 : element.hashCode());
		return result;
	}

	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodModel other = (MethodModel) obj;
		if (element == null) {
			if (other.element != null)
				return false;
		} else if (!element.equals(other.element))
			return false;
		return true;
	}
	
	
	public ITypeModel getBodyType() {		
		return null;
	}

}
