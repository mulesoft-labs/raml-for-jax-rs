package org.raml.jaxrs.codegen.model;

import com.mulesoft.jaxrs.raml.annotation.model.IFieldModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

/**
 * <p>FieldModel class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class FieldModel extends BasicModel implements IFieldModel{

	/** {@inheritDoc} */
	@Override
	public ITypeModel getType() {
		throw new UnsupportedOperationException();
	}
	
	protected boolean isGeneric;

	public boolean isGeneric() {
		return isGeneric;
	}

	public void setGeneric(boolean isGeneric) {
		this.isGeneric = isGeneric;
	}

}
