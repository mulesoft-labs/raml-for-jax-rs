package com.mulesoft.jaxrs.raml.annotation.model.jdt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.core.SourceType;

import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;
import com.mulesoft.jaxrs.raml.annotation.model.IBasicModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;
import com.mulesoft.jaxrs.raml.generator.popup.actions.GenerationException;

public abstract class JDTAnnotatable implements IBasicModel {

	private static final String VALUE = "value";
	protected org.eclipse.jdt.core.IAnnotatable tm;

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tm == null) ? 0 : tm.hashCode());
		return result;
	}

	public String getDocumentation() {
		try {
			IMember iMethod = (IMember) tm;
			ISourceRange javadocRange = iMethod.getJavadocRange();
			if (javadocRange != null) {
				String attachedJavadoc = iMethod
						.getCompilationUnit()
						.getSource()
						.substring(
								javadocRange.getOffset(),
								javadocRange.getOffset()
										+ javadocRange.getLength());
				attachedJavadoc = attachedJavadoc.substring(3,
						attachedJavadoc.length() - 2);
				StringReader rr = new StringReader(attachedJavadoc);
				BufferedReader mm = new BufferedReader(rr);
				StringBuilder bld = new StringBuilder();
				while (true) {
					try {
						String s = mm.readLine();
						if (s == null) {
							break;
						}
						int indexOf = s.indexOf('*');
						if (indexOf != -1) {
							s = s.substring(indexOf + 1);
						}
						s = s.trim();
						if (s.startsWith("@")) { //$NON-NLS-1$
							continue;
						}
						bld.append(s);
						bld.append('\n');
					} catch (IOException e) {
						break;
					}
				}
				return bld.toString().trim();
			}
			return null;
		} catch (JavaModelException e) {
			throw new IllegalStateException();
		}
	}

	protected Class<?> getBasicJavaType(String returnType) {
		if (returnType.equals("[B")){
			return byte[].class;
		}
		returnType=Signature.getElementType(returnType);
		if (returnType.startsWith("Q") && returnType.endsWith(";")) { //$NON-NLS-1$ //$NON-NLS-2$
			IType ownerType = (IType) ((IMember) tm)
					.getAncestor(IJavaElement.TYPE);
			String typeName = returnType.substring(1, returnType.length() - 1);
			String removeCapture = Signature.getTypeErasure(typeName);
			try {
				String[][] resolveType = ownerType.resolveType(removeCapture);
				if (resolveType == null) {
					throw new GenerationException(
							"Type " + typeName + " cannot be resolved", "Type " + typeName + " cannot be resolved, maybe because of the compilation errors"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				}
				if (resolveType.length == 1) {
					IType findType = ownerType.getJavaProject().findType(
							resolveType[0][0] + '.' + resolveType[0][1]);

				}
			} catch (JavaModelException e) {
				return null;
			}

		}
		if (returnType.equals("QString;")){
			return String.class;
		}
		if (returnType.equals("QInteger;")){
			return Integer.class;
		}
		if (returnType.equals("QLong;")){
			return Long.class;
		}
		if (returnType.equals("QShort;")){
			return Short.class;
		}
		if (returnType.equals("QDouble;")){
			return Double.class;
		}
		if (returnType.equals("QCharacter;")){
			return Character.class;
		}
		if (returnType.equals("QFloat;")){
			return Float.class;
		}
		if (returnType.equals("QByte;")){
			return Float.class;
		}
		if (returnType.equals("I")){
			return int.class;
		}
		if (returnType.equals("I")){
			return int.class;
		}
		if (returnType.equals("B")){
			return byte.class;
		}
		if (returnType.equals("C")){
			return char.class;
		}
		if (returnType.equals("D")){
			return double.class;
		}
		if (returnType.equals("F")){
			return float.class;
		}
		if (returnType.equals("J")){
			return long.class;
		}
		if (returnType.equals("S")){
			return short.class;
		}
		if (returnType.equals("Z")){
			return boolean.class;
		}		
		return null;
	}

	protected ITypeModel doGetType(IMember iMethod, String returnType)
			throws JavaModelException {
		if (returnType.startsWith("Q") && returnType.endsWith(";")) { //$NON-NLS-1$ //$NON-NLS-2$
			IType ownerType = (IType) iMethod.getAncestor(IJavaElement.TYPE);
			String typeName = returnType.substring(1, returnType.length() - 1);
			String removeCapture = Signature.getTypeErasure(typeName);
			String[][] resolveType = ownerType.resolveType(removeCapture);
			if (resolveType == null) {
				throw new GenerationException(
						"Type " + typeName + " cannot be resolved", "Type " + typeName + " cannot be resolved, maybe because of the compilation errors"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			}
			if (resolveType.length == 1) {
				IType findType = ownerType.getJavaProject().findType(
						resolveType[0][0] + '.' + resolveType[0][1]);
				if (findType != null ) {
					if(findType instanceof SourceType){
						return new JDTType(findType);
					}
					return new com.mulesoft.jaxrs.raml.annotation.model.reflection.ReflectionType(String.class);
				}
			}
		}
		return null;
	}

	protected JDTType doGetJAXBType(IMember iMethod, String returnType)
			throws JavaModelException {
		returnType = Signature.getElementType(returnType);
		if (returnType.startsWith("Q") && returnType.endsWith(";")) { //$NON-NLS-1$ //$NON-NLS-2$
			IType ownerType = (IType) iMethod.getAncestor(IJavaElement.TYPE);
			String typeName = returnType.substring(1, returnType.length() - 1);

			String removeCapture = Signature.getTypeErasure(typeName);
			if (isCollection(removeCapture)) {
				String[] typeArguments = Signature.getTypeArguments(returnType);
				if (typeArguments.length > 0) {
					removeCapture = typeArguments[0];
					removeCapture = removeCapture.substring(1,
							removeCapture.length() - 1);
				}
			}
			String[][] resolveType = ownerType.resolveType(removeCapture);
			if (resolveType == null) {
				throw new GenerationException(
						"Type " + typeName + " cannot be resolved", "Type " + typeName + " cannot be resolved, maybe because of the compilation errors"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			}
			if (resolveType.length == 1) {
				IType findType = ownerType.getJavaProject().findType(
						resolveType[0][0] + '.' + resolveType[0][1]);
				if (findType != null && findType instanceof SourceType) {
					return new JDTType(findType);
				}
			}
		}
		return null;
	}

	static HashSet<String> collectionTypes = new HashSet<String>();

	static {
		addType(Collection.class);
		addType(List.class);
		addType(Set.class);
		addType(LinkedHashSet.class);
		addType(HashSet.class);
		addType(ArrayList.class);
		addType(LinkedList.class);
		addType(Stack.class);
		addType(Vector.class);
	}

	private boolean isCollection(String removeCapture) {
		return collectionTypes.contains(removeCapture);
	}

	private static void addType(Class<?> class1) {
		collectionTypes.add(class1.getSimpleName());

	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JDTAnnotatable other = (JDTAnnotatable) obj;
		if (tm == null) {
			if (other.tm != null)
				return false;
		} else if (!tm.equals(other.tm))
			return false;
		return true;
	}

	private IAnnotationModel[] mms;

	public JDTAnnotatable(IAnnotatable tm) {
		super();
		this.tm = tm;
	}

	public boolean hasAnnotation(String name) {
		IAnnotationModel[] annotations = getAnnotations();
		for (IAnnotationModel m : annotations) {
			if (m.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public IAnnotationModel getAnnotation(String name) {
		IAnnotationModel[] annotations = getAnnotations();
		for (IAnnotationModel m : annotations) {
			if (m.getName().equals(name)) {
				return m;
			}
		}
		return null;
	}

	public String getAnnotationValue(String annotation) {
		IAnnotationModel[] annotations = getAnnotations();
		for (IAnnotationModel m : annotations) {
			if (m.getName().equals(annotation)) {
				return m.getValue(VALUE);
			}
		}
		return null;
	}

	public String[] getAnnotationValues(String annotation) {
		IAnnotationModel[] annotations = getAnnotations();
		for (IAnnotationModel m : annotations) {
			if (m.getName().equals(annotation)) {
				return m.getValues(VALUE);
			}
		}
		return null;
	}

	public IAnnotationModel[] getAnnotations() {
		try {
			if (mms != null) {
				return mms;
			}
			IAnnotation[] annotations = tm.getAnnotations();
			mms = new IAnnotationModel[annotations.length];
			int a = 0;
			for (IAnnotation q : annotations) {
				mms[a++] = new JDTAnnotation(q);
			}
			return mms;
		} catch (JavaModelException e) {
			throw new IllegalStateException();
		}
	}

}
