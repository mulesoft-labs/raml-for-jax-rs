package com.mulesoft.jaxrs.raml.annotation.model.jdt;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavadocContentAccess;

import com.mulesoft.jaxrs.raml.annotation.model.IFieldModel;
import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;
import com.mulesoft.jaxrs.raml.annotation.model.reflection.ReflectionType;
import com.mulesoft.jaxrs.raml.generator.popup.actions.GenerationException;

public class JDTType extends JDTGenericElement implements ITypeModel {

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
			IType typeDef = (IType) tm;
			String javadoc = typeDef.getAttachedJavadoc(new NullProgressMonitor());
			if(javadoc==null){
				Reader reader = JavadocContentAccess.getContentReader((IType)tm, true);
				if(reader!=null){
					int l = 0;
					StringBuilder bld = new StringBuilder();
					char[] buf = new char[1024];
					try {
						while((l=reader.read(buf))>=0){
							bld.append(buf,0,l);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					if(bld.length()>0){
						javadoc = bld.toString();
						if(javadoc.endsWith("\n/")){
							javadoc = javadoc.substring(0, javadoc.length()-2);
						}
					}
				}
			}
			return javadoc;
		} catch (JavaModelException e) {
			throw new IllegalStateException();
		}
	}


	
	public String getFullyQualifiedName() {
		return ((IType) tm).getFullyQualifiedName();
	}


	@Override
	public IFieldModel[] getFields() {
		try {
			IField[] methods = ((IType) tm).getFields();
			IFieldModel[] mm = new IFieldModel[methods.length];
			int a = 0;
			for (IField m : methods) {
				mm[a++] = new JDTField(m);
			}
			return mm;
		} catch (JavaModelException e) {
			throw new IllegalStateException(e);
		}
	}


	public IType getElement() {
		return ((IType) tm);
	}


	@Override
	public ITypeModel getSuperClass() {
		try {
			String signature = ((IType) tm).getSuperclassTypeSignature();
			if(signature==null){
				return null;
			}
			IType superType = this.resolveType(((IType) tm), signature);
			return new JDTType(superType);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}


	@Override
	public ITypeModel[] getImplementedInterfaces() {
		
		try {
			String[] interfaces = ((IType) tm).getSuperInterfaceTypeSignatures();
			ArrayList<ITypeModel> list = new ArrayList<ITypeModel>();
			for(String signature: interfaces){
				IType iFace = this.resolveType(((IType) tm), signature);
				if(iFace!=null){
					list.add(new JDTType(iFace));
				}
			}
			return list.toArray(new ITypeModel[list.size()]);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return new ITypeModel[0];
	}

	@Override
	public ITypeModel resolveClass(String qualifiedName) {
		try {
			Class<?> clazz = getBasicJavaType(qualifiedName);
			if(clazz!=null){
				return new ReflectionType(clazz);
			}
			IType iType = resolveType(this.getElement(), qualifiedName);
			if(iType==null){
				return null;
			}
			return new JDTType(iType);
		} catch (JavaModelException e) {
			return null;
		}
	}
}
