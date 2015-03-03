package org.raml.jaxrs.codegen.model;

import com.mulesoft.jaxrs.raml.annotation.model.IFieldModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

public class FieldModel extends BasicModel implements IFieldModel{

	@Override
	public ITypeModel getType() {
		throw new UnsupportedOperationException();
	}

}
