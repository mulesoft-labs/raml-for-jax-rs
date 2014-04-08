package com.mulesoft.jaxrs.raml.annotation.tests;

import java.util.List;

import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.MimeType;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.model.Response;
import org.raml.model.parameter.FormParameter;
import org.raml.model.parameter.Header;
import org.raml.model.parameter.UriParameter;
import org.raml.parser.visitor.RamlDocumentBuilder;

import com.mulesoft.jaxrs.raml.annotation.model.reflection.RuntimeRamlBuilder;

import junit.framework.TestCase;

public class BasicTest extends TestCase{

	
	public void test0(){
		RuntimeRamlBuilder runtimeRamlBuilder = new RuntimeRamlBuilder();
		runtimeRamlBuilder.addClass(ItemResource.class);
		String raml = runtimeRamlBuilder.toRAML();
		Raml build = new RamlDocumentBuilder().build(raml);
		Resource resource = build.getResource("/item");		
		TestCase.assertNotNull(resource);
		Resource resource2 = resource.getResource("/a").getResource("/{version}");
		UriParameter uriParameter = resource2.getUriParameters().get("version");
		TestCase.assertTrue(uriParameter.isRequired());
		TestCase.assertNotNull(resource2.getAction(ActionType.PUT));
	}
	
	public void test1(){
		RuntimeRamlBuilder runtimeRamlBuilder = new RuntimeRamlBuilder();
		runtimeRamlBuilder.addClass(TestResource1.class);
		String raml = runtimeRamlBuilder.toRAML();
		Raml build = new RamlDocumentBuilder().build(raml);
		Resource resource = build.getResource("/users/{username}");		
		TestCase.assertNotNull(resource);
		Resource resource2 = resource.getResource("/qqq/{someBoolean}");
		Action action = resource2.getAction(ActionType.POST);
		TestCase.assertNotNull(action);
		Header header = action.getHeaders().get("h");
		TestCase.assertNotNull(header);
	}
	
	public void test2(){
		RuntimeRamlBuilder runtimeRamlBuilder = new RuntimeRamlBuilder();
		runtimeRamlBuilder.addClass(TestResource2.class);
		String raml = runtimeRamlBuilder.toRAML();
		Raml build = new RamlDocumentBuilder().build(raml);
		Resource resource = build.getResource("/test2/qqq");		
		Action action = resource.getAction(ActionType.PUT);
		TestCase.assertNotNull(action);
		Response response = action.getResponses().get("200");
		TestCase.assertNotNull(response);
		TestCase.assertNotNull(response.getBody().get("application/json"));
		TestCase.assertNotNull(action.getBody().get("application/xml"));
	}
	
	public void test3(){
		RuntimeRamlBuilder runtimeRamlBuilder = new RuntimeRamlBuilder();
		runtimeRamlBuilder.addClass(TestResource2.class);
		String raml = runtimeRamlBuilder.toRAML();
		Raml build = new RamlDocumentBuilder().build(raml);
		Resource resource = build.getResource("/test2/qqq");		
		Action action = resource.getAction(ActionType.POST);
		TestCase.assertNotNull(action);
		Response response = action.getResponses().get("200");
		TestCase.assertNotNull(response);
		TestCase.assertNotNull(response.getBody().get("application/json"));
		TestCase.assertNotNull(action.getBody().get("application/xml"));
	}
	
	public void test4(){
		RuntimeRamlBuilder runtimeRamlBuilder = new RuntimeRamlBuilder();
		runtimeRamlBuilder.addClass(TestResource4.class);
		String raml = runtimeRamlBuilder.toRAML();
		Raml build = new RamlDocumentBuilder().build(raml);
		Resource resource = build.getResource("/forms2");		
		Action action = resource.getAction(ActionType.POST);
		TestCase.assertNotNull(action);
		MimeType mimeType = action.getBody().get("multipart/form-data");
		TestCase.assertNotNull(mimeType);
		List<FormParameter> list = mimeType.getFormParameters().get("enabled");
		TestCase.assertNotNull(list);
		TestCase.assertNotNull(list.get(0));
		TestCase.assertNotNull(list.get(0).getDefaultValue());
	}
	
	public void test5(){
		RuntimeRamlBuilder runtimeRamlBuilder = new RuntimeRamlBuilder();
		runtimeRamlBuilder.addClass(TestResource3.class);
		String raml = runtimeRamlBuilder.toRAML();
		Raml build = new RamlDocumentBuilder().build(raml);
		Resource resource = build.getResource("/world");		
		resource = resource.getResource("/countries");
		Action action = resource.getAction(ActionType.POST);
		TestCase.assertNotNull(action);
	}
}
