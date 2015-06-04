package com.mulesoft.jaxrs.raml.annotation.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.raml.emitter.IRamlHierarchyTarget;
import org.raml.emitter.RamlEmitterV2;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.DocumentationItem;
import org.raml.model.MimeType;
import org.raml.model.ParamType;
import org.raml.model.Protocol;
import org.raml.model.Raml2;
import org.raml.model.Resource;
import org.raml.model.Response;
import org.raml.model.parameter.AbstractParam;
import org.raml.model.parameter.FormParameter;
import org.raml.model.parameter.Header;
import org.raml.model.parameter.QueryParameter;
import org.raml.model.parameter.UriParameter;
import org.raml.schema.model.ISchemaType;

import com.mulesoft.jaxrs.raml.annotation.model.reflection.ReflectionType;
import com.mulesoft.jaxrs.raml.jaxb.ExampleGenerator;
import com.mulesoft.jaxrs.raml.jaxb.JAXBRegistry;
import com.mulesoft.jaxrs.raml.jaxb.JAXBType;
import com.mulesoft.jaxrs.raml.jaxb.SchemaModelBuilder;
import com.mulesoft.jaxrs.raml.jaxb.XMLModelSerializer;
import com.mulesoft.jaxrs.raml.jaxb.XMLWriter;
import com.mulesoft.jaxrs.raml.jsonschema.JsonFormatter;
import com.mulesoft.jaxrs.raml.jsonschema.JsonUtil;
import com.mulesoft.jaxrs.raml.jsonschema.SchemaGenerator;

