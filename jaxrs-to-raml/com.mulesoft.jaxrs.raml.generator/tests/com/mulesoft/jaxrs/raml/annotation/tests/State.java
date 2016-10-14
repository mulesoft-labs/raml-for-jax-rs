package com.mulesoft.jaxrs.raml.annotation.tests;

import javax.validation.constraints.Pattern;

public class State {

	@Pattern(regexp = "...")
	String abbr;

	public String getAbbr() {
		return abbr;
	}

	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}
}
