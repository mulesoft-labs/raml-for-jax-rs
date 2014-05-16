package com.mulesoft.jaxrs.raml.annotation.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.dynvocation.lib.xsd4j.XSDUtil;
import org.raml.emitter.IRamlHierarchyTarget;
import org.raml.emitter.RamlEmitterV2;
import org.raml.model.Action;
import org.raml.model.ActionType;
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

import com.mulesoft.jaxrs.raml.jsonschema.JsonFormatter;
import com.mulesoft.jaxrs.raml.jsonschema.JsonUtil;
import com.mulesoft.jaxrs.raml.jsonschema.SchemaGenerator;

public abstract class ResourceVisitor {

	private static final String JSONSCHEMA = "-jsonschema";

	protected static final String XML_FILE_EXT = ".xml"; //$NON-NLS-1$

	private static final String JSON_FILE_EXT = ".json"; //$NON-NLS-1$
	
	protected static final String SCHEMAS_FOLDER = "schemas"; //$NON-NLS-1$

	protected static final String EXAMPLES_FOLDER = "examples"; //$NON-NLS-1$
	
	protected static final String EXAMPLES_PREFFIX = EXAMPLES_FOLDER + "/"; //$NON-NLS-1$

	private static final String JSON = "json"; //$NON-NLS-1$

	private static final String XML = "xml"; //$NON-NLS-1$

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

	protected RAMLModelHelper spec = new RAMLModelHelper();

	protected String[] classConsumes;
	protected String[] classProduces;

	protected HashSet<ITypeModel> consumedTypes = new HashSet<ITypeModel>();

	private String basePath;

	protected final File outputFile;

	protected final ClassLoader classLoader;

	private IRamlConfig config;

	public ResourceVisitor(File outputFile, ClassLoader classLoader) {
		this.outputFile = outputFile;
		this.classLoader = classLoader;
	}

	public void visit(ITypeModel t) {
		consumedTypes.add(t);
		classConsumes = t.getAnnotationValues(CONSUMES);
		classProduces = t.getAnnotationValues(PRODUCES);
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

	protected abstract void generateXMLSchema(ITypeModel t);

	class StringHolder {
		String content;
	}
	
	

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
			IDocInfo documentation = m.getBasicDocInfo();
			String text = documentation.getDocumentation();
			if (!"".equals(text)) { //$NON-NLS-1$
				res.setDescription(text);
			}
			String returnName = null;
			String parameterName = null;

			ITypeModel returnedType = m.getReturnedType();

			if (returnedType != null) {
				if (returnedType.hasAnnotation(XML_ROOT_ELEMENT)) {
					generateXMLSchema(returnedType);
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
					generateXMLSchema(bodyType);
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

	

	

	protected abstract ResourceVisitor createResourceVisitor();

	private void addMethod(ActionType q, Resource res, IMethodModel m,
			IDocInfo documentation, String returnName, String parameterName) {
		Action value = new Action();
		value.setType(q);
		res.getActions().put(q, value);
		IParameterModel[] parameters = m.getParameters();

		for (IParameterModel pm : parameters) {
			if (pm.hasAnnotation(QUERY_PARAM)) {
				String annotationValue = pm.getAnnotationValue(QUERY_PARAM);
				String type = pm.getType();
				QueryParameter value2 = new QueryParameter();
				configureParam(pm, value2);
				proceedType(type, value2, pm);
				String text = documentation.getDocumentation(pm
						.getName());
				if (!"".equals(text)) { //$NON-NLS-1$
					value2.setDescription(text);
				}
				value.getQueryParameters().put(annotationValue, value2);
			}
		}
		for (IParameterModel pm : parameters) {
			if (pm.hasAnnotation(HEADER_PARAM)) {
				String annotationValue = pm.getAnnotationValue(HEADER_PARAM);
				Header value2 = new Header();
				configureParam(pm, value2);
				proceedType(pm.getType(), value2, pm);
				String text = documentation.getDocumentation(pm
						.getName());
				if (!"".equals(text)) { //$NON-NLS-1$
					value2.setDescription(text);
				}
				value.getHeaders().put(annotationValue, value2);
			}
		}
		for (IParameterModel pm : parameters) {
			if (pm.hasAnnotation(PATH_PARAM)) {
				String annotationValue = pm.getAnnotationValue(PATH_PARAM);
				UriParameter value2 = new UriParameter();
				configureParam(pm, value2);
				String text = documentation.getDocumentation(pm
						.getName());
				if (!"".equals(text)) { //$NON-NLS-1$
					value2.setDescription(text);
				}
				proceedType(pm.getType(), value2, pm);
				res.getUriParameters().put(annotationValue, value2);
			}
		}

		String[] consumesValue = m.getAnnotationValues(CONSUMES);
		if (consumesValue == null) {
			consumesValue = classConsumes;
		}
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
					bodyType.setSchema(parameterName + ResourceVisitor.JSONSCHEMA); //$NON-NLS-1$
					if (parameterName!=null){
						bodyType.setExample(EXAMPLES_PREFFIX + returnName + JSON_FILE_EXT);
						bodyType.setExampleOrigin(EXAMPLES_PREFFIX + returnName
							+ JSON_FILE_EXT);
					}

				}
				bodyType.setType(s);
				if (s.contains(FORM)) {
					for (IParameterModel pm : parameters) {
						if (pm.hasAnnotation(FORM_PARAM)) {
							String annotationValue = pm
									.getAnnotationValue(FORM_PARAM);
							FormParameter vl = new FormParameter();
							configureParam(pm,vl);
							String text = documentation.getDocumentation(pm
									.getName());
							if (!"".equals(text)) { //$NON-NLS-1$
								vl.setDescription(text);
							}
							proceedType(pm.getType(), vl, pm);
							ArrayList<FormParameter> arrayList = new ArrayList<FormParameter>();
							arrayList.add(vl);
							if (bodyType.getFormParameters() == null) {
								bodyType.setFormParameters(new HashMap<String, java.util.List<FormParameter>>());
							}
							bodyType.getFormParameters().put(annotationValue,
									arrayList);
						}
					}
				}
				value.getBody().put(s, bodyType);
			}
		}
		String[] producesValue = m.getAnnotationValues(PRODUCES);
		if (producesValue == null) {
			producesValue = classProduces;
		}
		if (producesValue != null) {
			Response value2 = new Response();
			String text = documentation.getReturnInfo();
			if (!"".equals(text)) { //$NON-NLS-1$
				value2.setDescription(text);
			}
			for (String s : producesValue) {
				s = sanitizeMediaType(s);
				MimeType mimeType = new MimeType();
				if (returnName != null) {
					if (s.contains(XML)) {
						mimeType.setSchema(returnName);
						if (returnName!=null){
							mimeType.setExample(EXAMPLES_PREFFIX + returnName + XML_FILE_EXT);
							mimeType.setExampleOrigin(EXAMPLES_PREFFIX + returnName
								+ XML_FILE_EXT);
						}
					}
					if (s.contains(JSON)) {
						mimeType.setSchema(returnName + ResourceVisitor.JSONSCHEMA); //$NON-NLS-1$
						if (returnName!=null){
							mimeType.setExample(EXAMPLES_PREFFIX + returnName + JSON_FILE_EXT);
							mimeType.setExampleOrigin(EXAMPLES_PREFFIX + returnName
								+ JSON_FILE_EXT);
						}
					}
				}
				mimeType.setType(s);
				value2.getBody().put(s, mimeType);

			}
			value.getResponses().put("200", value2); //$NON-NLS-1$
		} else {
			Response value2 = new Response();
			String text = documentation.getReturnInfo();
			if (!"".equals(text)) { //$NON-NLS-1$
				value2.setDescription(text);
			}
			value.getResponses().put("200", value2); //$NON-NLS-1$
		}
	}

