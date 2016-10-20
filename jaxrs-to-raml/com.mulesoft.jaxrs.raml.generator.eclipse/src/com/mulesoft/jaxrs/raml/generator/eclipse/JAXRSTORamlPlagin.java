package com.mulesoft.jaxrs.raml.generator.eclipse;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class JAXRSTORamlPlagin extends AbstractUIPlugin {

	public JAXRSTORamlPlagin() {
	}
	
	static JAXRSTORamlPlagin INSTANCE;
	
	
	public void start(BundleContext context) throws Exception {
		INSTANCE=this;
		super.start(context);
	}
	
	
	public void stop(BundleContext context) throws Exception {
		INSTANCE=null;
		super.stop(context);
	}
	
	public static JAXRSTORamlPlagin getInstance(){
		return INSTANCE;
	}
	
}
