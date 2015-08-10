package com.mulesoft.jaxrs.raml.annotation.model;

public interface IResourceVisitorExtension {
	
	/**
	 * Decide if the type needs schema to be generated
	 * @param type
	 * @return Whether to generate schema or not
	 */
	boolean generateSchema(ITypeModel type);

}
