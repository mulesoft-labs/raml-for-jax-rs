package org.raml.jaxrs.codegen.model;

import org.aml.typesystem.IFieldModel;
import org.aml.typesystem.ITypeModel;
import org.aml.typesystem.reflection.ReflectionType;

/**
 * <p>FieldModel class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class FieldModel extends BasicModel implements IFieldModel{
	
	protected boolean isGeneric;

	public boolean isGeneric() {
		return isGeneric;
	}

	public void setGeneric(boolean isGeneric) {
		this.isGeneric = isGeneric;
	}



}
