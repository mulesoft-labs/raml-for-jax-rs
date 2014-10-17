package org.raml.jaxrs.codegen.spoon;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import org.raml.jaxrs.codegen.model.BasicModel;
import org.raml.jaxrs.codegen.model.TypeModel;

import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;
import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;

public class SpoonProcessor<T extends Object> extends AbstractProcessor<CtClass<T>> {

	public void process(CtClass<T> classElement) {
		
		TypeModel type = new TypeModel();
		fillBasic(type,classElement);
		
		String qualifiedName = classElement.getQualifiedName();
		type.setFullyQualifiedName(qualifiedName);
		
		Set<CtMethod<?>> methods = classElement.getMethods();
		for(CtMethod<?> m : methods){
			IMethodModel methodModel = createMethodModel(m);
			type.addMethod(methodModel);
		}
		
	}

	private void fillBasic(BasicModel model, CtNamedElement namedElement) {
		
		String simpleName = namedElement.getSimpleName();
		String docComment = namedElement.getDocComment();
		
		model.setName(simpleName);
		model.setDocumentation(docComment);
		
		List<CtAnnotation<? extends Annotation>> annotations = namedElement.getAnnotations();
		for(CtAnnotation<? extends Annotation> a : annotations ){
			IAnnotationModel annotationModel = createannotationModel(a);
			model.addAnnotation(annotationModel);
		}
		
	}

	private IAnnotationModel createannotationModel(CtAnnotation<? extends Annotation> annotation) {

		return null;
	}
	
	private IMethodModel createMethodModel(CtMethod<?> m) {
		// TODO Auto-generated method stub
		return null;
	}
}
