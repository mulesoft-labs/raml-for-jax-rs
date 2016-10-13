package com.mulesoft.jaxrs.raml.annotation.model.apt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

import org.aml.typesystem.IAnnotationModel;
import org.aml.typesystem.ITypeModel;

/**
 * <p>APTAnnotation class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class APTAnnotation implements IAnnotationModel{

	AnnotationMirror mirror;
	private ProcessingEnvironment env;
	
	/**
	 * <p>Constructor for APTAnnotation.</p>
	 *
	 * @param m a {@link javax.lang.model.element.AnnotationMirror} object.
	 */
	public APTAnnotation(AnnotationMirror m,ProcessingEnvironment env) {
		this.mirror=m;
		this.env=env;
	}

	
	/**
	 * <p>getName.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getName() {
		DeclaredType annotationType = mirror.getAnnotationType();
		return annotationType.asElement().getSimpleName().toString();
	}

	
	/** {@inheritDoc} */
	public String getValue(String pairName) {
		Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = mirror.getElementValues();
		for (ExecutableElement q:elementValues.keySet()){
			String name=q.getSimpleName().toString();
			if (name.equals(pairName)){
				return mirror.getElementValues().get(q).getValue().toString();
			}
		}
		return null;
	}

	
	/** {@inheritDoc} */
	public String[] getValues(String value) {
		Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = mirror.getElementValues();
		for (ExecutableElement q:elementValues.keySet()){
			String name=q.getSimpleName().toString();
			if (name.equals(value)){
				Object value2 = mirror.getElementValues().get(q).getValue();
				if (value2 instanceof String){
					return new String[]{(String) value2};
				}
				return (String[]) value2;
			}
		}
		return null;
	}


	/** {@inheritDoc} */
	@Override
	public IAnnotationModel[] getSubAnnotations(String pairName) {
		return new IAnnotationModel[0];
	}


	@Override
	public String getCanonicalName() {
		DeclaredType annotationType = mirror.getAnnotationType();
		Element element = annotationType.asElement();
		ArrayList<String> list = new ArrayList<String>();
		while(element!=null){
			list.add(element.getSimpleName().toString());
		}
		Collections.reverse(list);
		StringBuilder bld = new StringBuilder();
		for(String s : list){
			bld.append(s).append(".");
		}
		String result = bld.substring(0, bld.length()-1);
		return result;
	}


	@Override
	public ITypeModel getType() {
		return new APTType((TypeElement)mirror.getAnnotationType().asElement(), env);		
	}


	@Override
	public Map<String, Object> allValues() {
		// TODO Auto-generated method stub
		return null;
	}

}
