package com.mulesoft.jaxrs.raml.annotation.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import org.raml.model.MimeType;
import org.raml.model.ParamType;
import org.raml.model.Resource;
import org.raml.model.Response;
import org.raml.model.parameter.AbstractParam;
import org.raml.model.parameter.FormParameter;
import org.raml.model.parameter.Header;
import org.raml.model.parameter.QueryParameter;
import org.raml.model.parameter.UriParameter;

public abstract class ResourceVisitor {
	

	public class CustomSchemaOutputResolver extends SchemaOutputResolver {

	    private final String fileName;
		private File file;

		public CustomSchemaOutputResolver(String fileName) {
			this.fileName = fileName;
		}

		public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
			if (outputFile != null) {
				File dir = new File(outputFile.getParent(), "schemes"); //$NON-NLS-1$
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
	
	protected final IResourceVisitorFactory factory;

	protected RAMLModelHelper spec = new RAMLModelHelper();

	protected String[] classConsumes;
	protected String[] classProduces;

	protected HashSet<ITypeModel> consumedTypes = new HashSet<ITypeModel>();

	private String basePath;

	private final File outputFile;

	protected ClassLoader classLoader;
	
	public ResourceVisitor(IResourceVisitorFactory factory, File outputFile) {
		this.factory = factory;
		this.outputFile = outputFile;
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
		
		if (t.hasAnnotation(XML_ROOT_ELEMENT)) {
			generateXMLSchema(t);
		}
	}

	protected abstract void generateXMLSchema(ITypeModel t);

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
			if (annotationValue.endsWith("/")) { //$NON-NLS-1$
				if (annotationValue2.startsWith("/")) { //$NON-NLS-1$
					annotationValue2 = annotationValue2.substring(1);
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

			if (hasPath) {
				ITypeModel returnedType = m.getReturnedType();
				if (returnedType != null) {
					if (consumedTypes.add(returnedType)) {
						ResourceVisitor resourceVisitor = factory.createResourceVisitor();
						resourceVisitor.consumedTypes
								.addAll(this.consumedTypes);
						resourceVisitor.basePath = annotationValue;
						resourceVisitor.spec = this.spec;
						resourceVisitor.visit(returnedType);
					}
				}
			}
			if (annotationValue.endsWith("/")) { //$NON-NLS-1$
				res.setRelativeUri(annotationValue.substring(0,
						annotationValue.length() - 1));
			} else {
				res.setRelativeUri(annotationValue);
			}
			for (ActionType q : ActionType.values()) {
				boolean hasAnnotation = m.hasAnnotation(q.name());
				if (hasAnnotation) {
					addMethod(q, res, m, documentation);
				}
			}
			spec.addResource(res);

		}
	}

	private void addMethod(ActionType q, Resource res, IMethodModel m,
			IDocInfo documentation) {
		Action value = new Action();
		value.setType(q);
		res.getActions().put(q, value);
		IParameterModel[] parameters = m.getParameters();

		for (IParameterModel pm : parameters) {
			if (pm.hasAnnotation(QUERY_PARAM)) {
				String annotationValue = pm.getAnnotationValue(QUERY_PARAM);
				String type = pm.getType();
				QueryParameter value2 = new QueryParameter();
				proceedType(type, value2, pm);
				value2.setDescription(documentation.getDocumentation(pm
						.getName()));
				value.getQueryParameters().put(annotationValue, value2);
			}
		}
		for (IParameterModel pm : parameters) {
			if (pm.hasAnnotation(HEADER_PARAM)) {
				String annotationValue = pm.getAnnotationValue(HEADER_PARAM);
				Header value2 = new Header();
				proceedType(pm.getType(), value2, pm);
				value2.setDescription(documentation.getDocumentation(pm
						.getName()));
				value.getHeaders().put(annotationValue, value2);
			}
		}
		for (IParameterModel pm : parameters) {
			if (pm.hasAnnotation(PATH_PARAM)) {
				String annotationValue = pm.getAnnotationValue(PATH_PARAM);
				UriParameter value2 = new UriParameter();
				value2.setDescription(documentation.getDocumentation(pm
						.getName()));
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
				MimeType value2 = new MimeType();
				value2.setType(s);
				if (s.contains(FORM)) {
					for (IParameterModel pm : parameters) {
						if (pm.hasAnnotation(FORM_PARAM)) {
							String annotationValue = pm
									.getAnnotationValue(FORM_PARAM);
							FormParameter vl = new FormParameter();
							vl.setDescription(documentation.getDocumentation(pm
									.getName()));
							proceedType(pm.getType(), vl, pm);
							ArrayList<FormParameter> arrayList = new ArrayList<FormParameter>();
							arrayList.add(vl);
							if (value2.getFormParameters()==null){
								value2.setFormParameters(new HashMap<String,java.util.List<FormParameter>>());
							}
							value2.getFormParameters().put(annotationValue,
									arrayList);
						}
					}
				}
				value.getBody().put(s, value2);
			}
		}
		String[] producesValue = m.getAnnotationValues(PRODUCES);
		if (producesValue == null) {
			producesValue = classProduces;
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
			value.getResponses().put("200", value2); //$NON-NLS-1$
		} else {
			Response value2 = new Response();
			value2.setDescription(documentation.getReturnInfo());
			// for (String s : producesValue) {
			// s = sanitizeMediaType(s);
			// MimeType mimeType = new MimeType();
			// mimeType.setType(s);
			// value2.getBody().put(s, mimeType);
			//
			// }
			value.getResponses().put("200", value2); //$NON-NLS-1$
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
		if (s.contains("xml")) { //$NON-NLS-1$
			s = "application/xml"; //$NON-NLS-1$
		}
		if (s.contains("json")) { //$NON-NLS-1$
			s = "application/json"; //$NON-NLS-1$
		}
		return s;
	}

	private void proceedType(String type, AbstractParam value2,
			IParameterModel param) {
		String annotationValue = param.getAnnotationValue(DEFAULT_VALUE);
		boolean hasDefault=false;
		if (annotationValue != null) {
			value2.setDefaultValue(annotationValue);
			hasDefault=true;
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
		if (type.equals("int")||type.equals("long")||type.equals("short")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			value2.setType(ParamType.INTEGER);
			value2.setRequired(!hasDefault);
		}
		if (type.equals("float")||type.equals("double")) { //$NON-NLS-1$ //$NON-NLS-2$
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
			CustomSchemaOutputResolver sor = new CustomSchemaOutputResolver(fileName);
			jaxbContext.generateSchema(sor);
			String content = FileUtil.fileToString(sor.getFile());				
			spec.getCoreRaml().addGlobalSchema(name, content, false, false);
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
}