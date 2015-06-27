package com.mulesoft.jaxrs.raml.annotation.model.jdt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import com.mulesoft.jaxrs.raml.annotation.model.IFieldModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

public class JDTField extends JDTAnnotatable implements IFieldModel{

	public JDTField(IAnnotatable tm) {
		super(tm);
	}
	
	private boolean isGeneric;

	@Override
	public String getName() {
		return ((IField)tm).getElementName();
	}

	@Override
	public ITypeModel getType() {
		try {
			return doGetType(((IField)tm), ((IField)tm).getTypeSignature());
		} catch (JavaModelException e) {
			return null;
		}
	}

	@Override
	public boolean isStatic() {
		try {
			return Flags.isStatic(((IField)tm).getFlags());
		} catch (JavaModelException e) {
			return false;
		}
	}

	@Override
	public boolean isPublic() {
		try {
			return Flags.isPublic(((IField)tm).getFlags());
		} catch (JavaModelException e) {
			return false;
		}
	}

	@Override
	public List<ITypeModel> getJAXBTypes() {
		try {
			List<ITypeModel> list = doGetJAXBTypes(((IField)tm), ((IField)tm).getTypeSignature());
			return list;
		} catch (JavaModelException e) {
			return null;
		}
	}


	@Override
	public Class<?> getJavaType() {
		try {
			String returnType = ((IField)tm).getTypeSignature();
			return getBasicJavaType(returnType);
		} catch (JavaModelException e) {
			return null;
		}
	}

	public boolean isGeneric() {
		return isGeneric;
	}

	public void setGeneric(boolean isGeneric) {
		this.isGeneric = isGeneric;
	}

	@Override
	public boolean isCollection() {
		try {
			return isCollection((IField)tm, ((IField)tm).getTypeSignature());
		} catch (JavaModelException e) {
			return false;
		}
	}

	@Override
	public boolean isMap() {
		try {
			return isMap((IField)tm, ((IField)tm).getTypeSignature());
		} catch (JavaModelException e) {
			return false;
		}
	}

}
