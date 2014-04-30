package com.mulesoft.jaxrs.raml.annotation.model.apt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

//import com.google.common.base.Function;
//import com.google.common.collect.Collections2;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;
import com.mulesoft.jaxrs.raml.annotation.model.IBasicModel;

public abstract class APTModel implements IBasicModel{

	private static final String VALUE_METHOD_ID = "value"; //$NON-NLS-1$

	public abstract Element element();
	
	
	public String getName() {
		return element().getSimpleName().toString();
	}
	
	public String getDocumentation() {
		return "Needs to be documented";
	}

	public IAnnotationModel[] getAnnotations() {
		List<? extends AnnotationMirror> annotationMirrors = element().getAnnotationMirrors();
		ArrayList<APTAnnotation>w=new ArrayList<APTAnnotation>();
		for (AnnotationMirror m:annotationMirrors){
			w.add(new APTAnnotation(m));
		}
		return w.toArray(new IAnnotationModel[w.size()]);
	}

	public String getAnnotationValue(String annotation) {
		List<? extends AnnotationMirror> annotationMirrors = element().getAnnotationMirrors();
		for (AnnotationMirror annotationMirror : annotationMirrors) {
			String name = annotationMirror.getAnnotationType().asElement().getSimpleName().toString();
			if (name.equals(annotation)) {
				for( Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet() )
                {
                    if( VALUE_METHOD_ID.equals(entry.getKey().getSimpleName().toString() ) ) 
                    {
                        AnnotationValue action = entry.getValue();
                        return action.getValue().toString();
                    }
                }
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public String[] getAnnotationValues(String annotation) {
		List<? extends AnnotationMirror> annotationMirrors = element().getAnnotationMirrors();
		for (AnnotationMirror annotationMirror : annotationMirrors) {
			String name = annotationMirror.getAnnotationType().asElement().getSimpleName().toString();
			if (name.equals(annotation)) {
				Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
				for (ExecutableElement q:elementValues.keySet()){
					String elementName=q.getSimpleName().toString();
					if (elementName.equals(VALUE_METHOD_ID)){
						Object value2 = annotationMirror.getElementValues().get(q).getValue();
						if (value2 instanceof String){
							return new String[]{(String) value2};
						} else if (value2 instanceof List) {
							String[] result = Collections2.transform(((List<AnnotationValue>) value2), new Function<AnnotationValue,String>() {

								
								public String apply(AnnotationValue arg0) {
									return arg0.getValue().toString();
								}
								
							}).toArray(new String[0]);
							return result;
						}
						return (String[]) value2;
					}
				}
			}
		}
		return null;
	}

	public boolean hasAnnotation(String annotationName) {
		List<? extends AnnotationMirror> annotationMirrors = element().getAnnotationMirrors();
		for (AnnotationMirror annotationMirror : annotationMirrors) {
			String name = annotationMirror.getAnnotationType().asElement().getSimpleName().toString();
			if (name.equals(annotationName)) {
				return true;
			}
		}
		return false;
	}
}
