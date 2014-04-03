package com.mulesoft.jaxrs.raml.annotation.model.apt;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

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
		return null;
	}

	public String[] getAnnotationValues(String annotation) {
		return null;
	}

	public boolean hasAnnotation(String name) {
		return false;
	}
}
