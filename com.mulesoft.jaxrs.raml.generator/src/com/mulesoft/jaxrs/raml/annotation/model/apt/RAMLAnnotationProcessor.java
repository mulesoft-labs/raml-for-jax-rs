package com.mulesoft.jaxrs.raml.annotation.model.apt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Encoded;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.mulesoft.jaxrs.raml.annotation.model.ResourceVisitor;

@SupportedAnnotationTypes({"javax.ws.rs.PUT","javax.ws.rs.GET","javax.ws.rs.POST","javax.ws.rs.OPTIONS", "javax.ws.rs.Produces", "javax.ws.rs.Path"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedOptions({ RAMLAnnotationProcessor.RAMLPATH_OPTION })
public class RAMLAnnotationProcessor extends AbstractProcessor {

	private static final String RAML_EXTENSION = ".raml"; //$NON-NLS-1$

	private static final String DEFAULT_GENERATED_NAME = "generated.raml"; //$NON-NLS-1$

	static final String RAMLPATH_OPTION = "ramlpath";
	
	private Class<?>[] annotationClasses = new Class<?>[]{ApplicationPath.class, 
			Consumes.class, 
			CookieParam.class, 
			DefaultValue.class, 
			DELETE.class, 
			Encoded.class, 
			FormParam.class, 
			GET.class, 
			HEAD.class, 
			HeaderParam.class, 
			HttpMethod.class, 
			MatrixParam.class, 
			OPTIONS.class, 
			Path.class, 
			PathParam.class, 
			POST.class, 
			Produces.class, 
			PUT.class, 
			QueryParam.class};

	private String outputPath;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		for (TypeElement typeElement : annotations) {
			if (typeElement.getQualifiedName().toString().startsWith("javax.ws")) { //$NON-NLS-1$
				continue;
			}
			System.out.println(typeElement.toString());
		}
		HashSet<TypeElement> result = new HashSet<TypeElement>();
		for (int i = 0; i < annotationClasses.length; i++) {
			Class<? extends Annotation> clazz = (Class<? extends Annotation>) annotationClasses[i];
			Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(clazz);
			for (Element element : elements) {
				Element enclosingElement = element.getEnclosingElement();
				while (enclosingElement != null && !(enclosingElement instanceof TypeElement)) {
					enclosingElement = enclosingElement.getEnclosingElement();
				}
				if (enclosingElement instanceof TypeElement && accept(((TypeElement) enclosingElement).getQualifiedName().toString())) {
					result.add((TypeElement) enclosingElement);
				}
			}
			
		}
		if (result.size() == 0) {
			return false;
		}
		ResourceVisitor visitor=new ResourceVisitor();
		for (TypeElement typeElement : result) {
			APTType aptType = new APTType(typeElement);
			visitor.visit(aptType);
		}
		String raml = visitor.getRaml();
		if (outputPath != null) {
			String defaultFileName = DEFAULT_GENERATED_NAME;
			if (result.size() == 1) {
				defaultFileName = result.toArray(new TypeElement[0])[0].getSimpleName().toString() + RAML_EXTENSION;
			}
			File outputFile;
			if (outputPath.endsWith(RAML_EXTENSION)) {
				outputFile = new File(outputPath);
				int idx = outputPath.lastIndexOf('/');
				idx = Math.max(outputPath.lastIndexOf('\\'), idx);
				if (idx > 0) {
					String dir = outputPath.substring(0, idx);
					new File(dir).mkdirs();
				}
			} else {
				File parentDir = new File(outputPath);
				parentDir.mkdirs();
				outputFile = new File(outputPath,defaultFileName);
			}
			PrintWriter writer = null;
			try {
				FileWriter fileWriter = new FileWriter(outputFile);
				writer = new PrintWriter(fileWriter);
				writer.write(raml);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				if (writer != null) {
					writer.close();
				}
			}
		}
		
		System.out.println(raml);
		return true;
	}

	private boolean accept(String typeName) {
		return !typeName.startsWith("java"); //$NON-NLS-1$
	}
	
	@Override
	public synchronized void init(ProcessingEnvironment environment) {
		super.init(environment);
		outputPath = environment.getOptions().get(RAMLPATH_OPTION);
	}

}