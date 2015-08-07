package org.raml.jaxrs.codegen.maven;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.raml.model.ActionType;
import org.raml.model.Protocol;

import com.google.common.collect.Sets;
import com.mulesoft.jaxrs.raml.annotation.model.IRamlConfig;
import com.mulesoft.jaxrs.raml.annotation.model.IResourceVisitorExtension;

/**
 * Simple IRamlConfig implementation to handle setting of API title, baseUrl, and version from the Maven plugin configuration.
 *
 * @author kor
 * @version $Id: $Id
 */
public class MavenRamlConfig implements IRamlConfig {
	private String title;
	private String baseUrl;
	private String version;
	private List<IResourceVisitorExtension> extensions
			= new ArrayList<IResourceVisitorExtension>();
	
	/**
	 * <p>Constructor for MavenRamlConfig.</p>
	 *
	 * @param title a {@link java.lang.String} object.
	 * @param baseUrl a {@link java.lang.String} object.
	 * @param version a {@link java.lang.String} object.
	 */
	public MavenRamlConfig(String title, String baseUrl, String version) {
		super();
		this.title = title;
		this.baseUrl = baseUrl;
		this.version = version;
	}

	/**
	 * <p>Getter for the field <code>title</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * <p>Getter for the field <code>baseUrl</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * <p>Getter for the field <code>version</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * <p>getProtocols.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<Protocol> getProtocols() {
		return Sets.newHashSet();
	}

	/** {@inheritDoc} */
	public String getResponseCode(ActionType type) {
		return "200";
	}

	/**
	 * <p>isSingle.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isSingle() {
		return false;
	}

	/**
	 * <p>isSorted.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isSorted() {
		return false;
	}

	/**
	 * <p>doFullTree.</p>
	 *
	 * @return a boolean.
	 */
	public boolean doFullTree() {
		return false;
	}

	/** {@inheritDoc} */
	public void setSingle(boolean selection) {
		// NOOP
	}

	public List<IResourceVisitorExtension> getExtensions() {
		return extensions;
	}
}