	private void configureParam(IParameterModel model, AbstractParam param) {
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
	}

	protected void generateXSDForClass(Class<?> element) {
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
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void clear() {
		spec.coreRaml=new Raml2();
		spec.coreRaml.setBaseUri("http://example.com"); //$NON-NLS-1$
		spec.coreRaml.setTitle("Please type API title here"); //$NON-NLS-1$
		spec.coreRaml.setProtocols(Collections.singletonList(Protocol.HTTP));
	}

	public boolean isEmpty() {
		return spec.coreRaml.getResources().isEmpty();
	}

	protected void doGenerateAndSave(File schemaFile, File parentDir, File examplesDir,
			String dummyXml) {
				String jsonText = JsonUtil.convertToJSON(dummyXml, true);
				jsonText=JsonFormatter.format(jsonText);
				String generatedSchema = new SchemaGenerator().generateSchema(jsonText);
				generatedSchema=JsonFormatter.format(generatedSchema);
				String fName = schemaFile.getName().replace(XML_FILE_EXT, ResourceVisitor.JSONSCHEMA); //$NON-NLS-1$
				fName=fName.replace(".xsd",  ResourceVisitor.JSONSCHEMA);
				spec.getCoreRaml().addGlobalSchema(fName,generatedSchema, true,false);
				String name = schemaFile.getName();
				name=name.substring(0,name.lastIndexOf('.'));
				File toSave=new File(examplesDir,name+XML_FILE_EXT);
				writeString(dummyXml, toSave);
				toSave=new File(examplesDir,name+JSON_FILE_EXT);
				writeString(jsonText, toSave);
				File shemas=new File(parentDir,SCHEMAS_FOLDER);
				toSave=new File(shemas,fName+JSON_FILE_EXT);
				writeString(generatedSchema, toSave);
			}

	private void writeString(String generateDummyXmlFor, File toSave) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(toSave);
			fileOutputStream.write(generateDummyXmlFor.getBytes("UTF-8")); //$NON-NLS-1$
			fileOutputStream.close();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	protected void generateExamle(File schemaFile, String content) {
	
		File examplesDir = schemaFile.getParentFile();
		if (examplesDir.getName().endsWith(SCHEMAS_FOLDER)) { 
			examplesDir = new File(examplesDir.getParent(),EXAMPLES_FOLDER); 
			examplesDir.mkdirs();
		}
		String dummyXml = new XSDUtil().instantiateToString(schemaFile.getAbsolutePath(),null);
		doGenerateAndSave(schemaFile, examplesDir.getParentFile(), examplesDir, dummyXml);
	}

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
	}
}
