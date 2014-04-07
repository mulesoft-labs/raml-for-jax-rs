	package com.mulesoft.jaxrs.raml.annotation.model;

import java.util.HashSet;

import org.raml.emitter.IRamlHierarchyTarget;
import org.raml.emitter.RamlEmitterV2;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.MimeType;
import org.raml.model.ParamType;
import org.raml.model.Resource;
import org.raml.model.Response;
import org.raml.model.parameter.AbstractParam;
import org.raml.model.parameter.Header;
import org.raml.model.parameter.QueryParameter;
import org.raml.model.parameter.UriParameter;


public class ResourceVisitor {

	private static final String PATH_PARAM = "PathParam";

	private static final String HEADER_PARAM = "HeaderParam";

	private static final String CONSUMES = "Consumes";

	private static final String PRODUCES = "Produces";

	private static final String QUERY_PARAM = "QueryParam";

	private static final String PATH = "Path";

	protected RAMLModelHelper spec = new RAMLModelHelper();

	protected String[] classConsumes;
	protected String[] classProduces;
	
	protected HashSet<ITypeModel>consumedTypes=new HashSet<ITypeModel>();

	private String basePath;
	
	public void visit(ITypeModel t) {
		consumedTypes.add(t);
		classConsumes=t.getAnnotationValues(CONSUMES);
		classProduces=t.getAnnotationValues(PRODUCES);
		String annotationValue = t.getAnnotationValue(PATH);
		if (basePath!=null){
			if (annotationValue==null)
			{
				annotationValue="";
			}
			annotationValue=basePath+annotationValue;
		}
		if (annotationValue != null) {
			if (!annotationValue.endsWith("/")){
				annotationValue=annotationValue+"/";
			}
			IMethodModel[] methods = t.getMethods();
			for (IMethodModel m : methods) {
				visit(m, annotationValue);
			}
		}
	}

	class StringHolder {
		String content;
	}

	public String getRaml() {
		RamlEmitterV2 emmitter = new RamlEmitterV2();
		emmitter.setSingle(false);		
		final StringHolder holder = new StringHolder();
		emmitter.dump(new IRamlHierarchyTarget() {

			@Override
			public void write(String path, String content) {

			}

			@Override
			public void writeRoot(String content) {
				holder.content = content;
			}

		}, spec.getCoreRaml());
		return holder.content;
	}

	private void visit(IMethodModel m, String annotationValue) {
		boolean hasPath = m.hasAnnotation(PATH);
		if (hasPath) {
			String annotationValue2 = m.getAnnotationValue(PATH);
			if(annotationValue.endsWith("/")){
				if (annotationValue2.startsWith("/")){
					annotationValue2=annotationValue2.substring(1);
				}
			}
					
			annotationValue += annotationValue2;
		}

		boolean isWs = hasPath;
		for (ActionType q : ActionType.values()) {
			boolean hasAnnotation = m.hasAnnotation(q.name());
			isWs |= hasAnnotation;
		}
		if (isWs) {
			Resource res = new Resource();
			IDocInfo documentation = m.getBasicDocInfo();
			res.setDescription(documentation.getDocumentation());
			
			
			if (hasPath){
				ITypeModel returnedType = m.getReturnedType();
				if (returnedType!=null){
					if (consumedTypes.add(returnedType)){
					ResourceVisitor resourceVisitor = new ResourceVisitor();
					resourceVisitor.consumedTypes.addAll(this.consumedTypes);
					resourceVisitor.basePath=annotationValue;
					resourceVisitor.spec=this.spec;
					resourceVisitor.visit(returnedType);
					}
				}
			}
			if (annotationValue.endsWith("/")){
				res.setRelativeUri(annotationValue.substring(0,annotationValue.length()-1));
			}
			else{
				res.setRelativeUri(annotationValue);
			}
			for (ActionType q : ActionType.values()) {
				boolean hasAnnotation = m.hasAnnotation(q.name());
				if (hasAnnotation) {
					addMethod(q, res, m,documentation);
				}
			}
			spec.addResource(res);
			
		}
	}

	private void addMethod(ActionType q, Resource res, IMethodModel m, IDocInfo documentation) {
		Action value = new Action();
		value.setType(q);
		res.getActions().put(q, value);
		IParameterModel[] parameters = m.getParameters();
		
		for (IParameterModel pm : parameters) {
			if (pm.hasAnnotation(QUERY_PARAM)) {
				String annotationValue = pm.getAnnotationValue(QUERY_PARAM);
				String type = pm.getType();
				QueryParameter value2 = new QueryParameter();
				proceedType(type, value2);
				value2.setDescription(documentation.getDocumentation(pm.getName()));
				value.getQueryParameters().put(annotationValue,
						value2);
			}
		}
		for (IParameterModel pm : parameters) {
			if (pm.hasAnnotation(HEADER_PARAM)) {
				String annotationValue = pm.getAnnotationValue(HEADER_PARAM);
				Header value2 = new Header();
				proceedType(pm.getType(), value2);
				value2.setDescription(documentation.getDocumentation(pm.getName()));
				value.getHeaders().put(annotationValue,
						value2);
			}
		}
		for (IParameterModel pm : parameters) {
			if (pm.hasAnnotation(PATH_PARAM)) {
				String annotationValue = pm.getAnnotationValue(PATH_PARAM);
				UriParameter value2 = new UriParameter();
				value2.setDescription(documentation.getDocumentation(pm.getName()));
				proceedType(pm.getType(), value2);
				res.getUriParameters().put(annotationValue, value2);				
			}
		}
		String[] consumesValue = m.getAnnotationValues(CONSUMES);
		if (consumesValue==null){
			consumesValue=classConsumes;
		}
		if (consumesValue != null) {
			for (String s : consumesValue) {
				s = sanitizeMediaType(s);
				MimeType value2 = new MimeType();
				value2.setType(s);
				value.getBody().put(s, value2);
			}
		}
		String[] producesValue = m.getAnnotationValues(PRODUCES);
		if (producesValue==null){
			producesValue=classProduces;
		}
		if (producesValue != null) {
			Response value2 = new Response();
			value2.setDescription(documentation.getReturnInfo());
			for (String s : producesValue) {
				s = sanitizeMediaType(s);
				MimeType mimeType = new MimeType();
				mimeType.setType(s);
				value2.getBody().put(s, mimeType);
				
			}
			value.getResponses().put("200", value2);
		}
		else{
			Response value2 = new Response();
			value2.setDescription(documentation.getReturnInfo());
//			for (String s : producesValue) {
//				s = sanitizeMediaType(s);
//				MimeType mimeType = new MimeType();
//				mimeType.setType(s);
//				value2.getBody().put(s, mimeType);
//				
//			}
			value.getResponses().put("200", value2);
		}
	}

	private String sanitizeMediaType(String s) {
		if (s.contains("xml")){
			s="application/xml";
		}
		if (s.contains("json")){
			s="application/json";
		}
		return s;
	}

	private void proceedType(String type, AbstractParam value2) {
		if (type.equals("I")){
			value2.setType(ParamType.INTEGER);
			value2.setRequired(true);
		}
		if (type.equals("D")){
			value2.setType(ParamType.NUMBER);
			value2.setRequired(true);
		}
		if (type.equals("Z")){
			value2.setType(ParamType.BOOLEAN);			
		}
		if (type.equals("QInteger;")){
			value2.setType(ParamType.INTEGER);			
		}
		if (type.equals("QDouble;")){
			value2.setType(ParamType.NUMBER);			
		}
		if (type.equals("QBoolean;")){
			value2.setType(ParamType.BOOLEAN);
			value2.setRequired(true);
		}
	}
}