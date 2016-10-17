package org.raml.jaxrs.codegen.model;

import java.io.File;

import com.mulesoft.jaxrs.raml.IRamlConfig;
import com.mulesoft.jaxrs.raml.ResourceVisitor;
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
	 * @param config a {@link IRamlConfig} object.
	 */
	public MavenResourceVisitor(File outputFile, ClassLoader classLoader,IRamlConfig config) {
		super(outputFile, classLoader,config);
		this.config=config;
	}

//	/** {@inheritDoc} */
//	@Override
//	protected boolean generateType(ITypeModel t, StructureType st) {
//		return super.generateType(t,st);
//	}

	/**
	 * <p>createResourceVisitor.</p>
	 *
	 * @return a {@link ResourceVisitor} object.
	 */
	protected ResourceVisitor createResourceVisitor() {
		return new MavenResourceVisitor(outputFile, classLoader,config);
	}
}
