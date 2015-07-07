package com.mulesoft.jaxrs.raml.annotation.tests;

import java.util.List;
import java.util.Map;

import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.MimeType;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.model.Response;
import org.raml.model.parameter.FormParameter;
import org.raml.model.parameter.Header;
import org.raml.model.parameter.QueryParameter;
import org.raml.model.parameter.UriParameter;
import org.raml.parser.visitor.RamlDocumentBuilder;

import com.mulesoft.jaxrs.raml.annotation.model.reflection.RuntimeRamlBuilder;
import com.mulesoft.jaxrs.raml.annotation.tests.TestResource5Child;

import junit.framework.TestCase;

public class BasicTest extends TestCase{

	
	public void test0(){
		RuntimeRamlBuilder runtimeRamlBuilder = new RuntimeRamlBuilder();
		runtimeRamlBuilder.addClass(ItemResource.class);
		String raml = runtimeRamlBuilder.toRAML();
		Raml build = new RamlDocumentBuilder().build(raml);
		Resource resource = build.getResource("/item");		 //$NON-NLS-1$
		TestCase.assertNotNull(resource);
		Resource resource2 = resource.getResource("/a").getResource("/{version}"); //$NON-NLS-1$ //$NON-NLS-2$
		UriParameter uriParameter = resource2.getUriParameters().get("version"); //$NON-NLS-1$
		TestCase.assertTrue(uriParameter.isRequired());
		TestCase.assertNotNull(resource2.getAction(ActionType.PUT));
	}
	
	public void test1(){
		RuntimeRamlBuilder runtimeRamlBuilder = new RuntimeRamlBuilder();
		runtimeRamlBuilder.addClass(TestResource1.class);
		String raml = runtimeRamlBuilder.toRAML();
		Raml build = new RamlDocumentBuilder().build(raml);
		Resource resource = build.getResource("/users/{username}");		 //$NON-NLS-1$
		TestCase.assertNotNull(resource);
		Resource resource2 = resource.getResource("/qqq/{someBoolean}"); //$NON-NLS-1$
		Action action = resource2.getAction(ActionType.POST);
		TestCase.assertNotNull(action);
		Header header = action.getHeaders().get("h"); //$NON-NLS-1$
		TestCase.assertNotNull(header);
	}
	
	public void test2(){
		RuntimeRamlBuilder runtimeRamlBuilder = new RuntimeRamlBuilder();
		runtimeRamlBuilder.addClass(TestResource2.class);
		String raml = runtimeRamlBuilder.toRAML();
		Raml build = new RamlDocumentBuilder().build(raml);
		Resource resource = build.getResource("/test2/qqq");		 //$NON-NLS-1$
		Action action = resource.getAction(ActionType.PUT);
		TestCase.assertNotNull(action);
		Response response = action.getResponses().get("200"); //$NON-NLS-1$
		TestCase.assertNotNull(response);
		TestCase.assertNotNull(response.getBody().get("application/json")); //$NON-NLS-1$
		TestCase.assertNotNull(action.getBody().get("application/xml")); //$NON-NLS-1$
	}
	
	public void test3(){
		RuntimeRamlBuilder runtimeRamlBuilder = new RuntimeRamlBuilder();
		runtimeRamlBuilder.addClass(TestResource2.class);
		String raml = runtimeRamlBuilder.toRAML();
		Raml build = new RamlDocumentBuilder().build(raml);
		Resource resource = build.getResource("/test2/qqq");		 //$NON-NLS-1$
		Action action = resource.getAction(ActionType.POST);
		TestCase.assertNotNull(action);
		Response response = action.getResponses().get("200"); //$NON-NLS-1$
		TestCase.assertNotNull(response);
		TestCase.assertNotNull(response.getBody().get("application/json")); //$NON-NLS-1$
		TestCase.assertNotNull(action.getBody().get("application/xml")); //$NON-NLS-1$
	}
	
	public void test4(){
		RuntimeRamlBuilder runtimeRamlBuilder = new RuntimeRamlBuilder();
		runtimeRamlBuilder.addClass(TestResource4.class);
		String raml = runtimeRamlBuilder.toRAML();
		Raml build = new RamlDocumentBuilder().build(raml);
		Resource resource = build.getResource("/forms2");		 //$NON-NLS-1$
		Action action = resource.getAction(ActionType.POST);
		TestCase.assertNotNull(action);
		MimeType mimeType = action.getBody().get("multipart/form-data"); //$NON-NLS-1$
		TestCase.assertNotNull(mimeType);
		List<FormParameter> list = mimeType.getFormParameters().get("enabled"); //$NON-NLS-1$
		TestCase.assertNotNull(list);
		TestCase.assertNotNull(list.get(0));
		TestCase.assertNotNull(list.get(0).getDefaultValue());
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
		Raml build = new RamlDocumentBuilder().build(raml);
		Resource resource = build.getResource("/root");		 //$NON-NLS-1$
		TestCase.assertNotNull(resource);
		Action getAction = resource.getAction(ActionType.GET);
		TestCase.assertNotNull(getAction);
		Action action = resource.getAction(ActionType.POST);
		TestCase.assertNotNull(action);
		MimeType mimeType = action.getBody().get("multipart/form-data"); //$NON-NLS-1$
		TestCase.assertNotNull(mimeType);
		List<FormParameter> list = mimeType.getFormParameters().get("visible"); //$NON-NLS-1$
		TestCase.assertNotNull(list);
		TestCase.assertTrue(list.size()>0);
		Map<String, QueryParameter> queryParameters = action.getQueryParameters();
		TestCase.assertNotNull(queryParameters);
		QueryParameter queryParameter = queryParameters.get("enabled");
		TestCase.assertNotNull(queryParameter);
	}
}
