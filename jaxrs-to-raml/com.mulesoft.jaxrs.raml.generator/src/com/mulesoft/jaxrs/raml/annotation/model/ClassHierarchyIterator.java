package com.mulesoft.jaxrs.raml.annotation.model;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class ClassHierarchyIterator {
	
	public void iterate(ITypeModel type, IMethodModel method){
		
		HashSet<String> iSet = new HashSet<String>();
		ArrayList<ITypeModel> iList = new ArrayList<ITypeModel>();
		for(ITypeModel t = type; t!=null ; t = t.getSuperClass()){
			
			if(processType(t,method)){
				return;
			}
			ITypeModel[] typeInterfaces = t.getImplementedInterfaces();
			for(ITypeModel iType : typeInterfaces){
				String name = iType.getFullyQualifiedName();
				if(!iSet.contains(name)){
					iSet.add(name);
					iList.add(iType);
				}
			}
		}
		for(int i = 0 ; i < iList.size() ; i++){
			ITypeModel t = iList.get(i);
			if(processType(t,method)){
				return;
			}
			ITypeModel[] typeInterfaces = t.getImplementedInterfaces();
			for(ITypeModel iType : typeInterfaces){
				String name = iType.getFullyQualifiedName();
				if(!iSet.contains(name)){
					iSet.add(name);
					iList.add(iType);
				}
			}
		}
	}
	
	boolean processType(ITypeModel type,IMethodModel method){
		
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

	abstract boolean checkMethod(IMethodModel m);

}
