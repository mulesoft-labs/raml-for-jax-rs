package com.mulesoft.jaxrs.raml.annotation.model;

import java.util.List;
import java.util.Set;

import org.raml.model.ActionType;
import org.raml.model.Protocol;

/**
 * <p>IRamlConfig interface.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public interface IRamlConfig {

	/**
	 * <p>getTitle.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	String getTitle();
	
	/**
	 * <p>getBaseUrl.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	String getBaseUrl();
	
	/**
	 * <p>getVersion.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getVersion();
	
	/**
	 * <p>getProtocols.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<Protocol>getProtocols();
	
	/**
	 * <p>getResponseCode.</p>
	 *
	 * @param type a {@link org.raml.model.ActionType} object.
	 * @return a {@link java.lang.String} object.
	 */
	String getResponseCode(ActionType type);

	/**
	 * <p>isSingle.</p>
	 *
	 * @return a boolean.
	 */
	boolean isSingle();

	/**
	 * <p>setSingle.</p>
	 *
	 * @param selection a boolean.
	 */
	void setSingle(boolean selection);

	/**
	 * <p>isSorted.</p>
	 *
	 * @return a boolean.
	 */
	boolean isSorted();

	/**
	 * <p>doFullTree.</p>
	 *
	 * @return a boolean.
	 */
	boolean doFullTree();
	
	/**
	 * <p>getExtensions.</p>
	 * 
	 * @return list of resource visitor extensions
	 */
	List<IResourceVisitorExtension> getExtensions();

}
