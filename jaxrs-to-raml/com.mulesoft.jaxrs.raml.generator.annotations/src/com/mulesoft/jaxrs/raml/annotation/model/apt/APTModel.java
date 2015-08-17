package com.mulesoft.jaxrs.raml.annotation.model.apt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;


//import com.google.common.base.Function;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;
import com.mulesoft.jaxrs.raml.annotation.model.IBasicModel;

/**
 * <p>Abstract APTModel class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public abstract class APTModel implements IBasicModel{
	
	protected APTModel(ProcessingEnvironment environment) {
		this.environment = environment;
	}

	protected ProcessingEnvironment environment;	

	private static final String VALUE_METHOD_ID = "value"; //$NON-NLS-1$

	/**
	 * <p>element.</p>
	 *
	 * @return a {@link javax.lang.model.element.Element} object.
	 */
	public abstract Element element();
	
	
	/**
	 * <p>getName.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getName() {
		return element().getSimpleName().toString();
	}
	
	/**
	 * <p>getDocumentation.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getDocumentation() {
		return this.environment.getElementUtils().getDocComment(element());
	}

	/**
	 * <p>getAnnotations.</p>
	 *
	 * @return an array of {@link com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel} objects.
	 */
	public IAnnotationModel[] getAnnotations() {
		List<? extends AnnotationMirror> annotationMirrors = element().getAnnotationMirrors();
		ArrayList<APTAnnotation>w=new ArrayList<APTAnnotation>();
		for (AnnotationMirror m:annotationMirrors){
			w.add(new APTAnnotation(m));
		}
		return w.toArray(new IAnnotationModel[w.size()]);
	}

	/** {@inheritDoc} */
	public String getAnnotationValue(String annotation) {
		List<? extends AnnotationMirror> annotationMirrors = element().getAnnotationMirrors();
		for (AnnotationMirror annotationMirror : annotationMirrors) {
			String name = annotationMirror.getAnnotationType().asElement().getSimpleName().toString();
			if (name.equals(annotation)) {
				for( Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet() )
                {
                    if( VALUE_METHOD_ID.equals(entry.getKey().getSimpleName().toString() ) ) 
                    {
                        AnnotationValue action = entry.getValue();
                        return action.getValue().toString();
                    }
                }
			}
		}
		return null;
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public String[] getAnnotationValues(String annotation) {
		List<? extends AnnotationMirror> annotationMirrors = element().getAnnotationMirrors();
		for (AnnotationMirror annotationMirror : annotationMirrors) {
			String name = annotationMirror.getAnnotationType().asElement().getSimpleName().toString();
			if (name.equals(annotation)) {
				Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
				for (ExecutableElement q:elementValues.keySet()){
					String elementName=q.getSimpleName().toString();
					if (elementName.equals(VALUE_METHOD_ID)){
						Object value2 = annotationMirror.getElementValues().get(q).getValue();
						if (value2 instanceof String){
							return new String[]{(String) value2};
						} else if (value2 instanceof List) {
							String[] result = Collections2.transform(((List<AnnotationValue>) value2), new Function<AnnotationValue,String>() {

								
								public String apply(AnnotationValue arg0) {
									return arg0.getValue().toString();
								}
								
							}).toArray(new String[0]);
							return result;
						}
						return (String[]) value2;
					}
				}
			}
		}
		return null;
	}

	/** {@inheritDoc} */
	public boolean hasAnnotation(String annotationName) {
		List<? extends AnnotationMirror> annotationMirrors = element().getAnnotationMirrors();
		for (AnnotationMirror annotationMirror : annotationMirrors) {
			String name = annotationMirror.getAnnotationType().asElement().getSimpleName().toString();
			if (name.equals(annotationName)) {
				return true;
			}
		}
		return false;
	}
	
	/** {@inheritDoc} */
	public IAnnotationModel getAnnotation(String name) {
		IAnnotationModel[] annotations = getAnnotations();
		for (IAnnotationModel m:annotations){
			if (m.getName().equals(name)){
				return m;
			}
		}
		return null;
	}
	
	public boolean hasAnnotationWithCanonicalName(String name) {
		IAnnotationModel[] annotations = getAnnotations();
		for (IAnnotationModel m:annotations){
			if (m.getCanonicalName().equals(name)){
				return true;
			}
		}
		return false;
	}
	
	/** {@inheritDoc} */
	public IAnnotationModel getAnnotationByCanonicalName(String name) {
		IAnnotationModel[] annotations = getAnnotations();
		for (IAnnotationModel m:annotations){
			if (m.getCanonicalName().equals(name)){
				return m;
			}
		}
		return null;
	}
}
