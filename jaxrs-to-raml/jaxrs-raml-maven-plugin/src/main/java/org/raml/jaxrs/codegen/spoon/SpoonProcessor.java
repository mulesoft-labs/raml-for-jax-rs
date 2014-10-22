/*
 * Copyright 2013 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.jaxrs.codegen.spoon;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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

import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;

public class SpoonProcessor{
	
	private TypeModelRegistry registry = new TypeModelRegistry();
	
	private Factory factory;
	
	public SpoonProcessor(Factory factory) {
		this.factory = factory;
	}

	public void process(Collection<CtPackage> packages){
		if(packages==null){
			return;
		}
		for(CtPackage package_ : packages ){
			processPackage(package_); 
		}
		
	}

	private void processPackage(CtPackage package_) {
		Set<CtPackage> subPackages = package_.getPackages();
		if(subPackages!=null){
			for(CtPackage subPackage:subPackages){
				processPackage(subPackage);
			}
		}
		for( CtSimpleType<?> type : package_.getTypes()){
			process(type);
		}
	}
	
	public void process(CtSimpleType<?> classElement) {
		
		ITypeModel type = processType(classElement);
		registry.registerTargetType(type);
	}

	private ITypeModel processType(CtSimpleType<?> classElement)
	{
		TypeModel type = new TypeModel();
		String qualifiedName = classElement.getQualifiedName();
		type.setFullyQualifiedName(qualifiedName);
		registry.registerType(type);
		
		fillBasic(type,classElement);		
		
		if(classElement instanceof CtType){
			Set<CtMethod<?>> methods = ((CtType<?>)classElement).getMethods();
			for(CtMethod<?> m : methods){
				IMethodModel methodModel = processMethod(m);
				type.addMethod(methodModel);
			}
		}
		return type;
	}


	private IAnnotationModel processAnnotation(CtAnnotation<? extends Annotation> annotation) {
		
		String simpleName = annotation.getActualAnnotation().annotationType().getSimpleName();
		
		AnnotationModel annotationModel = new AnnotationModel();		
		annotationModel.setName(simpleName);
		
		Map<String, Object> elementValues = annotation.getElementValues();
		
		for(Map.Entry<String, Object> entry : elementValues.entrySet()){
			
			String key = entry.getKey();
			if(key==null){
				continue;
			}
			
			Object value = entry.getValue();
			
			ArrayList<CtAnnotation<?>> annotationList = toCtAnnotationList(value);
			if(annotationList!=null){
				int size = annotationList.size();
				IAnnotationModel[] annotationModels = new IAnnotationModel[size];
				for(int i = 0 ; i < size ; i++){
					CtAnnotation<?> subAnnotation = annotationList.get(i);
					IAnnotationModel subAnnotationModel = processAnnotation(subAnnotation);
					annotationModels[i] = subAnnotationModel;
				}
				annotationModel.addValue(key, annotationModels);
			}			
			else if(value instanceof String[]){
				annotationModel.addValue(key, value);
			}			
			else{
				if(value==null){
					value = "null";
				}
				else if(value instanceof CtLiteral<?>){
					value = ((CtLiteral<?>)value).getValue().toString();
				}
				else if(value instanceof CtFieldReference<?>){
					value = ((CtFieldReference<?>)value).getActualField().getName();
				}
				else if(value instanceof CtNewArray<?>){
					List<?> elements = ((CtNewArray<?>) value).getElements();
					int size = elements.size();
					String[] arr = new String[size];
					for(int i = 0 ; i < size ; i++){
						arr[i] = elements.get(i).toString();
					}
					value = arr;
				}
				annotationModel.addValue(key, value);
			}
		}
		
		return annotationModel;
	}


	private ArrayList<CtAnnotation<?>> toCtAnnotationList(Object value) {
		
		if(value==null){
			return null;
		}
		
		ArrayList<CtAnnotation<?>> list = new ArrayList<CtAnnotation<?>>();
		if(value instanceof CtAnnotation<?>){			
			list.add((CtAnnotation<?>) value);
			
		}		
		
		if(value.getClass().isArray()){
			Class<?> componentType = value.getClass().getComponentType();
			if(checkIfCtAnnotation(componentType)){
				int l = Array.getLength(value);
				for(int i = 0 ; i < l ; i++){
					CtAnnotation<?> subAnnotation = (CtAnnotation<?>) Array.get(value, i);
					list.add(subAnnotation);
				}
			}
		}
		if(value instanceof Collection){
			Collection<?> col = (Collection<?>) value;
			for(Object member : col){
				if(member instanceof CtAnnotation<?>){
					list.add((CtAnnotation<?>) member);
				}
			}
			
		}
		if(list.isEmpty()){
			return null;
		}
		return list;
	}

	private boolean checkIfCtAnnotation(Class<?> componentType) {
		
		for(Class<?> clazz = componentType; clazz != null ; clazz = clazz.getSuperclass()){
			
			Class<?>[] interfaces = clazz.getInterfaces();
			for(Class<?> iClass : interfaces){
				if(iClass.equals(CtAnnotation.class)){
					return true;
				}
			}
		}
		return false;
	}

	
	private IAnnotationModel processJavaLangAnnotation(Annotation annotation) {
		
		return new com.mulesoft.jaxrs.raml.annotation.model.reflection.AnnotationModel(annotation);
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
		ITypeModel existingType = registry.getType(qualifiedName);
		if(existingType != null){
			return new ProxyType(registry, qualifiedName);
		}
		
		CtClass<Object> ctType = factory.Class().get(qualifiedName);
		if(ctType!=null){
			return processType(ctType);
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
		return new ProxyType(registry, qualifiedName);
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
		parameterModel.setName(paramTypeReference.getSimpleName());
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
