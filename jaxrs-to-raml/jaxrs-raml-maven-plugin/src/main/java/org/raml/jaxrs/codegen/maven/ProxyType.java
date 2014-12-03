package org.raml.jaxrs.codegen.maven;

import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;
import com.mulesoft.jaxrs.raml.annotation.model.IFieldModel;
import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;

public class ProxyType implements ITypeModel {	
	
	public ProxyType(TypeModelRegistry registry, String key) {
		super();
		this.registry = registry;
		this.key = key;
	}

	private final TypeModelRegistry registry;
	
	private final String key;

	public String getName() {
		return registry.getType(key).getName();
	}

	public String getDocumentation() {
		return registry.getType(key).getDocumentation();
	}

	public IAnnotationModel[] getAnnotations() {
		return registry.getType(key).getAnnotations();
	}

	public String getAnnotationValue(String annotation) {
		return registry.getType(key).getAnnotationValue(annotation);
	}

	public String[] getAnnotationValues(String annotation) {
		return registry.getType(key).getAnnotationValues(annotation);
	}

	public boolean hasAnnotation(String name) {
		return registry.getType(key).hasAnnotation(name);
	}

	public IAnnotationModel getAnnotation(String name) {
		return registry.getType(key).getAnnotation(name);
	}

	public IMethodModel[] getMethods() {
		return registry.getType(key).getMethods();
	}

	public String getFullyQualifiedName() {
		return registry.getType(key).getFullyQualifiedName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProxyType other = (ProxyType) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	@Override
	public IFieldModel[] getFields() {
		return null;
	}

}
