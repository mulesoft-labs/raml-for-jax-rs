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

import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;
import com.mulesoft.jaxrs.raml.annotation.model.IBasicModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;
import com.mulesoft.jaxrs.raml.annotation.model.reflection.ReflectionType;
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
		if (returnType.equals("V")){
			return void.class;
		}
		return null;
	}

	protected ITypeModel doGetType(IMember iMethod, String returnType)
			throws JavaModelException {
		
		ITypeModel jaxbType = getJAXBType(returnType,iMethod.getDeclaringType());		
		return jaxbType;
	}

	protected List<ITypeModel> doGetJAXBTypes(IMember iMember, String typeSignature)
			throws JavaModelException {
		
		String returnType = Signature.getElementType(typeSignature);
		ArrayList<ITypeModel> list = new ArrayList<ITypeModel>();
		Class<?> basicJavaType = getBasicJavaType(returnType);
		if(basicJavaType!=null){
			list.add(new ReflectionType(basicJavaType));
		}
		else{
			IType ownerType = (IType) iMember.getAncestor(IJavaElement.TYPE);
			if (isCollection(iMember,typeSignature)) {
				String[] typeArguments = Signature.getTypeArguments(typeSignature);
				if (typeArguments.length > 0) {
					String paramTypeName = typeArguments[0];
					ITypeModel paramType = getJAXBType(paramTypeName, ownerType);
					if(paramType!=null){
						list.add(paramType);
					}
				}
			}
			else if (isMap(iMember,typeSignature)) {
				String[] typeArguments = Signature.getTypeArguments(typeSignature);
				if (typeArguments.length > 0) {
					String keyTypeName = typeArguments[0];
					ITypeModel keyType = getJAXBType(keyTypeName, ownerType);
					if(keyType!=null){
						list.add(keyType);
					}
				}
				if (typeArguments.length > 1) {
					String valueTypeName = typeArguments[1];
					ITypeModel valueType = getJAXBType(valueTypeName, ownerType);
					if(valueType!=null){
						list.add(valueType);
					}
				}
			}
			else{
				ITypeModel type = getJAXBType(returnType, ownerType);
				if(type!=null){
					list.add(type);
				}
			}
		}
		return list;
	}

	private ITypeModel getJAXBType(String typeName, IType ownerType)
			throws JavaModelException {
		if(typeName.startsWith("[")){
			typeName=typeName.substring(1);
		}
		Class<?> basicJavaType = getBasicJavaType(typeName);
		if(basicJavaType!=null){
			return new ReflectionType(basicJavaType);
		}
		if(typeName.startsWith("T")&&typeName.endsWith(";")){
			return new ReflectionType(Object.class);
		}
		
		IType resolveType = resolveType(ownerType, typeName);
		if(resolveType==null){
			return null;
		}
		return new JDTType(resolveType);
	}

	protected IType resolveType(IType ownerType, String typeName)
			throws JavaModelException {
		
		if(typeName.startsWith("[")){
			typeName=typeName.substring(1);
		}
		
		if(typeName.startsWith("T")&&typeName.endsWith(";")){
			return null;
		}
		
		if(!typeName.endsWith(";")){
			typeName = Signature.getTypeErasure(typeName);
			IType type = ownerType.getJavaProject().findType(typeName);
			if(type==null){
				String[][] resolveType = ownerType.resolveType(typeName);
				if (resolveType == null) {
					throw new GenerationException(
							"Type " + typeName + " cannot be resolved", "Type " + typeName + " cannot be resolved, maybe because of the compilation errors"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				}
				if (resolveType.length == 1) {
					type = ownerType.getJavaProject().findType(
							resolveType[0][0] + '.' + resolveType[0][1]);				
				}
			}
			return type;
		}
		
		if(typeName.startsWith("L")&&typeName.endsWith(";")){
			typeName = Signature.getTypeErasure(typeName);
			typeName = typeName.substring(1, typeName.length()-1);
			IType type = ownerType.getJavaProject().findType(typeName);
			return type;
		}
		
		String tn;
		if(typeName.startsWith("Q")&&typeName.endsWith(";")){			
			tn = Signature.getTypeErasure(typeName);
		}
		else{
			tn = typeName;
		}
		tn = tn.substring(1, tn.length() - 1);
		String[][] resolveType = ownerType.resolveType(tn);
		if (resolveType == null) {
			throw new GenerationException(
					"Type " + typeName + " cannot be resolved", "Type " + typeName + " cannot be resolved, maybe because of the compilation errors"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
		IType findType = null;
		if (resolveType.length == 1) {
			findType = ownerType.getJavaProject().findType(
					resolveType[0][0] + '.' + resolveType[0][1]);				
		}
		return findType;
	}
	
	private boolean implementsInterface(IType type, String interfaceName){
		
		if(type==null){
			return false;
		}
		
		if(type.getFullyQualifiedName().equals(interfaceName)){
			return true;
		}
		
		String[] superInterfaceNames = new String[0];
		try {
			superInterfaceNames = type.getSuperInterfaceNames();
		} catch (JavaModelException e) {}
		
		for(String iName: superInterfaceNames){
			if(iName.equals(interfaceName)){
				return true;
			}
		}
		
		try{
			String superclassName = type.getSuperclassName();	
			if(superclassName!=null){
				Class<?> basicJavaType = getBasicJavaType(superclassName);
				if(basicJavaType==null){
					IType supertype = resolveType(type, superclassName);
					if(implementsInterface(supertype, interfaceName)){
						return true;
					}
				}
			}
		} catch (JavaModelException e) {}
		
		for(String iName: superInterfaceNames){
			IType interfaceType = null;
			try{
				interfaceType = resolveType(type, iName);
			} catch (JavaModelException e) {}
			if(interfaceType==null){
				continue;
			}
			if(implementsInterface(interfaceType, interfaceName)){
				return true;
			}
		}
		return false;
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
	
	private boolean isMap(IType type) {
		return implementsInterface(type, "java.util.Map");
	}
	
	private boolean isCollection(IType type) {
		return implementsInterface(type, "java.util.Collection");
	}
	
	protected boolean isCollection(IMember iMemeber, String typeSignature)
			throws JavaModelException {
		
		if(typeSignature.startsWith("[")){
			return true;
		}
		
		typeSignature = Signature.getElementType(typeSignature);
		Class<?> basicJavaType = getBasicJavaType(typeSignature);
		if(basicJavaType!=null){
			return false;
		}
		if(typeSignature.startsWith("T")&&typeSignature.endsWith(";")){
			return false;
		}		
		IType ownerType = (IType) iMemeber.getAncestor(IJavaElement.TYPE);		
		IType type = resolveType(ownerType, typeSignature);
		if(type==null){
			return false;
		}
		return isCollection(type);
	}
	
	protected boolean isMap(IMember iMember, String typeSignature)
			throws JavaModelException {
		
		typeSignature = Signature.getElementType(typeSignature);
		Class<?> basicJavaType = getBasicJavaType(typeSignature);
		if(basicJavaType!=null){
			return false;
		}
		if(typeSignature.startsWith("T")&&typeSignature.endsWith(";")){
			return false;
		}
		IType ownerType = (IType) iMember.getAncestor(IJavaElement.TYPE);		
		IType type = resolveType(ownerType, typeSignature);
		if(type==null){
			return false;
		}
		return isMap(type);
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
	
	public boolean hasAnnotationWithCanonicalName(String name) {
		IAnnotationModel[] annotations = getAnnotations();
		for (IAnnotationModel m : annotations) {
			if (m.getCanonicalName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public IAnnotationModel getAnnotationByCanonicalName(String name) {
		IAnnotationModel[] annotations = getAnnotations();
		for (IAnnotationModel m : annotations) {
			if (m.getCanonicalName().equals(name)) {
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
