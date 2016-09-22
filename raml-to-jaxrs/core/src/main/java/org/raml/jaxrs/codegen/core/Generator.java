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

import static com.sun.codemodel.JMod.PUBLIC;
import static com.sun.codemodel.JMod.STATIC;
import static org.apache.commons.lang.StringUtils.capitalize;
import static org.apache.commons.lang.StringUtils.defaultString;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.strip;
import static org.raml.jaxrs.codegen.core.Constants.RESPONSE_HEADER_WILDCARD_SYMBOL;
import static org.raml.jaxrs.codegen.core.Names.EXAMPLE_PREFIX;
import static org.raml.jaxrs.codegen.core.Names.GENERIC_PAYLOAD_ARGUMENT_NAME;
import static org.raml.jaxrs.codegen.core.Names.MULTIPLE_RESPONSE_HEADERS_ARGUMENT_NAME;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.aml.apimodel.AbstractParam;
import org.aml.apimodel.Action;
import org.aml.apimodel.MimeType;
import org.aml.apimodel.Api;
import org.aml.apimodel.Resource;
import org.aml.apimodel.Response;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.raml.jaxrs.codegen.core.ext.GeneratorExtension;
import org.raml.jaxrs.codegen.core.ext.InterfaceNameBuilderExtension;
import org.raml.jaxrs.codegen.core.ext.MethodNameBuilderExtension;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

