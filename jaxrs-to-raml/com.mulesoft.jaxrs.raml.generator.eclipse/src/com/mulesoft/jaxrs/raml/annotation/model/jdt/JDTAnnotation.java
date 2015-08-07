package com.mulesoft.jaxrs.raml.annotation.model.jdt;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMemberValuePairBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;

import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;

public class JDTAnnotation implements IAnnotationModel {

	IAnnotation annotation;
	
	public JDTAnnotation(IAnnotation annotation) {
		super();
		this.annotation = annotation;
	}

	
	public String getName() {
		String qName = annotation.getElementName();
		int ind = qName.lastIndexOf('.');
		ind++;
		return qName.substring(ind);
	}

	
	public String getValue(String pairName) {
		IMemberValuePair[] memberValuePairs;
		try {			
			ASTParser parser = ASTParser.newParser(AST.JLS4);
			parser.setProject(annotation.getJavaProject());
			try{
			IBinding[] bindings = parser.createBindings(new IJavaElement[]{annotation}, new NullProgressMonitor());
			if(bindings.length>0&&bindings[0] instanceof IAnnotationBinding){
				IAnnotationBinding bnd = (IAnnotationBinding) bindings[0];
				IMemberValuePairBinding[] pairs = bnd.getDeclaredMemberValuePairs();				
				for(IMemberValuePairBinding pair : pairs){
					if(pair.getName().equals(pairName)){
						Object value = pair.getValue();
						ArrayList<Object> values = new ArrayList<Object>();
						if(value.getClass().isArray()){
							for(int i = 0 ; i < Array.getLength(value) ; i++){
								values.add(Array.get(value, i));
							}
						}
						else{
							values.add(value);
						}
						StringBuilder bld = new StringBuilder();
						for(Object v : values){
							if(v instanceof Class){
								bld.append(((Class<?>)v).getCanonicalName());
							}
							else if(v instanceof ITypeBinding){
								bld.append(((ITypeBinding)v).getQualifiedName());
							}
							else if(v instanceof IVariableBinding){
								bld.append(((IVariableBinding)v).getName());
							}
							else{
								bld.append(v.toString());
							}
							bld.append(", ");
						}
						String str = bld.toString();
						str = str.substring(0, str.length()-", ".length());
						return str;
					}
				}
			}
			}
			catch(Exception e){
				//Handle JDT bugs. For example, in JDT 4.3.2, annotations inside binary classes can not be processed.
			}
			memberValuePairs = annotation.getMemberValuePairs();
			for (IMemberValuePair pair:memberValuePairs){
				if (pair.getMemberName().equals(pairName)){
					
					int valueKind = pair.getValueKind();
					String string = pair.getValue().toString();
					if (valueKind==IMemberValuePair.K_CLASS){
						IType ancestor = (IType) annotation.getAncestor(IJavaElement.TYPE);
						String[][] resolveType = ancestor.resolveType(string);
						if( resolveType.length>0){
							String[] vl=resolveType[0];
							return vl[0]+"."+vl[1];
						}
						//TODO resolve value;
						
					}
										
					return string;
				}
			}
		} catch (JavaModelException e) {
			throw new IllegalStateException(e);
		}	
		return null;
	}

	
	public String[] getValues(String value) {
		IMemberValuePair[] memberValuePairs;
		try {
			memberValuePairs = annotation.getMemberValuePairs();
			for (IMemberValuePair pair:memberValuePairs){
				if (pair.getMemberName().equals(value)){
					int valueKind = pair.getValueKind();
					if (valueKind==IMemberValuePair.K_UNKNOWN){
						ISourceRange sourceRange = annotation.getSourceRange();
						ICompilationUnit unit= (ICompilationUnit) annotation.getAncestor(IJavaElement.COMPILATION_UNIT);
						String source = unit.getSource();
						String substring = source.substring(sourceRange.getOffset(), sourceRange.getOffset()+sourceRange.getLength());
						ArrayList<String>mT=new ArrayList<String>();
						if (substring.toLowerCase().indexOf("xml")!=-1){
							mT.add("application/xml");
						}
						if (substring.toLowerCase().indexOf("json")!=-1){
							mT.add("application/json");
						}
						return mT.toArray(new String[mT.size()]);
					}
					Object value2 = pair.getValue();
					
					if (value2 instanceof String){
						return new String[]{(String) value2};
					}
					if (value2 instanceof Object[]){
						String[] vv=new String[((Object[]) value2).length];
						for (int a=0;a<vv.length;a++){
							Object object = ((Object[]) value2)[a];
							if (object==null){
								return null;
							}
							vv[a]=(String) object;
						}
						return vv;
					}					
				}
			}
		} catch (JavaModelException e) {
			throw new IllegalStateException(e);
		}		
		return null;
	}


	
	public IAnnotationModel[] getSubAnnotations(String pairName) {
		IMemberValuePair[] memberValuePairs;
		try {
			memberValuePairs = annotation.getMemberValuePairs();
			for (IMemberValuePair pair:memberValuePairs){
				if (pair.getMemberName().equals(pairName)){
					Object value = pair.getValue();
					if (value instanceof Object[]){
						Object[] objects = (Object[])value;
						IAnnotationModel[] result=new IAnnotationModel[objects.length];
						for (int a=0;a<objects.length;a++){
							result[a]=new JDTAnnotation((IAnnotation) objects[a]);
							
						}
						return result;
					}
					return null;
				}
			}
		} catch (JavaModelException e) {
			throw new IllegalStateException(e);
		}		
		return null;
	
	}


	@Override
	public String getCanonicalName() {
		return annotation.getElementName();
	}

}
