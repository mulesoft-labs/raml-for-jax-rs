package com.mulesoft.jaxrs.raml.annotation.model;

/**
 * <p>IClassLoaderVisitor interface.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public interface IClassLoaderVisitor {
	
	/**
	 * <p>setClassLoader.</p>
	 *
	 * @param classLoader a {@link java.lang.ClassLoader} object.
	 */
	public void setClassLoader(ClassLoader classLoader);

}
