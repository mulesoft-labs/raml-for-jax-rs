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
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.raml.jaxrs.codegen.maven.ProxyType;
import org.raml.jaxrs.codegen.maven.TypeModelRegistry;
import org.raml.jaxrs.codegen.model.AnnotationModel;
import org.raml.jaxrs.codegen.model.BasicModel;
import org.raml.jaxrs.codegen.model.FieldModel;
import org.raml.jaxrs.codegen.model.GenericElementModel;
import org.raml.jaxrs.codegen.model.MethodModel;
import org.raml.jaxrs.codegen.model.ParameterModel;
import org.raml.jaxrs.codegen.model.TypeModel;
import org.raml.jaxrs.codegen.model.TypeParameterModel;

import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;
import com.mulesoft.jaxrs.raml.annotation.model.IFieldModel;
import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.IParameterModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeParameter;

import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtGenericElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.eval.PartialEvaluator;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;

/**
 * <p>SpoonProcessor class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class SpoonProcessor{
	
	private static final String API_OPERATION = "ApiOperation";

	private static final String SWAGGER_API = "Api";

	private static final String JAVAX_XML_TYPE = "XmlType";

	private static final String JAVAX_CONSUMES = "Consumes";

	private TypeModelRegistry registry = new TypeModelRegistry();
	
	private Factory factory;
	
	/**
	 * <p>Constructor for SpoonProcessor.</p>
	 *
	 * @param factory a {@link spoon.reflect.factory.Factory} object.
	 */
	public SpoonProcessor(Factory factory) {
		this.factory = factory;
	}

	/**
	 * <p>process.</p>
	 *
	 * @param packages a {@link java.util.Collection} object.
	 */
	public void process(Collection<CtPackage> packages){
		if(packages==null){
			return;
		}
		for(CtPackage package_ : packages ){
			processPackage(package_); 
		}

		for(ITypeModel type : registry.getTypes()){
			
			boolean hasGlobalConsumes = hasGlobalConsumes(type);			
			for(IMethodModel method : type.getMethods()){
				adjustReturnedAndBodyType(method,hasGlobalConsumes);
			}
		}
	}

	private boolean hasGlobalConsumes(ITypeModel type) {
		
		if(type.hasAnnotation(JAVAX_CONSUMES)){
			return true;
		}
		IAnnotationModel apiAnn = type.getAnnotation(SWAGGER_API);
		if(apiAnn==null){
			return false;
		}
		String consumes = apiAnn.getValue(JAVAX_CONSUMES.toLowerCase());
		if(consumes!=null){
			return true;
		}
		return false;
	}

	private void adjustReturnedAndBodyType(IMethodModel method_, boolean hasGlobalConsumes) {
		
		if(!(method_ instanceof MethodModel)){
			return;
		}		
		MethodModel method = (MethodModel) method_;
		
		ITypeModel returnedType = method.getReturnedType();
		if(returnedType!=null){
			if(returnedType instanceof ProxyType){
				ITypeModel rt = registry.getType(returnedType.getFullyQualifiedName());				
				method.setReturnedType(rt);
			}
		}
		
		boolean hasConsumes = hasGlobalConsumes;
		
		IAnnotationModel apiOperation = method.getAnnotation(API_OPERATION);
		if(apiOperation!=null){
			IAnnotationModel[] subAnn = apiOperation.getSubAnnotations(JAVAX_CONSUMES.toLowerCase());
			if(subAnn!=null){
				hasConsumes = true;
			}
		}		
		
		IAnnotationModel consumes = method.getAnnotation(JAVAX_CONSUMES);
		if(consumes!=null){
			hasConsumes = true;
		}
		if(!hasConsumes){
			return;
		}
		
		IParameterModel[] parameters = method.getParameters();
		for(IParameterModel param_ : parameters){
			
			String paramType = param_.getParameterType();
//			if(paramType.startsWith("java.")){
//				continue;
//			}
			if(isPrimitive(paramType)){
				continue;
			}
			if(param_.hasAnnotation("QueryParam")){
				continue;
			}
			if(param_.hasAnnotation("HeaderParam")){
				continue;
			}
			if(param_.hasAnnotation("PathParam")){
				continue;
			}
			if(param_.hasAnnotation("FormParam")){
				continue;
			}
			if(param_.hasAnnotation("Context")){
				continue;
			}
			
			ITypeModel type = registry.getType(paramType);
			if(type==null){
				continue;
			}
//			IAnnotationModel typeAnnotation = type.getAnnotation(JAVAX_XML_TYPE);
//			if(typeAnnotation==null){
//				continue;
//			}
			method.setBodyType(type);
			if(registry.isTargetType(paramType)){
				break;
			}
		}
		
	}
	
	private static HashSet<String> primitives = new HashSet<String>(Arrays.asList(
			"byte", "java.lang.Byte",
			"short", "java.lang.Short",
			"int", "java.lang.Integer",
			"long", "java.lang.Long",
			"float", "java.lang.Float",
			"double", "java.lang.Double",
			"character", "java.lang.Character",
			"boolean", "java.lang.Boolean"
		));

	private boolean isPrimitive(String qName) {		
		return primitives.contains(qName);
	}

	private void processPackage(CtPackage package_) {
		Set<CtPackage> subPackages = package_.getPackages();
		if(subPackages!=null){
			for(CtPackage subPackage:subPackages){
				processPackage(subPackage);
			}
		}
		for( CtType<?> type : package_.getTypes()){
			process(type);
		}
	}
	
	/**
	 * <p>process.</p>
	 *
	 * @param classElement a {@link spoon.reflect.declaration.CtType} object.
	 */
	public void process(CtType<?> classElement) {
		
		ITypeModel type = processType(classElement);
		registry.registerTargetType(type);
	}

	private ITypeModel processType(CtType<?> classElement)
	{
		String qualifiedName = classElement.getQualifiedName();
		TypeModel type = (TypeModel) registry.getType(qualifiedName);
		if(type==null){
			type = new TypeModel(registry);
			type.setFullyQualifiedName(qualifiedName);
		}
		registry.registerType(type);
		
		fillBasic(type,classElement);
		
		fillTypeParameters(type,classElement);
		
		if(classElement instanceof CtType){
			CtTypeReference<?> superClass = classElement.getSuperclass();
			if(superClass!=null){
				ITypeModel superType = processTypeReference(superClass);
				type.setSuperClass(superType);
			}
			Set<CtTypeReference<?>> interfaces = classElement.getSuperInterfaces();
			if(interfaces!=null){
				ArrayList<ITypeModel> list = new ArrayList<ITypeModel>();
				for(CtTypeReference<?> ref: interfaces){
					ITypeModel tm = this.processTypeReference(ref);
					if(tm!=null){
						list.add(tm);
					}
				}
				type.setImplementedInterfaces(list.toArray(new ITypeModel[list.size()]));
			}
			Set<CtMethod<?>> methods = ((CtType<?>)classElement).getMethods();
			for(CtMethod<?> m : methods){
				IMethodModel methodModel = processMethod(m,type);
				type.addMethod(methodModel);
			}
			Collection<CtField<?>> fields = ((CtType<?>)classElement).getFields();
			for(CtField<?> m : fields){
				IFieldModel methodModel = processField(m,type);
				type.addField(methodModel);
			}
		}
		return type;
	}


	private void fillTypeParameters(GenericElementModel model, CtGenericElement element) {
		List<CtTypeReference<?>> ftp = element.getFormalTypeParameters();
		if(ftp==null||ftp.isEmpty()){
			return;
		}
		for(CtTypeReference<?> param : ftp){
			String name = param.getSimpleName();
			TypeParameterModel paramModel = new TypeParameterModel();
			paramModel.setName(name);
			model.getTypeParameters().add(paramModel);
		}
	}

	private IAnnotationModel processAnnotation(CtAnnotation<? extends Annotation> annotation) {
		
		String simpleName = annotation.getActualAnnotation().annotationType().getSimpleName();
		String qualifiedName = annotation.getActualAnnotation().annotationType().getCanonicalName();
		
		AnnotationModel annotationModel = new AnnotationModel();		
		annotationModel.setName(simpleName);
		annotationModel.setFullyQualifiedName(qualifiedName);
		
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
				if(value instanceof CtNewArray<?>){
					List<?> elements = ((CtNewArray<?>) value).getElements();
					int size = elements.size();
					Object[] arr = new Object[size];
					for(int i = 0 ; i < size ; i++){
						Object elem = elements.get(i);
						if(elem instanceof CtCodeElement){
							PartialEvaluator eval = factory.Eval().createPartialEvaluator();
							arr[i] = eval.evaluate(null, (CtCodeElement) elem);
						}
						else{
							arr[i] = elem;
						}
					}
					value = arr;
				}				
				if(value instanceof CtCodeElement){
					PartialEvaluator eval = factory.Eval().createPartialEvaluator();
					value = eval.evaluate(null, (CtCodeElement) value);
				}				
				
				if(value instanceof CtLiteral<?>){
					value = ((CtLiteral<?>)value).getValue().toString();
				}
				else if(value instanceof CtFieldReference<?>){
					Member member = ((CtFieldReference<?>)value).getActualField();

					//if field references a static final String, use string's value
					if (member instanceof Field) {
						Field field = (Field) member;

						int mod = field.getModifiers();
						if (Modifier.isStatic(mod) && Modifier.isFinal(mod)) {
							field.setAccessible(true);
							try {
								value = field.get(null).toString();
							} catch (Throwable t) {		
								value=member.getName();
								//NOOP tolerate any exception/error, and fall back to using name of field below
							}
						}
						//fall back to using name of field reference
						else{
						value=member.getName();
						}
					}
					
				}
				else if(value.getClass().isArray()){
					int length = Array.getLength(value);
					String[] arr = new String[length];
					for(int i = 0 ; i < length ; i++){
						
						Object elem = Array.get(value, i);						
						String sVal = elem.toString();
						if(elem instanceof CtLiteral<?>){
							sVal = ((CtLiteral<?>)elem).getValue().toString();
						}
						else if(elem instanceof CtFieldReference<?>){
							sVal = ((CtFieldReference<?>)elem).getActualField().getName();
						}
						arr[i] = sVal;
					}
					value = arr;
				}
				else{
					value = value.toString();
				}
				if(value==null){
					value = "null";
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
		
		if(value instanceof CtNewArray){
			value = ((CtNewArray<?>)value).getElements();
		}
		
		ArrayList<CtAnnotation<?>> list = new ArrayList<CtAnnotation<?>>();
		if(value instanceof CtAnnotation<?>){			
			list.add((CtAnnotation<?>) value);
			
		}
		else if(value.getClass().isArray()){
			Class<?> componentType = value.getClass().getComponentType();
			if(checkIfCtAnnotation(componentType)){
				int l = Array.getLength(value);
				for(int i = 0 ; i < l ; i++){
					CtAnnotation<?> subAnnotation = (CtAnnotation<?>) Array.get(value, i);
					list.add(subAnnotation);
				}
			}
		}else if(value instanceof Collection){
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

	
	private IMethodModel processMethod(CtMethod<?> m, TypeModel ownerType) {
		
		MethodModel methodModel = new MethodModel();
		fillBasic(methodModel, m);
		fillTypeParameters(methodModel,m);
		CtTypeReference<?> returnedType = m.getType();
		ITypeModel returnedTypeModel = processTypeReference(returnedType);
		methodModel.setReturnedType(returnedTypeModel);
		fillJAXBType(methodModel,returnedType);
		
		List<CtParameter<?>> parameters = m.getParameters();
		for(CtParameter<?> p : parameters){
			IParameterModel parameterModel = processParameter(p);
			methodModel.addParameter(parameterModel);
		}
		String returnedTypeSimpleName = returnedType.getSimpleName();
		String returnedTypeQualifiedname = returnedType.getQualifiedName();
		if(returnedTypeSimpleName.equalsIgnoreCase(returnedTypeQualifiedname)){
			for(ITypeParameter tp : ownerType.getTypeParameters()){
				if(returnedType.getSimpleName().equals(tp.getName())){
					methodModel.setHasGenericReturnType(true);
				}
			}
			for(ITypeParameter tp : methodModel.getTypeParameters()){
				if(returnedType.getSimpleName().equals(tp.getName())){
					methodModel.setHasGenericReturnType(true);
				}
			}
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
		fillJAXBType(parameterModel,paramType);
		processTypeReference(paramType);
		
		return parameterModel;
	}

	private void fillBasic(BasicModel model, CtNamedElement namedElement) {
		
		String simpleName = namedElement.getSimpleName();
		String docComment = namedElement.getDocComment();
		
		model.setName(simpleName);
		model.setDocumentation(docComment);
		if (namedElement instanceof CtModifiable) {
			CtModifiable ctModifiable = (CtModifiable) namedElement;
			Set<ModifierKind> modifiers = ctModifiable.getModifiers();
			for (ModifierKind mod : modifiers) {
				if (mod == ModifierKind.STATIC) {
					model.setStatic(true);
				}
				if (mod == ModifierKind.PUBLIC) {
					model.setPublic(true);
				}
			}
		}

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
		
		CtClass<Object> ctClass = factory.Class().get(qualifiedName);
		if(ctClass!=null){
			return processType(ctClass);
		}
		
		CtType<Object> ctType = factory.Type().get(qualifiedName);
		if(ctType!=null){
			return processType(ctType);
		}

		TypeModel type = new TypeModel(registry);
		type.setFullyQualifiedName(qualifiedName);
		registry.registerType(type);
		
		fillReference(type, typeReference);
		
		Collection<CtExecutableReference<?>> methods = typeReference.getDeclaredExecutables();
		for(CtExecutableReference<?> m : methods){
			IMethodModel methodModel = processMethodReference(m);
			type.addMethod(methodModel);
		}
		Collection<CtFieldReference<?>> fields = typeReference.getDeclaredFields();
		for(CtFieldReference<?> m : fields){
			IFieldModel methodModel = processFieldReference(m);
			type.addField(methodModel);
		}
		return new ProxyType(registry, qualifiedName);
	}
	
	private IFieldModel processFieldReference(CtFieldReference<?> m) {
		FieldModel fm=new FieldModel();
		fillReference(fm, m);
		Member actualField = m.getActualField();
		if(actualField!=null){
			adjustModifiers(fm, actualField);
		}
		return fm;
	}
	private IFieldModel processField(CtField<?> m, TypeModel ownerType) {
		FieldModel fm=new FieldModel();
		fillBasic(fm, m);
		if (m.getType() != null) {
			CtTypeReference<?> type = m.getType();
			fillJAXBType(fm,type);

			String typeSimpleName = type.getSimpleName();
			String typeQualifiedName = type.getQualifiedName();
			if(typeSimpleName.equalsIgnoreCase(typeQualifiedName)){
				for(ITypeParameter tp : ownerType.getTypeParameters()){
					if(typeSimpleName.equals(tp.getName())){
						fm.setGeneric(true);
					}
				}
			}
		}
		return fm;
	}

	private void fillJAXBType(BasicModel fm, CtTypeReference<?> type) {
		
		if(type==null){
			return;
		}
		List<CtTypeReference<?>> actualTypes = new ArrayList<CtTypeReference<?>>();
		Class<?> actualClass = type.getActualClass();
		fm.setJavaClass(actualClass);
		if (actualClass!=null&&Collection.class.isAssignableFrom(actualClass)){
			fm.setCollection(true);
			List<CtTypeReference<?>> actualTypeArguments = type.getActualTypeArguments();			
			if (actualTypeArguments.size()>0){
				actualTypes.add(actualTypeArguments.get(0));
			}
		}
		else if (actualClass!=null&&Map.class.isAssignableFrom(actualClass)){
			fm.setMap(true);
			List<CtTypeReference<?>> actualTypeArguments = type.getActualTypeArguments();
			if (actualTypeArguments.size()==2){
				actualTypes = actualTypeArguments;				
			}
		}
		else{
			actualTypes.add(type);
		}
		for(CtTypeReference<?> t : actualTypes){
			ITypeModel processTypeReference = processTypeReference(t);
			fm.addJaxbType(processTypeReference);
		}
	}

	private IMethodModel processMethodReference(CtExecutableReference<?> methodElement) {
		
		MethodModel methodModel = new MethodModel();
		fillReference(methodModel,methodElement);
		List<CtTypeReference<?>> parameters = methodElement.getParameters();
		Method actualMethod = methodElement.getActualMethod();
		if(actualMethod!=null){
			String canonicalName = actualMethod.getReturnType().getCanonicalName();
			ITypeModel existingType = registry.getType(canonicalName);
			if(existingType!=null){
				methodModel.setReturnedType(existingType);
			}
			if(existingType != null){
				methodModel.setReturnedType(new ProxyType(registry, canonicalName));
			}
			adjustModifiers(methodModel, actualMethod);
		}
		for(CtTypeReference<?> p : parameters){
			IParameterModel parameterModel = processParameterReference(p);
			methodModel.addParameter(parameterModel);
		}
		return methodModel;
	}

	private void adjustModifiers(BasicModel model, Member member) {
		int modifiers = member.getModifiers();
		if(Modifier.isPublic(modifiers)){
			model.setPublic(true);
		}
		if(Modifier.isStatic(modifiers)){
			model.setStatic(true);
		}
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
		
		List<Annotation> annotations = null;
		try{
			annotations = ref.getAnnotations();
		}
		catch(Exception e){
		}
		
		if(annotations!=null){
			for(Annotation a : annotations){
				IAnnotationModel annotationModel = processJavaLangAnnotation(a);
				model.addAnnotation(annotationModel);
			}
		}
	}

	/**
	 * <p>Getter for the field <code>registry</code>.</p>
	 *
	 * @return a {@link org.raml.jaxrs.codegen.maven.TypeModelRegistry} object.
	 */
	public TypeModelRegistry getRegistry() {
		return registry;
	}
}
