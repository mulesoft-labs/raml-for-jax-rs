package com.mulesoft.jaxrs.raml.annotation.model;

import java.util.List;

import org.raml.model.ActionType;

public class WrapperMethodModel implements IMethodModel{
	
	public WrapperMethodModel(ITypeModel ownerType, IMethodModel originalMethod) {
		super();
		this.ownerType = ownerType;
		this.originalMethod = originalMethod;
	}

	private ITypeModel ownerType;
	
	private IMethodModel originalMethod;
	
	private IMethodModel actualMethod;

	@Override
	public String getName() {
		return originalMethod.getName();
	}

	@Override
	public String getDocumentation() {
		return originalMethod.getDocumentation();
	}

	@Override
	public IAnnotationModel[] getAnnotations() {
		return originalMethod.getAnnotations();
	}

	@Override
	public String getAnnotationValue(String annotation) {
		return this.getActualMethod().getAnnotationValue(annotation);
	}

	@Override
	public String[] getAnnotationValues(String annotation) {
		return this.getActualMethod().getAnnotationValues(annotation);
	}

	@Override
	public boolean hasAnnotation(String name) {
		return this.getActualMethod().hasAnnotation(name);
	}

	@Override
	public IAnnotationModel getAnnotation(String name) {
		return this.getActualMethod().getAnnotation(name);
	}
	
	@Override
	public boolean hasAnnotationWithCanonicalName(String name) {
		return this.getActualMethod().hasAnnotationWithCanonicalName(name);
	}

	@Override
	public IAnnotationModel getAnnotationByCanonicalName(String name) {
		return this.getActualMethod().getAnnotationByCanonicalName(name);
	}

	@Override
	public ITypeModel getType() {
		return originalMethod.getType();
	}

	@Override
	public List<ITypeModel> getJAXBTypes() {
		return originalMethod.getJAXBTypes();
	}

	@Override
	public Class<?> getJavaType() {
		return originalMethod.getJavaType();
	}

	@Override
	public boolean isCollection() {
		return originalMethod.isCollection();
	}

	@Override
	public boolean isMap() {
		return originalMethod.isMap();
	}

	@Override
	public List<ITypeParameter> getTypeParameters() {
		return originalMethod.getTypeParameters();
	}

	@Override
	public IParameterModel[] getParameters() {
		
		IParameterModel[] originalParams = this.originalMethod.getParameters();
		IParameterModel[] arr = new IParameterModel[originalParams.length];
		for(int i = 0 ; i < originalParams.length; i++){
			arr[i] = new WrapperParameterModel(ownerType, originalMethod, originalParams[i]);
		}
		return arr;
	}

	@Override
	public IDocInfo getBasicDocInfo() {
		return originalMethod.getBasicDocInfo();
	}

	@Override
	public ITypeModel getReturnedType() {
		return originalMethod.getReturnedType();
	}

	@Override
	public ITypeModel getBodyType() {
		return originalMethod.getBodyType();
	}

	@Override
	public boolean isStatic() {
		return originalMethod.isStatic();
	}

	@Override
	public boolean isPublic() {
		return originalMethod.isPublic();
	}

	@Override
	public boolean hasGenericReturnType() {
		return originalMethod.hasGenericReturnType();
	}
	
	private IMethodModel getActualMethod(){
		
		if(this.actualMethod!=null){
			return this.actualMethod;
		}		
		new ClassHierarchyVisitor() {
			@Override
			boolean checkMethod(IMethodModel m) {
				boolean isWS = m.hasAnnotation("Path");
				if(!isWS){
					for(ActionType at : ActionType.values()){
						isWS = m.hasAnnotation(at.name());
						WrapperMethodModel.this.actualMethod = m;
						if(isWS){
							break;
						}
					}
				}				
				return isWS;
			}
		}.visit(ownerType, originalMethod);
		if(this.actualMethod!=null){
			return this.actualMethod;
		}
		return this.originalMethod;
	}
}
