package org.raml.jaxrs.codegen.core;

import java.io.InputStreamReader;

public class GeneratorProxy {

	public void run(InputStreamReader ramlReader, Configuration configuration) throws Exception {
		if (configuration.isGenerateClientInterface()){
			new ClientGenerator().run(ramlReader, configuration);
		}
		else{
			new Generator().run(ramlReader, configuration);
		}
	}

}
