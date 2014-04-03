package com.mulesoft.jaxrs.raml.annotation.model.jdt;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMemberValuePair;
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
					Object value2 = pair.getValue();
					if (value2 instanceof String){
						return new String[]{(String) value2};
					}
					if (value2 instanceof Object[]){
						String[] vv=new String[((Object[]) value2).length];
						for (int a=0;a<vv.length;a++){
							vv[a]=(String) ((Object[]) value2)[a];
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
