package com.mulesoft.jaxrs.raml.jaxb;

import java.util.HashMap;

import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

public class JAXBRegistry {

	protected HashMap<ITypeModel, JAXBType>types=new HashMap<ITypeModel, JAXBType>();
	
	public JAXBType getJAXBModel(ITypeModel tp){
		if (tp==null){
			return null;
		}
		if (types.containsKey(tp)){
			return types.get(tp);
		}
		JAXBType type=new JAXBType(tp,this);
		types.put(tp, type);
		
		return type;		
	}
}
