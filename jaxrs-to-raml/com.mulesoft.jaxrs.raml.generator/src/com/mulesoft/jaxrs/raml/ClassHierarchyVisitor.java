package com.mulesoft.jaxrs.raml;

import java.util.ArrayList;
import java.util.HashSet;

import org.aml.typesystem.IMethodModel;
import org.aml.typesystem.IParameterModel;
import org.aml.typesystem.ITypeModel;

public abstract class ClassHierarchyVisitor {
	
	public void visit(ITypeModel type, IMethodModel method){
		
		HashSet<String> iSet = new HashSet<String>();
		ArrayList<ITypeModel> iList = new ArrayList<ITypeModel>();
		for(ITypeModel t = type; t!=null ; t = t.getSuperClass()){
			
			if(method!=null?processType(t,method):processType(t)){
				return;
			}
			if(visitInterfaces()){
				getInterfaces(iSet, iList, t);
			}
		}
		for(int i = 0 ; i < iList.size() ; i++){
			ITypeModel t = iList.get(i);
			if(method!=null?processType(t,method):processType(t)){
				return;
			}
			getInterfaces(iSet, iList, t);
		}
	}

	private void getInterfaces(HashSet<String> iSet, ArrayList<ITypeModel> iList, ITypeModel t) {
		ITypeModel[] typeInterfaces = t.getImplementedInterfaces();
		if(typeInterfaces!=null){
			for(ITypeModel iType : typeInterfaces){
				String name = iType.getFullyQualifiedName();
				if(!iSet.contains(name)){
					iSet.add(name);
					iList.add(iType);
				}
			}
		}
	}

	protected boolean processType(ITypeModel type,IMethodModel method){
		
		IMethodModel[] methods = type.getMethods();
		String methodName = method.getName();
		IParameterModel[] methodParams = method.getParameters();
		int paramsCount = methodParams.length;
		for(IMethodModel m : methods){
			if(m.getName()==methodName){
				IParameterModel[] params = m.getParameters();
				if(params.length==paramsCount){
					boolean match = true;
					for(int i = 0 ; i < paramsCount; i++){
						if(!params[i].getParameterType().equals(methodParams[i].getParameterType())){
							match=false;
							break;
						}
					}
					if(match){
						return checkMethod(m);
					}
				}
			}
		}
		return false;
	}
	
	protected boolean processType(ITypeModel type){
		
		IMethodModel[] methods = type.getMethods();
		for(IMethodModel m : methods){
			if(checkMethod(m)){
				return true;				
			}
		}
		return false;
	}
	
	protected boolean visitInterfaces(){
		return true;
	}

	abstract boolean checkMethod(IMethodModel m);

}
