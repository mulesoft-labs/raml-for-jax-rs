package com.mulesoft.jaxrs.raml.annotation.model.apt;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

public class APTType extends APTModel implements ITypeModel{

	private TypeElement element;

	public APTType(TypeElement element) {
		this.element = element;
	}
	
	@Override
	public IMethodModel[] getMethods() {
		List<? extends Element> enclosedElements = element.getEnclosedElements();
		ArrayList<IMethodModel>result=new ArrayList<IMethodModel>();
		for (Element r:enclosedElements){
			if (r instanceof ExecutableElement){
				ExecutableElement x=(ExecutableElement) r;
				result.add(new APTMethodModel(x));
			}
		}
		return result.toArray(new IMethodModel[result.size()]);
	}

	@Override
	public Element element() {
		return element;
	}
}
