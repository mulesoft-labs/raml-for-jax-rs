package org.raml.emitter;

import org.raml.model.ParamType;
import org.raml.model.parameter.UriParameter;

/**
 * <p>UrlParameterFilter class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class UrlParameterFilter implements IFilter<UriParameter>{

	
	/**
	 * <p>accept.</p>
	 *
	 * @param element a {@link org.raml.model.parameter.UriParameter} object.
	 * @return a boolean.
	 */
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
