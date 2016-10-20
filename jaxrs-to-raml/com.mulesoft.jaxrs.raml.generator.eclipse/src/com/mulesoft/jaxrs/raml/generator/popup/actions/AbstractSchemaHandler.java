package com.mulesoft.jaxrs.raml.generator.popup.actions;

import java.io.File;

import org.eclipse.core.runtime.Platform;

public abstract class AbstractSchemaHandler {
	
	private static final String FILE_PROTOCOL_WIN = "file:///";

	private static final String FILE_PROTOCOL_MACOS_LINUX = "file://";
	
	private static final String WINDOWS_SIGN = "win";
	
	protected String originalSchemaURI;
	
	protected File enhancedSchema;
	
	public AbstractSchemaHandler(String originalSchemaURI) {
		this.originalSchemaURI = originalSchemaURI;
	}
	
	public String getEnhancedSchemaURI() {
		File originalFile = uriToFile(originalSchemaURI);
		if (!originalFile.exists()) {
			return originalSchemaURI; 
		}
		
		long originalLastModified = originalFile.lastModified();
		if (enhancedSchema == null || !enhancedSchema.exists() || 
				enhancedSchema.lastModified() < originalLastModified) {
			
			enhancedSchema = enhanceSchema(originalSchemaURI);
			if (enhancedSchema == null) {
				return originalSchemaURI;
			}
		}

		return fileToUri(enhancedSchema);
	}
	
	abstract protected File enhanceSchema(String originalSchemaURI);
	

	protected static File uriToFile(String uri) {
		return new File(uri);
	}

	protected static String fileToUri(File file) {
		String filePrefix = getFilePrefix();
		return filePrefix + file.toString();
	}

	protected static final String getFilePrefix() {
		String filePrefix = FILE_PROTOCOL_MACOS_LINUX;
		if (Platform.getOS() != null && Platform.getOS().contains(WINDOWS_SIGN)) {
			filePrefix = FILE_PROTOCOL_WIN;
		}

		return filePrefix;
	}
}
