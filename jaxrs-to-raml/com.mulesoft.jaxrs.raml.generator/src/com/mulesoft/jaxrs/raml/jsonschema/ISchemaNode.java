package com.mulesoft.jaxrs.raml.jsonschema;

import java.util.Collection;

/**
 * <p>ISchemaNode interface.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public interface ISchemaNode {
	
	/**
	 * <p>getChildren.</p>
	 *
	 * @return a {@link java.util.Collection} object.
	 */
	Collection<? extends ISchemaNode> getChildren();

}
