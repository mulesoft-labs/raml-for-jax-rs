package com.mulesoft.jaxrs.raml.annotation.model.jdt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.mulesoft.jaxrs.raml.annotation.model.IGenericElement;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeParameter;

abstract public class JDTGenericElement extends JDTAnnotatable implements IGenericElement {

	public JDTGenericElement(IType type) {
		super(type);
	}
	
	public JDTGenericElement(IMethod method) {
		super(method);
	}

	@Override
	public List<ITypeParameter> getTypeParameters() {
		
		org.eclipse.jdt.core.ITypeParameter[] typeParameters = null;
		try {
			if(this.tm instanceof IType){				
				typeParameters = ((IType)tm).getTypeParameters();				
			}
			else if(this.tm instanceof IMethod){
				typeParameters = ((IMethod)tm).getTypeParameters();
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		ArrayList<ITypeParameter> list = new ArrayList<ITypeParameter>();
		if(typeParameters==null||typeParameters.length==0){
			return list;
		}
		for(org.eclipse.jdt.core.ITypeParameter param : typeParameters){
			JDTTypeParameter model = new JDTTypeParameter(param);
			list.add(model);
		}
		return list;
	}

}
