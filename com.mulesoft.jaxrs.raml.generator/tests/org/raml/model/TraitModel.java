package org.raml.model;

import java.util.HashSet;

import org.raml.parser.annotation.Scalar;


public class TraitModel extends Action{

	@Scalar
	String displayName;
	
	@Scalar
	String usage;

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
