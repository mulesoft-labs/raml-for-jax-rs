package org.raml.schema.model.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.raml.schema.model.ISchemaProperty;
import org.raml.schema.model.ISchemaType;
import org.raml.schema.model.JAXBClassMapping;
import org.raml.schema.model.SchemaModelElement;

import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;
import com.mulesoft.jaxrs.raml.annotation.model.StructureType;

public class TypeModelImpl extends SchemaModelElement implements ISchemaType{

	public TypeModelImpl(
			String name,
			String classQualifiedName,
			Map<String,String> namespaces,
			StructureType parentStructureType,
			List<IAnnotationModel> annotations) {
		super(annotations);
		this.name = name;
		this.namespaces = namespaces;
		this.classQualifiedName = classQualifiedName;
		this.parentStructureType = parentStructureType;
	}
	
	public TypeModelImpl(
			String name,
			String classQualifiedName,
			Map<String,String> namespaces,
			StructureType parentStructureType,
			JAXBClassMapping mapping,
			List<IAnnotationModel> annotations) {
		super(annotations);
		this.name = name;
		this.namespaces = namespaces;
		this.classQualifiedName = classQualifiedName;
		this.parentStructureType = parentStructureType;
		this.mapping = mapping;
		this.isSimple = true;
	}
	
	protected TypeModelImpl(
			String name,
			String classQualifiedName,
			Map<String,String> namespaces,
			boolean isSimple,
			StructureType parentStructureType,
			List<IAnnotationModel> annotations) {
		super(annotations);
		this.name = name;
		this.isSimple = isSimple;
		this.namespaces = namespaces;
		this.classQualifiedName = classQualifiedName;
		this.parentStructureType = parentStructureType;
	}
	
	private String name;
	
	private String classQualifiedName;
	
	private Map<String,String> namespaces;
	
	private boolean isSimple = false;

	private List<ISchemaProperty> properties;
	
	private StructureType parentStructureType;
	
	private JAXBClassMapping mapping;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isSimple() {
		return this.isSimple;
	}

	@Override
	public boolean isComplex() {
		return !this.isSimple;
	}

	@Override
	public List<ISchemaProperty> getProperties() {
		return this.properties;
	}
	
	@Override
	public void addProperty(ISchemaProperty property){
		if(this.properties==null){
			this.properties = new ArrayList<ISchemaProperty>();
		}
		this.properties.add(property);
	}

	public Map<String, String> getNamespaces() {
		return namespaces;
	}

	@Override
	public String getQualifiedPropertyName(ISchemaProperty prop) {
		
		String namespace = prop.getNamespace();
		if(namespace!=null&&this.namespaces!=null){
			String pref = this.namespaces.get(namespace);
			if(pref!=null){
				return pref + ":" + prop.getName();
			}
		}
		return prop.getName();
	}

	public String getClassName() {
		int ind = classQualifiedName.lastIndexOf(".");
		if(ind<0){
			return classQualifiedName;
		}
		return classQualifiedName.substring(ind+1);
	}

	@Override
	public String getClassQualifiedName() {
		return classQualifiedName;
	}

	@Override
	public StructureType getParentStructureType() {
		return this.parentStructureType;
	}

	@Override
	public JAXBClassMapping getMapping() {
		return this.mapping;
	}

}
