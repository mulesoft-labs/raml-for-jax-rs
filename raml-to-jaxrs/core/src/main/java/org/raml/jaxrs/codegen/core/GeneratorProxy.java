/*
 * Copyright 2013-2015 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.jaxrs.codegen.core;

import java.io.InputStreamReader;

/**
 * <p>GeneratorProxy class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class GeneratorProxy {

	/**
	 * <p>run.</p>
	 *
	 * @param ramlReader a {@link java.io.InputStreamReader} object.
	 * @param configuration a {@link org.raml.jaxrs.codegen.core.Configuration} object.
	 * @throws java.lang.Exception if any.
	 */
	public void run(InputStreamReader ramlReader, Configuration configuration,String location) throws Exception {
		if (configuration.isGenerateClientInterface()){
			new ClientGenerator().run(ramlReader, configuration,location);
		}
		else{
			new Generator().run(ramlReader, configuration,location);
		}
	}

}
