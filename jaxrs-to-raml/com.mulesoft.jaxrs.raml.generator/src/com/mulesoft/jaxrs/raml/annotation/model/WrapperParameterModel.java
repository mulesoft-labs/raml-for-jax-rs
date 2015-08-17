package com.mulesoft.jaxrs.raml.annotation.model;

public class WrapperParameterModel implements IParameterModel {
	
	public WrapperParameterModel(ITypeModel ownerType,
			IMethodModel ownerMethod, IParameterModel originalParameter) {
		super();
		this.ownerType = ownerType;
		this.ownerMethod = ownerMethod;
		this.originalParameter = originalParameter;
		
		IParameterModel[] params = ownerMethod.getParameters();		
		for(int i = 0 ; i < params.length ; i++){
			if(params[i].getName().equals(originalParameter.getName())){
				this.index = i;
				break;
			}
		}
	}

	private ITypeModel ownerType;
	
	private IMethodModel ownerMethod;
	
	private IParameterModel originalParameter;
	
	private IParameterModel actualParameter;
	
	private int index;

	@Override
	public String getName() {
		return originalParameter.getName();
	}

	@Override
	public String getDocumentation() {
		return originalParameter.getDocumentation();
	}

	@Override
	public IAnnotationModel[] getAnnotations() {
		return originalParameter.getAnnotations();
	}

	@Override
	public String getAnnotationValue(String annotation) {
		return this.getActualParameter().getAnnotationValue(annotation);
	}

	@Override
	public String[] getAnnotationValues(String annotation) {
		return this.getActualParameter().getAnnotationValues(annotation);
	}

	@Override
	public boolean hasAnnotation(String name) {
		return this.getActualParameter().hasAnnotation(name);
	}

	@Override
	public IAnnotationModel getAnnotation(String name) {
		return this.getActualParameter().getAnnotation(name);
	}
	
	@Override
	public boolean hasAnnotationWithCanonicalName(String name) {
		return this.getActualParameter().hasAnnotationWithCanonicalName(name);
	}

	@Override
	public IAnnotationModel getAnnotationByCanonicalName(String name) {
		return this.getActualParameter().getAnnotationByCanonicalName(name);
	}

	@Override
	public String getParameterType() {
		return originalParameter.getParameterType();
	}

	@Override
	public boolean required() {
		return originalParameter.required();
	}

	private IParameterModel getActualParameter() {
		if(this.actualParameter!=null){
			return this.actualParameter;
		}
		new ClassHierarchyVisitor() {
			@Override
			boolean checkMethod(IMethodModel m) {
				
				IParameterModel param = m.getParameters()[index];
				if(param.hasAnnotation("HeaderParam")){
					WrapperParameterModel.this.actualParameter = param;
					return true;
				}
				if(param.hasAnnotation("FormParam")){
					WrapperParameterModel.this.actualParameter = param;
					return true;
				}
				if(param.hasAnnotation("QueryParam")){
					WrapperParameterModel.this.actualParameter = param;
					return true;
				}
				if(param.hasAnnotation("PathParam")){
					WrapperParameterModel.this.actualParameter = param;
					return true;
				}
				return false;
			}
		}.visit(ownerType, this.ownerMethod);
		if(this.actualParameter!=null){
			return this.actualParameter;
		}
		return this.originalParameter;
	}
}
