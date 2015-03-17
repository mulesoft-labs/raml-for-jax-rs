package com.mulesoft.jaxrs.raml.jaxb;

import java.util.HashMap;

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
}
