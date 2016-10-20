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
package raml.jaxrs.eclipse.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.Workbench;
import org.jsonschema2pojo.AnnotationStyle;
import org.raml.jaxrs.codegen.core.ClientGenerator;
import org.raml.jaxrs.codegen.core.Configuration;
import org.raml.jaxrs.codegen.core.Configuration.JaxrsVersion;
import org.raml.jaxrs.codegen.core.Generator;
import org.raml.jaxrs.codegen.core.GeneratorProxy;

public class GenerationHandler extends AbstractHandler{

	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ISelection selection = Workbench.getInstance().getActiveWorkbenchWindow().getActivePage().getSelection();
		if(selection instanceof IStructuredSelection){
			IStructuredSelection sSelection = (IStructuredSelection) selection;
			Object element = sSelection.getFirstElement();
			if(element instanceof IFile){
				IFile file = (IFile) element;
				process(file);
			}
			return null;
			
		}
		return null;
	}

	private void process(IFile file) {
		
		Shell activeShell = Display.getCurrent().getActiveShell();
		UIConfiguration uiConfig = prepareUIConfiguration(file);
		ConfigurationDialog dialog = new ConfigurationDialog(activeShell, uiConfig);
		
		if(dialog.open() != Dialog.OK ){
			return ;
		}
		
		if(!uiConfig.isValid()){
			return;
		}
		
		Configuration configuration = prepareConfiguraton(uiConfig);
		
		try {
			File ramlOSFile = uiConfig.getRamlFile().getLocation().toFile();
			InputStreamReader ramlReader = new InputStreamReader( new FileInputStream(ramlOSFile) );
			new GeneratorProxy().run(ramlReader, configuration,ramlOSFile.getAbsolutePath());
			uiConfig.getDstFolder().refreshLocal( IResource.DEPTH_ONE, new NullProgressMonitor() );
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Configuration prepareConfiguraton(UIConfiguration uiConfig)
	{
		IContainer srcFolder = uiConfig.getSrcFolder();
		if(srcFolder == null){
			srcFolder = uiConfig.getRamlFile().getParent();
		}
		File srcOSFolder = srcFolder.getLocation().toFile();		
		File dstOSFolder = uiConfig.getDstFolder().getLocation().toFile();
		
		Configuration configuration = new Configuration();
		configuration.setOutputDirectory(dstOSFolder);
		configuration.setSourceDirectory(srcOSFolder);
		configuration.setBasePackageName(uiConfig.getBasePackageName());
		configuration.setGenerateClientInterface(uiConfig.getGenerateClientProxy());
		JaxrsVersion jaxrsVersion = JaxrsVersion.valueOf(uiConfig.getJaxrsVersion());
		if(jaxrsVersion!=null){
			configuration.setJaxrsVersion(jaxrsVersion);
		}
		
		AnnotationStyle jsonMapper = AnnotationStyle.valueOf(uiConfig.getJsonMapper());
		if(jsonMapper!=null){
			configuration.setJsonMapper(jsonMapper);
		}
		
		configuration.setUseJsr303Annotations(uiConfig.getUseJsr303Annotations());
		configuration.setEmptyResponseReturnVoid(uiConfig.getEmptyResponseUsesVoid());
		return configuration;
	}

	private UIConfiguration prepareUIConfiguration(IFile file) {
		
		UIConfiguration uiConfig = new UIConfiguration();
		uiConfig.setRamlFile(file);
		uiConfig.setSrcFolder(file.getParent());
		uiConfig.setDstFolder(null);
		uiConfig.setBasePackageName("org.raml.jaxrs.test");
		
		Configuration config = new Configuration();
		uiConfig.setJaxrsVersion(config.getJaxrsVersion().name());
		uiConfig.setEmptyResponseUsesVoid(config.isEmptyResponseReturnVoid());
		uiConfig.setJsonMapper(config.getJsonMapper().name());
		uiConfig.setUseJsr303Annotations(config.isUseJsr303Annotations());
		
		return uiConfig;
	}
}
