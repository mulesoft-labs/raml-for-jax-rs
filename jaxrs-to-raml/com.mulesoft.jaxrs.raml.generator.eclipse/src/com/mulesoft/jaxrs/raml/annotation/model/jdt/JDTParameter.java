package com.mulesoft.jaxrs.raml.annotation.model.jdt;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.JavaModelException;

import com.mulesoft.jaxrs.raml.annotation.model.IParameterModel;

public class JDTParameter extends JDTAnnotatable implements IParameterModel{

	public JDTParameter(ILocalVariable var) {
		super(var);
	}
	
	
	public String getName() {
		return ((ILocalVariable) tm).getElementName();
	}
	

	
	public String getDocumentation() {
		try {
			return ((ILocalVariable) tm).getAttachedJavadoc(new NullProgressMonitor());
		} catch (JavaModelException e) {
			throw new IllegalStateException();
		}
	}

	
	public String getParameterType() {
		return ((ILocalVariable) tm).getTypeSignature();
	}

	
	public boolean required() {
		return false;
	}
}