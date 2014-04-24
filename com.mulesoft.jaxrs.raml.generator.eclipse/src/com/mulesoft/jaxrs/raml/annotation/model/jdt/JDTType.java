package com.mulesoft.jaxrs.raml.annotation.model.jdt;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

public class JDTType extends JDTAnnotatable implements ITypeModel {

	public JDTType(IType tm) {
		super(tm);
	}

	
	public String getName() {
		return ((IType) tm).getElementName();
	}

	
	public IMethodModel[] getMethods() {
		try {
			IMethod[] methods = ((IType) tm).getMethods();
			IMethodModel[] mm = new IMethodModel[methods.length];
			int a = 0;
			for (IMethod m : methods) {
				mm[a++] = new JDTMethod(m);
			}
			return mm;
		} catch (JavaModelException e) {
			throw new IllegalStateException(e);
		}
	}
	
	
	public String getDocumentation() {
		try {
			return ((IType) tm).getAttachedJavadoc(new NullProgressMonitor());
		} catch (JavaModelException e) {
			throw new IllegalStateException();
		}
	}

}
