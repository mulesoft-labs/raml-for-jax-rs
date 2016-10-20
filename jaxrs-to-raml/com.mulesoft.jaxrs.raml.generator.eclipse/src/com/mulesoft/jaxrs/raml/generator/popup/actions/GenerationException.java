package com.mulesoft.jaxrs.raml.generator.popup.actions;

public class GenerationException extends RuntimeException {

	private static final long serialVersionUID = -2410631395084262382L;
	private final String shortMessage;
	private final String detailMessage;
	
	public GenerationException(String shortMessage, String detainMessage) {
		this.shortMessage = shortMessage;
		this.detailMessage = detainMessage;
	}

	public String getShortMessage() {
		return shortMessage;
	}

	public String getDetailMessage() {
		return detailMessage;
	}

}
