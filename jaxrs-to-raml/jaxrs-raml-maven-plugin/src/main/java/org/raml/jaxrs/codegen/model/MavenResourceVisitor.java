package org.raml.jaxrs.codegen.model;

import java.io.File;

import org.aml.typesystem.ITypeModel;

import com.mulesoft.jaxrs.raml.IRamlConfig;
import com.mulesoft.jaxrs.raml.ResourceVisitor;
import com.mulesoft.jaxrs.raml.StructureType;
import com.mulesoft.jaxrs.raml.reflection.RuntimeResourceVisitor;

/**
 * <p>MavenResourceVisitor class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class MavenResourceVisitor extends RuntimeResourceVisitor {

	
	private IRamlConfig config;

	/**
	 * <p>Constructor for MavenResourceVisitor.</p>
	 *
	 * @param outputFile a {@link java.io.File} object.
	 * @param classLoader a {@link java.lang.ClassLoader} object.
	 * @param config a {@link com.mulesoft.jaxrs.raml.IRamlConfig} object.
	 */
	public MavenResourceVisitor(File outputFile, ClassLoader classLoader,IRamlConfig config) {
		super(outputFile, classLoader,config);
		this.config=config;
	}

	/** {@inheritDoc} */
	@Override
	protected boolean generateXMLSchema(ITypeModel t, StructureType st) {
		return super.generateXMLSchema(t,st);
	}

	/**
	 * <p>createResourceVisitor.</p>
	 *
	 * @return a {@link com.mulesoft.jaxrs.raml.ResourceVisitor} object.
	 */
	protected ResourceVisitor createResourceVisitor() {
		return new MavenResourceVisitor(outputFile, classLoader,config);
	}
}
