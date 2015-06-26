package com.mulesoft.jaxrs.raml.jaxb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

/**
 * <p>JAXBRegistry class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class JAXBRegistry {

	protected HashMap<ITypeModel, JAXBType>types=new HashMap<ITypeModel, JAXBType>();
	
	/**
	 * <p>getJAXBModel.</p>
	 *
	 * @param tp a {@link com.mulesoft.jaxrs.raml.annotation.model.ITypeModel} object.
	 * @return a {@link com.mulesoft.jaxrs.raml.jaxb.JAXBType} object.
	 */
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
	
	public List<JAXBType> getJAXBModels(Collection<ITypeModel> list){		
		if (list==null){
			return null;
		}
		ArrayList<JAXBType> result = new ArrayList<JAXBType>();
		for(ITypeModel tp: list){
			JAXBType jaxbModel = getJAXBModel(tp);
			result.add(jaxbModel);
		}
		return result;		
	}
}
