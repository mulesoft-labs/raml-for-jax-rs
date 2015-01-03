package org.raml.jaxrs.codegen.maven;

import java.util.Set;

import org.raml.model.ActionType;
import org.raml.model.Protocol;

import com.google.common.collect.Sets;
import com.mulesoft.jaxrs.raml.annotation.model.IRamlConfig;

/**
 * Simple IRamlConfig implementation to handle setting of API title, baseUrl, and version from the Maven plugin configuration.
 */
public class MavenRamlConfig implements IRamlConfig {
	private String title;
	private String baseUrl;
	private String version;
	
	public MavenRamlConfig(String title, String baseUrl, String version) {
		super();
		this.title = title;
		this.baseUrl = baseUrl;
		this.version = version;
	}

	public String getTitle() {
		return title;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public String getVersion() {
		return version;
	}

	public Set<Protocol> getProtocols() {
		return Sets.newHashSet();
	}

	public String getResponseCode(ActionType type) {
		return "200";
	}

	public boolean isSingle() {
		return false;
	}

	public boolean isSorted() {
		return false;
	}

	public boolean doFullTree() {
		return false;
	}

	public void setSingle(boolean selection) {
		// NOOP
	}
}
