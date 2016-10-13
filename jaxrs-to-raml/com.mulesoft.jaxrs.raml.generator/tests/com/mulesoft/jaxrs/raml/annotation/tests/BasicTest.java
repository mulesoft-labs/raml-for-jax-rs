package com.mulesoft.jaxrs.raml.annotation.tests;

import org.aml.apimodel.Action;
import org.aml.apimodel.Api;
import org.aml.apimodel.INamedParam;
import org.aml.apimodel.MimeType;
import org.aml.apimodel.Resource;
import org.aml.apimodel.Response;
import org.aml.typesystem.ramlreader.TopLevelRamlModelBuilder;

import com.mulesoft.jaxrs.raml.reflection.RuntimeRamlBuilder;

import junit.framework.TestCase;

public class BasicTest extends TestCase{

	
	public void test0(){
		RuntimeRamlBuilder runtimeRamlBuilder = new RuntimeRamlBuilder();
		runtimeRamlBuilder.addClass(ItemResource.class);
		String raml = runtimeRamlBuilder.toRAML();
		final Api build = (Api) TopLevelRamlModelBuilder.build(raml);		
		Resource resource = build.getResource("/item");		 //$NON-NLS-1$
		TestCase.assertNotNull(resource);
		Resource resource2 = resource.getResource("/a").getResource("/{version}"); //$NON-NLS-1$ //$NON-NLS-2$
		INamedParam uriParameter = resource2.uriParameter("version"); //$NON-NLS-1$
		TestCase.assertTrue(uriParameter.isRequired());
		TestCase.assertNotNull(resource2.method("put"));
	}
	
	public void test1(){
		RuntimeRamlBuilder runtimeRamlBuilder = new RuntimeRamlBuilder();
		runtimeRamlBuilder.addClass(TestResource1.class);
		String raml = runtimeRamlBuilder.toRAML();
		final Api build = (Api) TopLevelRamlModelBuilder.build(raml);		
		Resource resource = build.getResource("/users/{username}");		 //$NON-NLS-1$
		TestCase.assertNotNull(resource);
		Resource resource2 = resource.getResource("/qqq/{someBoolean}"); //$NON-NLS-1$
		Action action = resource2.method("post");
		TestCase.assertNotNull(action);
		INamedParam header = action.header("h"); //$NON-NLS-1$
		TestCase.assertNotNull(header);
	}
	
	public void test2(){
		RuntimeRamlBuilder runtimeRamlBuilder = new RuntimeRamlBuilder();
		runtimeRamlBuilder.addClass(TestResource2.class);
		String raml = runtimeRamlBuilder.toRAML();
		final Api build = (Api) TopLevelRamlModelBuilder.build(raml);
		Resource resource = build.getResource("/test2/qqq");		 //$NON-NLS-1$
		Action action = resource.method("put");
		TestCase.assertNotNull(action);
		Response response = action.response("200"); //$NON-NLS-1$
		TestCase.assertNotNull(response);
		TestCase.assertNotNull(response.body("application/json")); //$NON-NLS-1$
		TestCase.assertNotNull(action.body("application/xml")); //$NON-NLS-1$
	}
	
	public void test3(){
		RuntimeRamlBuilder runtimeRamlBuilder = new RuntimeRamlBuilder();
		runtimeRamlBuilder.addClass(TestResource2.class);
		String raml = runtimeRamlBuilder.toRAML();
		final Api build = (Api) TopLevelRamlModelBuilder.build(raml);
		Resource resource = build.getResource("/test2/qqq");		 //$NON-NLS-1$
		Action action = resource.method("post");
		TestCase.assertNotNull(action);
		Response response = action.response("200"); //$NON-NLS-1$
		TestCase.assertNotNull(response);
		TestCase.assertNotNull(response.body("application/json")); //$NON-NLS-1$
		TestCase.assertNotNull(action.body("application/xml")); //$NON-NLS-1$
	}
	
	public void test4(){
		RuntimeRamlBuilder runtimeRamlBuilder = new RuntimeRamlBuilder();
		runtimeRamlBuilder.addClass(TestResource4.class);
		String raml = runtimeRamlBuilder.toRAML();
		final Api build = (Api) TopLevelRamlModelBuilder.build(raml);
		Resource resource = build.getResource("/forms2");		 //$NON-NLS-1$
		Action action = resource.method("post");
		TestCase.assertNotNull(action);
		MimeType mimeType = action.body("multipart/form-data"); //$NON-NLS-1$
		TestCase.assertNotNull(mimeType);
		INamedParam list = mimeType.getFormParameters().stream().filter(x->x.getKey().equals("enabled")).findAny().get(); //$NON-NLS-1$
		TestCase.assertNotNull(list.getDefaultValue());
	}
	
	/*public void test5(){
		RuntimeRamlBuilder runtimeRamlBuilder = new RuntimeRamlBuilder();
		runtimeRamlBuilder.addClass(TestResource3.class);
		String raml = runtimeRamlBuilder.toRAML();
		Raml build = new RamlDocumentBuilder().build(raml);
		Resource resource = build.getResource("/world");		 //$NON-NLS-1$
		resource = resource.getResource("/countries"); //$NON-NLS-1$
		Action action = resource.getAction(ActionType.POST);
		TestCase.assertNotNull(action);
	}
	*/
	
	
	public void test6(){
		RuntimeRamlBuilder runtimeRamlBuilder = new RuntimeRamlBuilder();
		runtimeRamlBuilder.addClass(TestResource5Child.class);
		String raml = runtimeRamlBuilder.toRAML();
		final Api build = (Api) TopLevelRamlModelBuilder.build(raml);
		Resource resource = build.getResource("/root");		 //$NON-NLS-1$
		TestCase.assertNotNull(resource);
		Action getAction = resource.method("get");
		TestCase.assertNotNull(getAction);
		Action action = resource.method("post");
		TestCase.assertNotNull(action);
		MimeType mimeType = action.body("multipart/form-data"); //$NON-NLS-1$
		TestCase.assertNotNull(mimeType);
		mimeType.getFormParameters().stream().filter(x->x.getKey().equals("visible")).findAny().get(); //$NON-NLS-1$
		INamedParam queryParameter = action.queryParam("enabled");
		TestCase.assertNotNull(queryParameter);		
	}
	
	public void test7(){
		RuntimeRamlBuilder runtimeRamlBuilder = new RuntimeRamlBuilder();
		runtimeRamlBuilder.addClass(HelloWorldRest.class);
		String raml = runtimeRamlBuilder.toRAML();
		final Api build = (Api) TopLevelRamlModelBuilder.build(raml);
		System.out.println(raml);		
	}
}
