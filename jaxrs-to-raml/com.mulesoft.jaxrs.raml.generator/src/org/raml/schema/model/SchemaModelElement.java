package org.raml.schema.model;

import java.util.ArrayList;
import java.util.List;

import com.mulesoft.jaxrs.raml.annotation.model.IAnnotationModel;

public class SchemaModelElement {

	protected List<IAnnotationModel> annotations;

	public SchemaModelElement(List<IAnnotationModel> annotations) {
		this.annotations = annotations !=null ? annotations : new ArrayList<IAnnotationModel>();
	}

	public List<IAnnotationModel> getAnnotations() {
		return annotations;
	}

}