/**
 * <p>Abstract ResourceVisitor class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public abstract class ResourceVisitor {

	private static final String DEFAULT_RESPONSE = "200";
	
	private static final String API_OPERATION = "ApiOperation";
	
	private static final String API_RESPONSE = "ApiResponse";

	private static final String API_RESPONSES = "ApiResponses";

	private static final String CODE = "code";

	private static final String JSONSCHEMA = "-jsonschema";

	/** Constant <code>XML_FILE_EXT=".xml"</code> */
	protected static final String XML_FILE_EXT = ".xml"; //$NON-NLS-1$

	private static final String JSON_FILE_EXT = ".json"; //$NON-NLS-1$
	
	/** Constant <code>SCHEMAS_FOLDER="schemas"</code> */
	protected static final String SCHEMAS_FOLDER = "schemas"; //$NON-NLS-1$

	/** Constant <code>EXAMPLES_FOLDER="examples"</code> */
	protected static final String EXAMPLES_FOLDER = "examples"; //$NON-NLS-1$
	
	/** Constant <code>EXAMPLES_PREFFIX="EXAMPLES_FOLDER + /"</code> */
	protected static final String EXAMPLES_PREFFIX = EXAMPLES_FOLDER + "/"; //$NON-NLS-1$

	private static final String JSON = "json"; //$NON-NLS-1$

	private static final String XML = "xml"; //$NON-NLS-1$
	
	protected JAXBRegistry regsistry=new JAXBRegistry();

	public class CustomSchemaOutputResolver extends SchemaOutputResolver {

		private final String fileName;
		private File file;

		public CustomSchemaOutputResolver(String fileName) {
			this.fileName = fileName;
		}

		public Result createOutput(String namespaceURI, String suggestedFileName)
				throws IOException {
			if (outputFile != null) {
				File dir = new File(outputFile.getParent(), SCHEMAS_FOLDER); //$NON-NLS-1$
				dir.mkdirs();
				file = new File(dir, fileName);
			} else {
				file = new File(fileName);
			}
			StreamResult result = new StreamResult(file);
			result.setSystemId(file.toURI().toURL().toString());
			return result;
		}

		public File getFile() {
			return file;
		}

	}

	private static final String FORM = "form"; //$NON-NLS-1$

	private static final String DEFAULT_VALUE = "DefaultValue"; //$NON-NLS-1$

	private static final String PATH_PARAM = "PathParam"; //$NON-NLS-1$

	private static final String HEADER_PARAM = "HeaderParam"; //$NON-NLS-1$

	private static final String CONSUMES = "Consumes"; //$NON-NLS-1$

	private static final String PRODUCES = "Produces"; //$NON-NLS-1$

	private static final String QUERY_PARAM = "QueryParam"; //$NON-NLS-1$

	private static final String PATH = "Path"; //$NON-NLS-1$

	private static final String FORM_PARAM = "FormParam"; //$NON-NLS-1$

	private static final String XML_ROOT_ELEMENT = "XmlRootElement"; //$NON-NLS-1$
	
	private static final String XML_TYPE = "XmlType"; //$NON-NLS-1$
	
	private static final String XML_ACCESSOR_TYPE = "XmlAccessorType"; //$NON-NLS-1$
	
	private static final String XML_ACCESSOR_ORDER = "XmlAccessorOrder"; //$NON-NLS-1$

	private static final String RESPONSE = "response";
	
	private static final String MESSAGE = "message";

	protected RAMLModelHelper spec = new RAMLModelHelper();

	protected String[] classConsumes;
	protected String[] classProduces;

	protected HashSet<ITypeModel> consumedTypes = new HashSet<ITypeModel>();

	private String basePath;

	protected final File outputFile;

	protected final ClassLoader classLoader;

	private IRamlConfig config;

	/**
	 * <p>Constructor for ResourceVisitor.</p>
	 *
	 * @param outputFile a {@link java.io.File} object.
	 * @param classLoader a {@link java.lang.ClassLoader} object.
	 */
	public ResourceVisitor(File outputFile, ClassLoader classLoader) {
		this.outputFile = outputFile;
		this.classLoader = classLoader;
	}

	/**
	 * <p>visit.</p>
	 *
	 * @param t a {@link com.mulesoft.jaxrs.raml.annotation.model.ITypeModel} object.
	 */
	public void visit(ITypeModel t) {
		consumedTypes.add(t);
		
		IAnnotationModel apiAnn = t.getAnnotation("Api");
		if(apiAnn!=null){			
			
			String baseUri = apiAnn.getValue("basePath");
			if(baseUri!=null&&!baseUri.trim().isEmpty()){
				spec.getCoreRaml().setBaseUri(baseUri);
			}	
			
			String description = apiAnn.getValue("description");
			if(description!=null&&!description.trim().isEmpty()){
				DocumentationItem di = new DocumentationItem();
				di.setContent(description);
				di.setTitle("description");
				spec.getCoreRaml().setDocumentation(new ArrayList<DocumentationItem>(Arrays.asList(di)));				
			}
			
			String producesString = apiAnn.getValue(PRODUCES.toLowerCase());
			if(producesString!=null&&!producesString.isEmpty()){
				classProduces = producesString.split(",");
				for(int i = 0 ; i < classProduces.length ; i++){
					classProduces[i] = classProduces[i].trim();
				}
			}
			
			String consumesString = apiAnn.getValue(CONSUMES.toLowerCase());
			if(consumesString!=null&&!consumesString.isEmpty()){
				classConsumes = consumesString.split(",");
				for(int i = 0 ; i < classConsumes.length ; i++){
					classConsumes[i] = classConsumes[i].trim();
				}
			}
		}
		if(classConsumes==null||classConsumes.length==0){
			classConsumes = t.getAnnotationValues(CONSUMES);
		}
		if(classProduces==null||classProduces.length==0){
			classProduces = t.getAnnotationValues(PRODUCES);
		}
		
		String annotationValue = t.getAnnotationValue(PATH);		
		if (basePath != null) {
			if (annotationValue == null) {
				annotationValue = ""; //$NON-NLS-1$
			}
			annotationValue = basePath + annotationValue;
		}
		if (annotationValue != null) {
			if (!annotationValue.endsWith("/")) { //$NON-NLS-1$
				annotationValue = annotationValue + "/"; //$NON-NLS-1$
			}
			IMethodModel[] methods = t.getMethods();
			for (IMethodModel m : methods) {
				visit(m, annotationValue);
			}
		}

	}

	/**
	 * <p>generateXMLSchema.</p>
	 *
	 * @param t a {@link com.mulesoft.jaxrs.raml.annotation.model.ITypeModel} object.
	 */
	protected void generateXMLSchema(ITypeModel t, String collectionTag){
			
	}
	
	/**
	 * <p>generateXMLExampleJAXB.</p>
	 *
	 * @param t a {@link com.mulesoft.jaxrs.raml.annotation.model.ITypeModel} object.
	 * @return a {@link java.lang.String} object.
	 */
	protected String generateXMLExampleJAXB(ITypeModel t){
		JAXBRegistry rs=new JAXBRegistry();
		JAXBType jaxbModel = rs.getJAXBModel(t);
		if (jaxbModel!=null){
//			XMLWriter writer = new XMLWriter();
//			ExampleGenerator gen=new ExampleGenerator(writer);
//			gen.generateXML(jaxbModel);
//			return writer.toString();
			ISchemaType model = new SchemaModelBuilder().buildSchemaModel(jaxbModel);
			return new XMLModelSerializer().serialize(model);
		}
		return null;
	}

	class StringHolder {
		String content;
	}
	
	

	/**
	 * <p>getRaml.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getRaml() {
		spec.optimize();
		RamlEmitterV2 emmitter = new RamlEmitterV2();
		emmitter.setSingle(false);
		final StringHolder holder = new StringHolder();
		emmitter.dump(new IRamlHierarchyTarget() {

			public void write(String path, String content) {

			}

			public void writeRoot(String content) {
				holder.content = content;
			}

		}, spec.getCoreRaml());
		return holder.content;
	}

	private void visit(IMethodModel m, String path) {
		boolean hasPath = m.hasAnnotation(PATH);
		if (hasPath) {
			String localPath = m.getAnnotationValue(PATH);
			if (path.endsWith("/")) { //$NON-NLS-1$
				if (localPath.startsWith("/")) { //$NON-NLS-1$
					localPath = localPath.substring(1);
				}
			}

			path += localPath;
		}
		
		boolean isWs = hasPath;
		for (ActionType q : ActionType.values()) {
			boolean hasAnnotation = m.hasAnnotation(q.name());
			isWs |= hasAnnotation;
		}
		if (isWs) {
			Resource res = new Resource();
			IDocInfo documentation = getDocumentation(m);

			String returnName = null;
			String parameterName = null;

			ITypeModel returnedType = m.getReturnedType();

			if (returnedType != null) {
				if (returnedType.hasAnnotation(XML_ROOT_ELEMENT)) {
					generateXMLSchema(returnedType,null);
					returnName = returnedType.getName().toLowerCase();
				}
				if (hasPath) {
					if (consumedTypes.add(returnedType)) {
						ResourceVisitor resourceVisitor = createResourceVisitor();
						resourceVisitor.consumedTypes
								.addAll(this.consumedTypes);
						resourceVisitor.basePath = path;
						resourceVisitor.spec = this.spec;
						resourceVisitor.visit(returnedType);
					}
				}
			}
			ITypeModel bodyType = m.getBodyType();
			if (bodyType != null) {
				if (bodyType.hasAnnotation(XML_ROOT_ELEMENT)) {
					generateXMLSchema(bodyType,null);
					parameterName = bodyType.getName().toLowerCase();
				}
			}
			if (path.endsWith("/")) { //$NON-NLS-1$
				res.setRelativeUri(path.substring(0,
						path.length() - 1));
			} else {
				res.setRelativeUri(path);
			}
			for (ActionType q : ActionType.values()) {
				boolean hasAnnotation = m.hasAnnotation(q.name());
				if (hasAnnotation) {
					addMethod(q, res, m, documentation, returnName,
							parameterName);
				}
			}
			spec.addResource(res);
		}
	}

	

	

	private IDocInfo getDocumentation(IMethodModel m) {
		
		final IDocInfo basicDocInfo = m.getBasicDocInfo();
		
		String docString = basicDocInfo.getDocumentation();
		
		if(docString != null&&!docString.trim().isEmpty()){
			return basicDocInfo;
		}
		
		IAnnotationModel apiOperation = m.getAnnotation(API_OPERATION);
		if (apiOperation != null) {
			StringBuilder bld = new StringBuilder();

			String summary = apiOperation.getValue("value");
			if (summary != null) {
				bld.append(summary.trim());
			}

			String notes = apiOperation.getValue("notes");
			if (notes != null) {
				if (bld.length() != 0) {
					bld.append("\r\n");
				}
				bld.append(notes);
			}
			docString = bld.toString();
		}
		
		if(docString!=null&&!docString.isEmpty()){
			final String finalDocumentation = docString;
			return new IDocInfo() {
				
				@Override
				public String getReturnInfo() {
					return basicDocInfo.getReturnInfo();
				}
				
				@Override
				public String getDocumentation(String pName) {
					return basicDocInfo.getDocumentation(pName);
				}
				
				@Override
				public String getDocumentation() {
					return finalDocumentation;
				}
			};
		}
		return basicDocInfo;
	}

	/**
	 * <p>createResourceVisitor.</p>
	 *
	 * @return a {@link com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor} object.
	 */
	protected abstract ResourceVisitor createResourceVisitor();

	private void addMethod(ActionType actionType, Resource res, IMethodModel m,
			IDocInfo documentation, String returnName, String parameterName) {
		
		Action action = new Action();
		String description = documentation.getDocumentation();
		if (!"".equals(description)) { //$NON-NLS-1$
			action.setDescription(description);
		}
		
		ActionType adjustedActionType = adjustActionType(m,actionType);		
		action.setType(adjustedActionType);		
		res.getActions().put(adjustedActionType, action);
		
		processResponses(m,action,documentation,returnName);
		
		IParameterModel[] parameters = m.getParameters();
		for (IParameterModel pm : parameters) {
			if (pm.hasAnnotation(QUERY_PARAM)) {
				IAnnotationModel paramAnnotation = pm.getAnnotation(QUERY_PARAM);
				QueryParameter value2 = new QueryParameter();
				String paramName = configureParam(pm, value2, documentation,paramAnnotation);
				action.getQueryParameters().put(paramName, value2);
			}
		}
		for (IParameterModel pm : parameters) {
			if (pm.hasAnnotation(HEADER_PARAM)) {
				IAnnotationModel paramAnnotation = pm.getAnnotation(HEADER_PARAM);
				Header value2 = new Header();
				String paramName = configureParam(pm, value2, documentation,paramAnnotation);
				action.getHeaders().put(paramName, value2);
			}
		}
		for (IParameterModel pm : parameters) {
			if (pm.hasAnnotation(PATH_PARAM)) {
				IAnnotationModel paramAnnotation = pm.getAnnotation(PATH_PARAM);
				UriParameter value2 = new UriParameter();
				String paramName = configureParam(pm, value2, documentation,paramAnnotation);
				res.getUriParameters().put(paramName, value2);
			}
		}

		boolean hasBody = m.getBodyType()!=null;
		String[] consumesValue = extractMediaTypes(m, CONSUMES, classConsumes, hasBody,adjustedActionType);
		if (consumesValue != null) {
			for (String s : consumesValue) {
				s = sanitizeMediaType(s);
				MimeType bodyType = new MimeType();
				if (s.contains(XML)) {
					bodyType.setSchema(parameterName);
					if (parameterName!=null){
						bodyType.setExample(EXAMPLES_PREFFIX + parameterName + XML_FILE_EXT);
						bodyType.setExampleOrigin(EXAMPLES_PREFFIX + parameterName
								+ XML_FILE_EXT);
					}
				}
				if (s.contains(JSON)) {
					if (parameterName!=null){
						bodyType.setSchema(parameterName + ResourceVisitor.JSONSCHEMA); //$NON-NLS-1$
						bodyType.setExample(EXAMPLES_PREFFIX + parameterName + JSON_FILE_EXT);
						bodyType.setExampleOrigin(EXAMPLES_PREFFIX + parameterName
							+ JSON_FILE_EXT);
					}

				}
				bodyType.setType(s);
				if (s.contains(FORM)) {
					for (IParameterModel pm : parameters) {
						if (pm.hasAnnotation(FORM_PARAM)) {
							IAnnotationModel paramAnnotation = pm.getAnnotation(FORM_PARAM);							
							FormParameter vl = new FormParameter();
							String paramName = configureParam(pm,vl,documentation,paramAnnotation);							
							ArrayList<FormParameter> arrayList = new ArrayList<FormParameter>();
							arrayList.add(vl);
							if (bodyType.getFormParameters() == null) {
								bodyType.setFormParameters(new HashMap<String, java.util.List<FormParameter>>());
							}
							bodyType.getFormParameters().put(paramName,	arrayList);
						}
					}
				}
				action.getBody().put(s, bodyType);
			}
		}
	}
	
	
	private void processResponses(IMethodModel m, Action action, IDocInfo documentation, String returnName) {
		
		HashMap<String, ResponseModel> responses = new HashMap<String, ResponseModel>();
		String mainResponseCode = DEFAULT_RESPONSE;
		if(config != null){
			ActionType actionType = action.getType();
			mainResponseCode = config.getResponseCode(actionType);
		}
		
		ResponseModel mainResponse = new ResponseModel(mainResponseCode, null, returnName);
		responses.put(mainResponseCode, mainResponse);
		
		IAnnotationModel apiResponse = m.getAnnotation(ResourceVisitor.API_RESPONSE);
		if(apiResponse!=null){
			String code = apiResponse.getValue(ResourceVisitor.CODE);
			String message = apiResponse.getValue(ResourceVisitor.MESSAGE);
			ResponseModel response = new ResponseModel(code, message, returnName);
			responses.put(code, response);
		}
		
		IAnnotationModel apiResponses = m.getAnnotation(ResourceVisitor.API_RESPONSES);
		if (apiResponses!=null)
		{
			IAnnotationModel[] subAnnotations = apiResponses.getSubAnnotations("value");
			if (subAnnotations!=null){				
				for (IAnnotationModel subAnn:subAnnotations){
					String code = subAnn.getValue(ResourceVisitor.CODE);
					String message = subAnn.getValue(ResourceVisitor.MESSAGE);
					
					String adjustedReturnName = returnName;
					String responseQualifiedName = subAnn.getValue(RESPONSE);					
					if(responseQualifiedName!=null){
						try {
							Class<?> responseClass = classLoader.loadClass(responseQualifiedName);
							ReflectionType rt = new ReflectionType(responseClass); 
							generateXMLSchema(rt,null);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
						adjustedReturnName = getSimpleName(responseQualifiedName).toLowerCase();
					}
					ResponseModel response = responses.get(code);
					if(response==null){
						response = new ResponseModel(code, message, adjustedReturnName);
						responses.put(code, response);
					}
					else{
						response.setMessage(message);
						response.setReturnTypeName(adjustedReturnName);
					}
				}
			}
		}

		boolean returnsValue = !m.getReturnedType().getName().toLowerCase().equals("void");
		String[] producesValues = extractMediaTypes(m, PRODUCES, classProduces, returnsValue, null);
		if(producesValues!=null){
			for (ResponseModel responseModel : responses.values()){
				responseModel.setProduces(producesValues);
			}
		}
		
		IAnnotationModel apiOperation = m.getAnnotation(API_OPERATION);
		if(apiOperation!=null){
			String responseContainer = apiOperation.getValue("responseContainer");
			String responseQualifiedName = apiOperation.getValue(RESPONSE);
			if(responseQualifiedName!=null){
				try {
					Class<?> responseClass = classLoader.loadClass(responseQualifiedName);
					ReflectionType rt = new ReflectionType(responseClass); 
					generateXMLSchema(rt,responseContainer);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}	
				String adjustedReturnType = getSimpleName(responseQualifiedName).toLowerCase();
				mainResponse.setReturnTypeName(adjustedReturnType);
			}
		}
		
		
		for(ResponseModel rm : responses.values()){
			
			Response response = new Response();
			
			String description = rm.getMessage();
			if(description==null||description.trim().isEmpty()){
				description = documentation.getReturnInfo();
			}
			if(description!=null&&!description.trim().isEmpty()){
				response.setDescription(description);
			}
			
			String[] produces = rm.getProduces();
			if(produces!=null){
				
				String returnTypeName = rm.getReturnTypeName();
				for (String mediaType : producesValues) {
					mediaType = sanitizeMediaType(mediaType);
					MimeType mimeType = new MimeType();
					if (returnTypeName != null) {
						if (mediaType.contains(XML)) {
							mimeType.setSchema(returnTypeName);
							mimeType.setExample(EXAMPLES_PREFFIX + returnTypeName + XML_FILE_EXT);
							mimeType.setExampleOrigin(EXAMPLES_PREFFIX + returnTypeName	+ XML_FILE_EXT);
						}
						if (mediaType.contains(JSON)) {
							mimeType.setSchema(returnTypeName + ResourceVisitor.JSONSCHEMA); //$NON-NLS-1$
							mimeType.setExample(EXAMPLES_PREFFIX + returnTypeName + JSON_FILE_EXT);
							mimeType.setExampleOrigin(EXAMPLES_PREFFIX + returnTypeName	+ JSON_FILE_EXT);
						}
					}
					mimeType.setType(mediaType);
					response.getBody().put(mediaType, mimeType);
				}
			}
			
			String code = rm.getCode();
			action.getResponses().put(code, response);
		}
	}

	
	private String[] extractMediaTypes(IMethodModel m, String annotationName, String[] defaultValues, boolean useDefault, ActionType at) {
		
		useDefault = at != ActionType.GET;
		
		IAnnotationModel apiOperation1 = m.getAnnotation(API_OPERATION);		
		String[] values = null;
		
		if(apiOperation1!=null){
			String value = apiOperation1.getValue(annotationName.toLowerCase());
			if(value!=null){
				values = value.split(",");
				for(int i = 0 ; i < values.length ;i++){
					values[i] = values[i].trim();
				}
			}
		}
		if(values==null){	
			values = m.getAnnotationValues(annotationName);
		}
		if(values!=null){
			return values;
		}
		if(!useDefault){
			return null;
		}
		if (values == null) {
			values = defaultValues;
		}
		return values;
	}

	private String getSimpleName(String qName) {
		
		int ind = qName.lastIndexOf('.');
		String simpleName = qName.substring(ind+1);
		return simpleName;
	}

	private ActionType adjustActionType(IMethodModel m, ActionType actionType) {
		
		IAnnotationModel a = m.getAnnotation(API_OPERATION);
		if(a==null){
			return actionType;
		}
		
		String atString = a.getValue("httpMethod");
		if(atString==null||atString.trim().isEmpty()){
			return actionType;
		}
		
		ActionType adjustedActionType = null;
		try{
			adjustedActionType = Enum.valueOf(ActionType.class, atString);
		}
		catch(Exception e){
			return actionType;
		}		
		return adjustedActionType;
	}


	private String configureParam(IParameterModel model, AbstractParam param, IDocInfo documentation, IAnnotationModel paramAnnotation) {
		
		String paramName = paramAnnotation.getValue("value");
		
		String type = model.getType();
		proceedType(type, param, model);
		String text = documentation.getDocumentation(model
				.getName());
		if (!"".equals(text)) { //$NON-NLS-1$
			param.setDescription(text);
		}
		
		if (model.hasAnnotation("NotNull")) { //$NON-NLS-1$
			param.setRequired(true);
		}
		if (model.hasAnnotation("Pattern")) { //$NON-NLS-1$
			IAnnotationModel annotation = model.getAnnotation("Pattern"); //$NON-NLS-1$
			String pattern = annotation.getValue("regexp"); //$NON-NLS-1$
			param.setPattern(pattern);
		}
		if (model.hasAnnotation("Min")) { //$NON-NLS-1$
			String min = model.getAnnotationValue("Min"); //$NON-NLS-1$
			param.setMinimum(BigDecimal.valueOf(Double.parseDouble(min)));
		}
		if (model.hasAnnotation("DecimalMin")) { //$NON-NLS-1$
			String min = model.getAnnotationValue("DecimalMin"); //$NON-NLS-1$
			param.setMinimum(BigDecimal.valueOf(Double.parseDouble(min)));
		}
		if (model.hasAnnotation("Max")) { //$NON-NLS-1$
			String max = model.getAnnotationValue("Max"); //$NON-NLS-1$
			param.setMaximum(BigDecimal.valueOf(Double.parseDouble(max)));
		}
		if (model.hasAnnotation("DecimalMax")) { //$NON-NLS-1$
			String max = model.getAnnotationValue("DecimalMax"); //$NON-NLS-1$
			param.setMaximum(BigDecimal.valueOf(Double.parseDouble(max)));
		}
		if(model.hasAnnotation("ApiParam")){
			IAnnotationModel ann = model.getAnnotation("ApiParam");
			String allowableValues = ann.getValue("allowableValues");
			if(allowableValues!=null&&!allowableValues.trim().isEmpty()){
				int start = 0;
				int end = allowableValues.length();
				if(allowableValues.startsWith("[")){
					start++;
				}
				if(allowableValues.endsWith("]")){
					end--;
				}
				allowableValues = allowableValues.substring(start, end);
				String[] split = allowableValues.split(",");
				ArrayList<String> list = new ArrayList<String>();
				for(String s : split){
					list.add(s.trim());
				}
				param.setEnumeration(list);
			}
			String allowMultiple = ann.getValue("allowMultiple");
			if(allowMultiple!=null){
				boolean boolValue = Boolean.parseBoolean(allowMultiple);
				param.setRepeat(boolValue);
			}
			String defaultValue = ann.getValue("defaultValue");
			if(defaultValue!=null&&!defaultValue.trim().isEmpty()){
				param.setDefaultValue(defaultValue.trim());
			}			
			String required = ann.getValue("required");
			if(required!=null&&!required.trim().isEmpty()){
				boolean boolValue = Boolean.parseBoolean(required);
				param.setRequired(boolValue);
			}
			String description = ann.getValue("value");
			if(description!=null&&!description.trim().isEmpty()){
				param.setDescription(description);
			}
			String overridenName = ann.getValue("name");
			if(overridenName!=null&&!overridenName.trim().isEmpty()){
				paramName = overridenName;
			}
		}
		return paramName;
	}

	private String sanitizeMediaType(String s) {
		s = s.toLowerCase();
		if (s.contains(FORM)) {
			if (s.contains("urlencoded")) { //$NON-NLS-1$
				s = "application/x-www-form-urlencoded"; //$NON-NLS-1$
			}
			if (s.contains("multipart")) { //$NON-NLS-1$
				s = "multipart/form-data"; //$NON-NLS-1$
			}
		}
		if (s.contains("text")) { //$NON-NLS-1$
			if (s.contains("html")) { //$NON-NLS-1$
				s = "text/html"; //$NON-NLS-1$
			}
			if (s.contains("plain")) { //$NON-NLS-1$
				s = "text/plain"; //$NON-NLS-1$
			}
		}
		if (s.contains("octet")) { //$NON-NLS-1$
			return "application/octet-stream"; //$NON-NLS-1$
		}
		if (s.contains(XML)) { //$NON-NLS-1$
			s = "application/xml"; //$NON-NLS-1$
		}
		if (s.contains(JSON)) { //$NON-NLS-1$
			s = "application/json"; //$NON-NLS-1$
		}
		return s;
	}

	private void proceedType(String type, AbstractParam value2,
			IParameterModel param) {
		String annotationValue = param.getAnnotationValue(DEFAULT_VALUE);
		boolean hasDefault = false;
		if (annotationValue != null) {
			value2.setDefaultValue(annotationValue);
			hasDefault = true;
		}
		if (type.equals("I")) { //$NON-NLS-1$
			value2.setType(ParamType.INTEGER);
			value2.setRequired(!hasDefault);
		}
		if (type.equals("D")) { //$NON-NLS-1$
			value2.setType(ParamType.NUMBER);
			value2.setRequired(!hasDefault);
		}
		if (type.equals("Z")) { //$NON-NLS-1$
			value2.setType(ParamType.BOOLEAN);
		}
		if (type.equals("int") || type.equals("long") || type.equals("short")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			value2.setType(ParamType.INTEGER);
			value2.setRequired(!hasDefault);
		}
		if (type.equals("float") || type.equals("double")) { //$NON-NLS-1$ //$NON-NLS-2$
			value2.setType(ParamType.NUMBER);
			value2.setRequired(!hasDefault);
		}
		if (type.equals("boolean")) { //$NON-NLS-1$
			value2.setType(ParamType.BOOLEAN);
			value2.setRequired(!hasDefault);
		}
		if (type.equals("QInteger;")) { //$NON-NLS-1$
			value2.setType(ParamType.INTEGER);
		}
		if (type.equals("QDouble;")) { //$NON-NLS-1$
			value2.setType(ParamType.NUMBER);
		}
		if (type.equals("QBoolean;")) { //$NON-NLS-1$
			value2.setType(ParamType.BOOLEAN);
			value2.setRequired(!hasDefault);
		}
		if (type.equals("java.lang.Integer") || type.equals("java.lang.Long") || type.equals("java.lang.Short")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			value2.setType(ParamType.INTEGER);
		}
		if (type.equals("java.lang.Float") || type.equals("java.lang.Double")) { //$NON-NLS-1$ //$NON-NLS-2$
			value2.setType(ParamType.NUMBER);
		}
		if (type.equals("java.lang.Boolean")) { //$NON-NLS-1$
			value2.setType(ParamType.BOOLEAN);
		}
	}

	/**
	 * <p>generateXSDForClass.</p>
	 *
	 * @param element a {@link java.lang.Class} object.
	 */
	protected String generateXSDForClass(Class<?> element) {
		try {
			String name = element.getSimpleName().toLowerCase();
			String fileName = name + ".xsd"; //$NON-NLS-1$
			JAXBContext jaxbContext = JAXBContext.newInstance(element);
			CustomSchemaOutputResolver sor = new CustomSchemaOutputResolver(
					fileName);
			jaxbContext.generateSchema(sor);
			File file = sor.getFile();
			String content = FileUtil.fileToString(file);
			generateExamle(file, content);
			spec.getCoreRaml().addGlobalSchema(name, content, false, false);
			return content;
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>clear.</p>
	 */
	public void clear() {
		spec.coreRaml=new Raml2();
		spec.coreRaml.setBaseUri("http://example.com"); //$NON-NLS-1$
		spec.coreRaml.setTitle("Please type API title here"); //$NON-NLS-1$
		spec.coreRaml.setProtocols(Collections.singletonList(Protocol.HTTP));
	}

	/**
	 * <p>isEmpty.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isEmpty() {
		return spec.coreRaml.getResources().isEmpty();
	}

	/**
	 * <p>doGenerateAndSave.</p>
	 *
	 * @param schemaFile a {@link java.io.File} object.
	 * @param parentDir a {@link java.io.File} object.
	 * @param examplesDir a {@link java.io.File} object.
	 * @param dummyXml a {@link java.lang.String} object.
	 */
	protected void doGenerateAndSave(File schemaFile, File parentDir,
			File examplesDir, String dummyXml) {

		String jsonText = JsonUtil.convertToJSON(dummyXml, true);
		jsonText = JsonFormatter.format(jsonText);	
		String fName = schemaFile.getName().replace(XML_FILE_EXT,ResourceVisitor.JSONSCHEMA); //$NON-NLS-1$
		fName = fName.replace(".xsd", ResourceVisitor.JSONSCHEMA);
		
		String generatedSchema = jsonText != null ? new SchemaGenerator().generateSchema(jsonText) : null;
		generatedSchema = generatedSchema != null ? JsonFormatter.format(generatedSchema) : null;
		if(generatedSchema != null){
			spec.getCoreRaml().addGlobalSchema(fName, generatedSchema, true, false);
		}
		String name = schemaFile.getName();
		name = name.substring(0, name.lastIndexOf('.'));
		File toSave = new File(examplesDir, name + XML_FILE_EXT);
		writeString(dummyXml, toSave);		
		toSave = new File(examplesDir, name + JSON_FILE_EXT);
		if(jsonText != null){
			writeString(jsonText, toSave);
		}
		File shemas = new File(parentDir, SCHEMAS_FOLDER);
		toSave = new File(shemas, fName + JSON_FILE_EXT);
		if(generatedSchema != null){
			writeString(generatedSchema, toSave);
		}
	}

	/**
	 * <p>writeString.</p>
	 *
	 * @param generateDummyXmlFor a {@link java.lang.String} object.
	 * @param toSave a {@link java.io.File} object.
	 */
	protected void writeString(String generateDummyXmlFor, File toSave) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(toSave);
			fileOutputStream.write(generateDummyXmlFor.getBytes("UTF-8")); //$NON-NLS-1$
			fileOutputStream.close();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * <p>generateExamle.</p>
	 *
	 * @param schemaFile a {@link java.io.File} object.
	 * @param content a {@link java.lang.String} object.
	 */
	protected void generateExamle(File schemaFile, String content) {
		/*if (schemaFile != null) {
			File examplesDir = schemaFile.getParentFile();
			if (examplesDir != null
					&& examplesDir.getName().endsWith(SCHEMAS_FOLDER)) {
				examplesDir = new File(examplesDir.getParent(), EXAMPLES_FOLDER);
				examplesDir.mkdirs();
				org.apache.xerces.xs.XSModel xsModel = new XSParser().parse(schemaFile.getAbsolutePath());

				XSInstance xsInstance = new XSInstance();
				xsInstance.minimumElementsGenerated = 2;
				xsInstance.maximumElementsGenerated = 4;
				xsInstance.generateOptionalElements = Boolean.TRUE; // null means
																	// random

				List<XSElementDeclaration> elements = XSUtil.guessRootElements(xsModel);
				if (elements.size() == 0) {
					System.err.println("no elements found in given xml schema: "
							+ schemaFile.getName());
					return;
				} else {
					try {
						File toSave = new File(examplesDir, schemaFile.getName());

						XSElementDeclaration elem = elements.get(0);
						javax.xml.namespace.QName rootElement = XSUtil.getQName(elem,new MyNamespaceSupport());
						StringWriter writer = new StringWriter();
						XMLDocument sampleXml = new XMLDocument(new StreamResult(
								writer), true, 4, null);
						xsInstance.generate(xsModel, rootElement, sampleXml);
						doGenerateAndSave(toSave, schemaFile.getParentFile().getParentFile(), examplesDir, 
								writer.toString());
					} catch (TransformerConfigurationException e) {
						throw new IllegalStateException(e);
					} catch (Exception e) {
						throw new IllegalStateException(e.getMessage(), e);
					}
				}
			}
		}*/
		/*String dummyXml = new XSDUtil().instantiateToString(schemaFile.getAbsolutePath(),null);
		doGenerateAndSave(schemaFile, examplesDir.getParentFile(), examplesDir, dummyXml);*/
		return;
	}

	/**
	 * <p>setPreferences.</p>
	 *
	 * @param preferencesConfig a {@link com.mulesoft.jaxrs.raml.annotation.model.IRamlConfig} object.
	 */
	public void setPreferences(IRamlConfig preferencesConfig) {
		this.config=preferencesConfig;
		if (preferencesConfig.getTitle()!=null&&preferencesConfig.getTitle().length()>0){
			spec.getCoreRaml().setTitle(preferencesConfig.getTitle());
		}
		if (preferencesConfig.getVersion()!=null&&preferencesConfig.getVersion().length()>0){
			spec.getCoreRaml().setVersion(preferencesConfig.getVersion());
		}
		if (preferencesConfig.getBaseUrl()!=null&&preferencesConfig.getBaseUrl().length()>0){
			spec.getCoreRaml().setBaseUri(preferencesConfig.getBaseUrl());
		}
		if (preferencesConfig.getProtocols()!=null) {
			ArrayList<Protocol> protocols = new ArrayList<Protocol>(preferencesConfig.getProtocols());
			Collections.sort(protocols);
			spec.getCoreRaml().setProtocols(protocols);
		}
		spec.doSort=preferencesConfig.isSorted();
		spec.extractCommonParts=preferencesConfig.doFullTree();
	}
	
	private static class ResponseModel{
		
		public ResponseModel(String code, String message, String returnTypeName) {
			super();
			this.code = code;
			this.message = message;
			this.returnTypeName = returnTypeName;
		}

		private String code;
		
		private String message;
		
		private String returnTypeName;
		
		private String produces[];

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getReturnTypeName() {
			return returnTypeName;
		}

		public void setReturnTypeName(String returnTypeName) {
			this.returnTypeName = returnTypeName;
		}

		public String[] getProduces() {
			return produces;
		}

		public void setProduces(String[] produces) {
			this.produces = produces;
		}
	}
}
