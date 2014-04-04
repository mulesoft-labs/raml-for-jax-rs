package com.mulesoft.jaxrs.raml.annotation.model.jdt;

import java.util.ArrayList;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaModelException;

import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;

public class JDTAnnotation implements IAnnotationModel {

	IAnnotation annotation;
	
	public JDTAnnotation(IAnnotation annotation) {
		super();
		this.annotation = annotation;
	}

	@Override
	public String getName() {
		return annotation.getElementName();
	}

	@Override
	public String getValue(String pairName) {
		IMemberValuePair[] memberValuePairs;
		try {
			memberValuePairs = annotation.getMemberValuePairs();
			for (IMemberValuePair pair:memberValuePairs){
				if (pair.getMemberName().equals(pairName)){
					return pair.getValue().toString();
				}
			}
		} catch (JavaModelException e) {
			throw new IllegalStateException(e);
		}		
		return null;
	}

	@Override
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

}
