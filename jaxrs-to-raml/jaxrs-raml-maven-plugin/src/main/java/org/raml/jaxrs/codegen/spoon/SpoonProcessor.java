package org.raml.jaxrs.codegen.spoon;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.raml.jaxrs.codegen.maven.ProxyType;
import org.raml.jaxrs.codegen.maven.TypeModelRegistry;
import org.raml.jaxrs.codegen.model.AnnotationModel;
import org.raml.jaxrs.codegen.model.BasicModel;
import org.raml.jaxrs.codegen.model.MethodModel;
import org.raml.jaxrs.codegen.model.ParameterModel;
import org.raml.jaxrs.codegen.model.TypeModel;

import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;
import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.IParameterModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;

public class SpoonProcessor{
	
	private TypeModelRegistry registry = new TypeModelRegistry(); 
	
	public void process(Collection<CtPackage> packages){
		if(packages==null){
			return;
		}
		for(CtPackage package_ : packages ){
			for( CtSimpleType<?> type : package_.getTypes()){
				process(type);
			} 
		}
		
	}
	
	public void process(CtSimpleType<?> classElement) {
		
		processType(classElement);		
	}

	private ITypeModel processType(CtSimpleType<?> classElement)
	{
		TypeModel type = new TypeModel();
		String qualifiedName = classElement.getQualifiedName();
		type.setFullyQualifiedName(qualifiedName);
		registry.registerType(type);
		
		fillBasic(type,classElement);		
		
		if(classElement instanceof CtClass){
			Set<CtMethod<?>> methods = ((CtClass<?>)classElement).getMethods();
			for(CtMethod<?> m : methods){
				IMethodModel methodModel = processMethod(m);
				type.addMethod(methodModel);
			}
		}
		return type;
	}


	private IAnnotationModel processAnnotation(CtAnnotation<? extends Annotation> annotation) {
		
		IAnnotationModel annotationModel = new AnnotationModel();
		
		
		
		return annotationModel;
	}
	
	private IAnnotationModel processJavaLangAnnotation(Annotation a) {
		IAnnotationModel annotationModel = new AnnotationModel();
		return annotationModel;
	}
	
	
	private IMethodModel processMethod(CtMethod<?> m) {
		
		MethodModel methodModel = new MethodModel();
		fillBasic(methodModel, m);
		CtTypeReference<?> returnedType = m.getType();
		ITypeModel returnedTypeModel = processTypeReference(returnedType);
		methodModel.setReturnedType(returnedTypeModel);
		
		List<CtParameter<?>> parameters = m.getParameters();
		for(CtParameter<?> p : parameters){
			IParameterModel parameterModel = processParameter(p);
			methodModel.addParameter(parameterModel);
		}
		return methodModel;
	}
	
	
	private IParameterModel processParameter(CtParameter<?> paramElement) {
		
		ParameterModel parameterModel = new ParameterModel();
		
		CtTypeReference<?> paramType = paramElement.getType();
		String qualifiedName = paramType.getQualifiedName();		
		parameterModel.setType(qualifiedName);
		parameterModel.setRequired(paramType.isPrimitive());
		
		fillBasic(parameterModel, paramElement);
		
		return parameterModel;
	}

	private void fillBasic(BasicModel model, CtNamedElement namedElement) {
		
		String simpleName = namedElement.getSimpleName();
		String docComment = namedElement.getDocComment();
		
		model.setName(simpleName);
		model.setDocumentation(docComment);
		
		List<CtAnnotation<? extends Annotation>> annotations = namedElement.getAnnotations();
		for(CtAnnotation<? extends Annotation> a : annotations ){
			IAnnotationModel annotationModel = processAnnotation(a);
			model.addAnnotation(annotationModel);
		}
		
	}
	
	private ITypeModel processTypeReference(CtTypeReference<?> typeReference)
	{
		String qualifiedName = typeReference.getQualifiedName();
		ITypeModel proxyType = new ProxyType(registry, qualifiedName);		
		
		ITypeModel registeredType = registry.getType(qualifiedName);
		if(registeredType!=null){
			return proxyType;
		}

		TypeModel type = new TypeModel();
		type.setFullyQualifiedName(qualifiedName);
		registry.registerType(type);
		
		fillReference(type, typeReference);
		
		Collection<CtExecutableReference<?>> methods = typeReference.getDeclaredExecutables();
		for(CtExecutableReference<?> m : methods){
			IMethodModel methodModel = processMethodReference(m);
			type.addMethod(methodModel);
		}
		
		return proxyType;
	}
	
	private IMethodModel processMethodReference(CtExecutableReference<?> methodElement) {
		
		MethodModel methodModel = new MethodModel();
		fillReference(methodModel,methodElement);
		List<CtTypeReference<?>> parameters = methodElement.getParameterTypes();
		for(CtTypeReference<?> p : parameters){
			IParameterModel parameterModel = processParameterReference(p);
			methodModel.addParameter(parameterModel);
		}
		return methodModel;
	}

	private IParameterModel processParameterReference(CtTypeReference<?> paramTypeReference) {
		
		ParameterModel parameterModel = new ParameterModel();
		parameterModel.setType(paramTypeReference.getQualifiedName());
		parameterModel.setRequired(paramTypeReference.isPrimitive());
		
		List<Annotation> annotations = paramTypeReference.getAnnotations();
		for(Annotation a : annotations){
			IAnnotationModel annotationModel = processJavaLangAnnotation(a);
			parameterModel.addAnnotation(annotationModel);
		}
		
		return parameterModel;
	}

	private void fillReference( BasicModel model, CtReference ref) {
		
		String simpleName = ref.getSimpleName();
		
		model.setName(simpleName);
		
		List<Annotation> annotations = ref.getAnnotations();
		for(Annotation a : annotations){
			IAnnotationModel annotationModel = processJavaLangAnnotation(a);
			model.addAnnotation(annotationModel);
		}
	}

	public TypeModelRegistry getRegistry() {
		return registry;
	}
}
