package com.mulesoft.jaxrs.raml.jaxb;

import org.raml.schema.model.ISchemaProperty;
import org.raml.schema.model.ISchemaType;

import com.mulesoft.jaxrs.raml.annotation.model.IResourceVisitorExtension;

public interface ISchemaModelBuilderExtension extends IResourceVisitorExtension {
	
	/**
	 * The method allows to adjust property right after it and its type have been created by the
	 * SchemaModelBuilder.
	 * @param prop Property created by the SchemaModelBuilder. May be null.
	 * @return Adjusted or created property. By default must return @prop, not null.
	 */
	ISchemaProperty processProperty(JAXBProperty jaxbProp, ISchemaProperty prop);
	
	/**
	 * Adjust type right after it and all its properties have been created by the SchemaModelBuilder
	 * The method is called once for each type.
	 * @param type Type created by the SchemaModelBuilder
	 */
	void processType(ISchemaType type);
	
	/**
	 * Adjust schema type right after it and all its properties have been created by the SchemaModelBuilder
	 * The method is called only for root type.
	 * @param type Root type created by the SchemaModelBuilder
	 */
	void processModel(ISchemaType type);

}
