package com.mulesoft.jaxrs.raml.annotation.model.apt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;

public abstract class APTModel {

	public abstract Element element();
	
	
	public String getName() {
		return element().getSimpleName().toString();
	}
	
	public String getDocumentation() {
		return "Neeeds to be documented";
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
				Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
				for( Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet() )
                {
                    if( "value".equals(entry.getKey().getSimpleName().toString() ) ) //$NON-NLS-1$
                    {
                        AnnotationValue action = entry.getValue();
                        return action.getValue().toString();
                    }
                }
			}
		}
		return null;
	}

	public String[] getAnnotationValues(String annotation) {
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
