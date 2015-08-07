package com.mulesoft.jaxrs.raml.jaxb;

import org.raml.schema.model.ISchemaProperty;
import org.raml.schema.model.ISchemaType;

import com.mulesoft.jaxrs.raml.annotation.model.IResourceVisitorExtension;

public interface ISchemaModelBuilderExtension extends IResourceVisitorExtension {
	
	/**
	 * Adjust property right after it and its type have been created by the SchemaModelBuilder.If prop is null
	 * @param prop adjusted or created property property. By
	 */
	ISchemaProperty processProperty(JAXBProperty jaxbProp, ISchemaProperty prop);
	
	/**
	 * Adjust type right after it and all its properties have been created by the SchemaModelBuilder
	 * @param type type
	 */
	void processType(ISchemaType type);
	
	/**
	 * Adjust schema type right after it and all its properties have been created by the SchemaModelBuilder
	 * @param type type
	 */
	void processModel(ISchemaType type);

}
