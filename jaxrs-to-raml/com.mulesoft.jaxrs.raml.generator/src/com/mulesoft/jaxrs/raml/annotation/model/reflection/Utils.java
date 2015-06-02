package com.mulesoft.jaxrs.raml.annotation.model.reflection;

import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

public class Utils {
	
private static final String XML_ROOT_ELEMENT = "XmlRootElement"; //$NON-NLS-1$
	
	private static final String XML_TYPE = "XmlType"; //$NON-NLS-1$
	
	private static final String XML_ACCESSOR_TYPE = "XmlAccessorType"; //$NON-NLS-1$
	
	private static final String XML_ACCESSOR_ORDER = "XmlAccessorOrder"; //$NON-NLS-1$
	
	public static boolean isJAXBType(ITypeModel type){
		
		for(String aName: new String[]{XML_ROOT_ELEMENT, XML_TYPE, XML_ACCESSOR_TYPE, XML_ACCESSOR_ORDER}){
			if(type.getAnnotation(aName)!=null){
				return true;
			}
		}
		return false;
	}
	
	public static boolean isCollection(Class<?> clazz){
		
		if(clazz.getCanonicalName().equals("java.util.Collection")){
			return true;
		}
		
		Class<?> cl = clazz;
		while(cl!=null){
			for(Class<?> iCl : cl.getInterfaces()){
				if(isCollection(iCl)){
					return true;
				}
			}
			cl = cl.getSuperclass();			
		}		
		return false;		
	}

}
