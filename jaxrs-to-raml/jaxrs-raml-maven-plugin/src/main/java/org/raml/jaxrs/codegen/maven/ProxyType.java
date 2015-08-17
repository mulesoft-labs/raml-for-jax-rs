package org.raml.jaxrs.codegen.maven;

import java.util.List;

import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;
import com.mulesoft.jaxrs.raml.annotation.model.IFieldModel;
import com.mulesoft.jaxrs.raml.annotation.model.IMethodModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeModel;
import com.mulesoft.jaxrs.raml.annotation.model.ITypeParameter;

/**
 * <p>ProxyType class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class ProxyType implements ITypeModel {	
	
	/**
	 * <p>Constructor for ProxyType.</p>
	 *
	 * @param registry a {@link org.raml.jaxrs.codegen.maven.TypeModelRegistry} object.
	 * @param key a {@link java.lang.String} object.
	 */
	public ProxyType(TypeModelRegistry registry, String key) {
		super();
		this.registry = registry;
		this.key = key;
	}

	private final TypeModelRegistry registry;
	
	private final String key;

	/**
	 * <p>getName.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getName() {
		return registry.getType(key).getName();
	}

	/**
	 * <p>getDocumentation.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getDocumentation() {
		return registry.getType(key).getDocumentation();
	}

	/**
	 * <p>getAnnotations.</p>
	 *
	 * @return an array of {@link com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel} objects.
	 */
	public IAnnotationModel[] getAnnotations() {
		return registry.getType(key).getAnnotations();
	}

	/** {@inheritDoc} */
	public String getAnnotationValue(String annotation) {
		return registry.getType(key).getAnnotationValue(annotation);
	}

	/** {@inheritDoc} */
	public String[] getAnnotationValues(String annotation) {
		return registry.getType(key).getAnnotationValues(annotation);
	}

	/** {@inheritDoc} */
	public boolean hasAnnotation(String name) {
		return registry.getType(key).hasAnnotation(name);
	}

	/** {@inheritDoc} */
	public IAnnotationModel getAnnotation(String name) {
		return registry.getType(key).getAnnotation(name);
	}

	/**
	 * <p>getMethods.</p>
	 *
	 * @return an array of {@link com.mulesoft.jaxrs.raml.annotation.model.IMethodModel} objects.
	 */
	public IMethodModel[] getMethods() {
		return registry.getType(key).getMethods();
	}

	/**
	 * <p>getFullyQualifiedName.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getFullyQualifiedName() {
		return registry.getType(key).getFullyQualifiedName();
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	/** {@inheritDoc} */
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

	/** {@inheritDoc} */
	@Override
	public IFieldModel[] getFields() {
		return registry.getType(key).getFields();
	}

	@Override
	public List<ITypeParameter> getTypeParameters() {
		return registry.getType(key).getTypeParameters();
	}

	@Override
	public ITypeModel getSuperClass() {
		return registry.getType(key).getSuperClass();
	}

	@Override
	public ITypeModel[] getImplementedInterfaces() {
		return registry.getType(key).getImplementedInterfaces();
	}

	@Override
	public ITypeModel resolveClass(String qualifiedName) {
		return registry.getType(qualifiedName);
	}

	@Override
	public boolean hasAnnotationWithCanonicalName(String name) {
		return registry.getType(key).hasAnnotationWithCanonicalName(name);
	}

	@Override
	public IAnnotationModel getAnnotationByCanonicalName(String name) {
		return registry.getType(key).getAnnotationByCanonicalName(name);
	}

}
