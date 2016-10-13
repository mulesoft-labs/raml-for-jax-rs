package com.mulesoft.jaxrs.raml.schemas;

import java.util.List;

import org.aml.typesystem.IAnnotationModel;

import com.mulesoft.jaxrs.raml.StructureType;

public class MapPropertyImpl extends PropertyModelImpl implements IMapSchemaProperty {

	public MapPropertyImpl(
			String name,
			List<ISchemaType> types,
			boolean required,
			boolean isAttribute,
			String namespace,
			List<IAnnotationModel> annotations) {
		super(name, null, required, isAttribute, StructureType.MAP, namespace,annotations);
		if(types!=null){
			if(types.size()>0){
				this.keyType = types.get(0);
			}
			if(types.size()>1){
				this.valueType = types.get(1);
			}
		}
	}
	
	private ISchemaType keyType;
	
	private ISchemaType valueType;

	@Override
	public ISchemaType getKeyType() {
		return this.keyType;
	}

	@Override
	public ISchemaType getValueType() {
		return this.valueType;
	}
}