/**
 * <p>Generator class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class Generator extends AbstractGenerator
{
    

    /** {@inheritDoc} */
    protected void createResourceInterface(final Resource resource, final Api raml,Configuration config) throws Exception
    {

    	String resourceInterfaceName = null;
    	for (GeneratorExtension e : extensions) {
    		if(e instanceof InterfaceNameBuilderExtension){
    			InterfaceNameBuilderExtension inbe = (InterfaceNameBuilderExtension) e;
    			resourceInterfaceName = inbe.buildResourceInterfaceName(resource);
    			if(resourceInterfaceName!=null){
    				break;
    			}
    		}
        }
    	if(resourceInterfaceName==null){
    		resourceInterfaceName = Names.buildResourceInterfaceName(resource,config);
    	}

    	final JDefinedClass resourceInterface = context.createResourceInterface(resourceInterfaceName);
        context.setCurrentResourceInterface(resourceInterface);

        final String path = strip(resource.relativeUri(), "/");
        resourceInterface.annotate(Path.class).param(DEFAULT_ANNOTATION_PARAMETER,
            StringUtils.defaultIfBlank(path, "/"));

        if (isNotBlank(resource.description()))
        {
            resourceInterface.javadoc().add(resource.description());
        }
        
        addResourceMethods(resource, resourceInterface, path);
        
        /* call registered extensions */
        for (GeneratorExtension e : extensions) {
        	e.onCreateResourceInterface(resourceInterface, resource);
        }
    }

    

    /** {@inheritDoc} */
    protected void addResourceMethod(final JDefinedClass resourceInterface,
    							   final Resource resource,
                                   final String resourceInterfacePath,
                                   final Action action,
                                   final MimeType bodyMimeType,
                                   final boolean addBodyMimeTypeInMethodName,
                                   final Collection<MimeType> uniqueResponseMimeTypes) throws Exception
    {
    	MimeType actualBodyMimeType = addBodyMimeTypeInMethodName ? bodyMimeType : null;
		String methodName = null;
		if(this.extensions!=null){
			for(GeneratorExtension ext : this.extensions){
				if(ext instanceof MethodNameBuilderExtension){
					methodName = ((MethodNameBuilderExtension)ext)
							.buildResourceMethodName(action, actualBodyMimeType,resource);
					break;
				}
			}
		}
		if(methodName==null){
			methodName = Names.buildResourceMethodName(action,actualBodyMimeType);
		}
		
        Configuration configuration = context.getConfiguration();
        String asyncResourceTrait = configuration.getAsyncResourceTrait();
        boolean asyncMethod = isNotBlank(asyncResourceTrait) && action.getIs().contains(asyncResourceTrait);
        
        final JType resourceMethodReturnType = getResourceMethodReturnType(methodName, action,
            uniqueResponseMimeTypes.isEmpty(),asyncMethod, resourceInterface);

        // the actually created unique method name should be needed in the previous method but
        // no way of doing this :(
        final JMethod method = context.createResourceMethod(resourceInterface, methodName,
            resourceMethodReturnType);
        
        if(configuration.isUseJsr303Annotations()) {
            method.annotate(Valid.class);
        }
        
        if (configuration.getMethodThrowException() != null ) {
            method._throws(configuration.getMethodThrowException());
        }

        context.addHttpMethodAnnotation(action.method(), method);

        addParamAnnotation(resourceInterfacePath, action, method);
        addConsumesAnnotation(bodyMimeType, method);
        addProducesAnnotation(uniqueResponseMimeTypes, method);

        final JDocComment javadoc = addBaseJavaDoc(action, method);

        addPathParameters(action, method, javadoc);
        addHeaderParameters(action, method, javadoc);
        addQueryParameters(action, method, javadoc);
        addBodyParameters(bodyMimeType, method, javadoc);
        if (asyncMethod) {
            addAsyncResponseParameter(asyncResourceTrait, method, javadoc);
        }
        
        /* call registered extensions */
        for (GeneratorExtension e : extensions) {
        	e.onAddResourceMethod(method, action, bodyMimeType, uniqueResponseMimeTypes);
        }
     

    }

    private JType getResourceMethodReturnType(final String methodName,
                                              final Action action,
                                              final boolean returnsVoid,
                                              final boolean asyncMethod,
                                              final JDefinedClass resourceInterface) throws Exception
    {
    	if (asyncMethod) 
    	{
    	   // returns void but also generate the response helper object
    	   createResourceMethodReturnType(methodName, action, resourceInterface);
    	   return types.getGeneratorType(void.class);
    	}
    	else
        if (returnsVoid&&context.getConfiguration().isEmptyResponseReturnVoid())
        {
            return types.getGeneratorType(void.class);
        }
        else
        {
            return createResourceMethodReturnType(methodName, action, resourceInterface);
        }
    }
    private void addAsyncResponseParameter(String asyncResourceTrait,final JMethod method,final JDocComment javadoc) throws Exception {
    	
      final String argumentName = Names.buildVariableName(asyncResourceTrait);
    	
      final JVar argumentVariable = method.param(types.getGeneratorClass("javax.ws.rs.container.AsyncResponse"),
      argumentName);
    
      argumentVariable.annotate(types.getGeneratorClass("javax.ws.rs.container.Suspended"));
      javadoc.addParam( argumentVariable.name()).add(asyncResourceTrait);
   }

    private JDefinedClass createResourceMethodReturnType(final String methodName,
                                                         final Action action,
                                                         final JDefinedClass resourceInterface)
        throws Exception
    {
        final JDefinedClass responseClass = resourceInterface._class(capitalize(methodName) + "Response")
            ._extends(context.getResponseWrapperType());

        final JMethod responseClassConstructor = responseClass.constructor(JMod.PRIVATE);
        responseClassConstructor.param(javax.ws.rs.core.Response.class, "delegate");
        responseClassConstructor.body().invoke("super").arg(JExpr.ref("delegate"));

        for (final Response statusCodeAndResponse : action.responses())
        {
            createResponseBuilderInResourceMethodReturnType(action, responseClass, statusCodeAndResponse);
        }

        return responseClass;
    }

    private void createResponseBuilderInResourceMethodReturnType(final Action action,
                                                                 final JDefinedClass responseClass,
                                                                 final Response statusCodeAndResponse)
        throws Exception
    {
        final int statusCode = NumberUtils.toInt(statusCodeAndResponse.code());
        final Response response = statusCodeAndResponse;

        if (!response.hasBody())
        {
            createResponseBuilderInResourceMethodReturnType(responseClass, statusCode, response, null);
        }
        else
        {
            for (final MimeType mimeType : response.body())
            {
                createResponseBuilderInResourceMethodReturnType(responseClass, statusCode, response, mimeType);
            }
        }
    }

    private void createResponseBuilderInResourceMethodReturnType(final JDefinedClass responseClass,
                                                                 final int statusCode,
                                                                 final Response response,
                                                                 final MimeType responseMimeType)
        throws Exception
    {
        final String responseBuilderMethodName = Names.buildResponseMethodName(statusCode, responseMimeType);

        final JMethod responseBuilderMethod = responseClass.method(PUBLIC + STATIC, responseClass,
            responseBuilderMethodName);

        final JDocComment javadoc = responseBuilderMethod.javadoc();

        if (isNotBlank(response.description()))
        {
            javadoc.add(response.description());
        }

        if ((responseMimeType != null) && (isNotBlank(responseMimeType.getExample())))
        {
            javadoc.add(EXAMPLE_PREFIX + responseMimeType.getExample());
        }

        JInvocation builderArgument = types.getGeneratorClass(javax.ws.rs.core.Response.class)
            .staticInvoke("status")
            .arg(JExpr.lit(statusCode));

        if (responseMimeType != null)
        {
            builderArgument = builderArgument.invoke("header")
                .arg(HttpHeaders.CONTENT_TYPE)
                .arg(responseMimeType.getType());
        }

        final StringBuilder freeFormHeadersDescription = new StringBuilder();
        
        for (final AbstractParam namedHeaderParameter : response.headers())
        {
            final String headerName = namedHeaderParameter.getKey();
            final AbstractParam header = namedHeaderParameter;

            if (headerName.contains(RESPONSE_HEADER_WILDCARD_SYMBOL))
            {
                appendParameterJavadocDescription(header, freeFormHeadersDescription);
                continue;
            }

            final String argumentName = Names.buildVariableName(headerName);
            if (!header.isRepeat()){
            	builderArgument = builderArgument.invoke("header").arg(headerName).arg(JExpr.ref(argumentName));
            }
            addParameterJavaDoc(header, argumentName, javadoc);

            responseBuilderMethod.param(types.buildParameterType(header, argumentName), argumentName);
        }

        final JBlock responseBuilderMethodBody = responseBuilderMethod.body();

        final JVar builderVariable = responseBuilderMethodBody.decl(
            types.getGeneratorType(ResponseBuilder.class), "responseBuilder", builderArgument);

        if (freeFormHeadersDescription.length() > 0)
        {
            // generate a Map<String, List<Object>> argument for {?} headers
            final JClass listOfObjectsClass = types.getGeneratorClass(List.class).narrow(Object.class);
            final JClass headersArgument = types.getGeneratorClass(Map.class).narrow(
                types.getGeneratorClass(String.class), listOfObjectsClass);

            builderArgument = responseBuilderMethodBody.invoke("headers")
                .arg(JExpr.ref(MULTIPLE_RESPONSE_HEADERS_ARGUMENT_NAME))
                .arg(builderVariable);

            final JVar param = responseBuilderMethod.param(headersArgument,
                MULTIPLE_RESPONSE_HEADERS_ARGUMENT_NAME);

            javadoc.addParam(param).add(freeFormHeadersDescription.toString());
        }

        if (responseMimeType != null)
        {
            responseBuilderMethodBody.invoke(builderVariable, "entity").arg(
                JExpr.ref(GENERIC_PAYLOAD_ARGUMENT_NAME));
            responseBuilderMethod.param(types.getResponseEntityClass(responseMimeType),
                GENERIC_PAYLOAD_ARGUMENT_NAME);
            javadoc.addParam(GENERIC_PAYLOAD_ARGUMENT_NAME).add(defaultString(responseMimeType.getExample()));
        }
        for (final AbstractParam namedHeaderParameter : response.headers())
        {
            final String headerName = namedHeaderParameter.getKey();
            final AbstractParam header = namedHeaderParameter;

            final String argumentName = Names.buildVariableName(headerName);
            if (header.isRepeat()){
            	JBlock body = responseBuilderMethod.body().forEach(context.getGeneratorType(Types.getJavaType(header)), "h", JExpr.ref(argumentName)).body();
				body.add(JExpr.invoke(JExpr.ref("responseBuilder"), "header").arg(headerName).arg(JExpr.ref("h")));
            }            
        }
        responseBuilderMethodBody._return(JExpr._new(responseClass).arg(builderVariable.invoke("build")));
    }

    

        
    
    

}
