package com.mulesoft.jaxrs.raml.annotation.model.apt;

import java.util.Set;

import javax.annotation.processing.Completion;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;


public class RAMLAnnotationProcessor implements javax.annotation.processing.Processor {

	@Override
	public Set<String> getSupportedOptions() {
		return null;
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return null;
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.RELEASE_5;
	}

	@Override
	public void init(ProcessingEnvironment processingEnv) {
		
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		return true;
	}

	@Override
	public Iterable<? extends Completion> getCompletions(Element element,
			AnnotationMirror annotation, ExecutableElement member,
			String userText) {
		return null;
	}
}