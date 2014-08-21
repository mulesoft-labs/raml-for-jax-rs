package org.raml.emitter;

import org.raml.model.ParamType;
import org.raml.model.parameter.UriParameter;

public class UrlParameterFilter implements IFilter<UriParameter>{

	@Override
	public boolean accept(UriParameter element) {
		if (element.getType()==ParamType.STRING){
			if (element.getDescription()==null||element.getDescription().trim().length()==0){
				if (element.getEnumeration()==null||element.getEnumeration().isEmpty()){
					if (element.getPattern()==null||element.getPattern().isEmpty()){
						return false;
					}
				}
			}
		}
		return true;
	}
}
