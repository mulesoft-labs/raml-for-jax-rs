package com.mulesoft.jaxrs.raml.annotation.model.apt;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import com.mulesoft.jaxrs.raml.annotation.model.IDocInfo;
import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.IParameterModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

public class APTMethodModel extends APTModel implements IMethodModel {

	private ExecutableElement element;

	public APTMethodModel(ExecutableElement x) {
		this.element=x;
	}

	

	@Override
	public IParameterModel[] getParameters() {
		List<? extends VariableElement> parameters = element.getParameters();
		ArrayList<IParameterModel>result=new ArrayList<IParameterModel>();
		for (VariableElement q:parameters){
			result.add(new APTParameter(q));
		}
		return result.toArray(new IParameterModel[result.size()]);
	}

	@Override
	public IDocInfo getBasicDocInfo() {
		return new IDocInfo() {
			
			@Override
			public String getReturnInfo() {
				return "";
			}
			
			@Override
			public String getDocumentation(String pName) {
				return "";
			}
			
			@Override
			public String getDocumentation() {
				return "";
			}
		};
	}

	@Override
	public Element element() {
		return element;
	}



	@Override
	public ITypeModel getReturnedType() {
		return null;
	}

}
