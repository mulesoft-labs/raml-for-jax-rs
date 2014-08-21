package org.raml.model;

import org.raml.model.Action;
import org.raml.model.MimeType;
import org.raml.model.Resource;
import org.raml.model.Response;
import org.raml.model.parameter.Header;
import org.raml.model.parameter.QueryParameter;
import org.raml.model.parameter.UriParameter;

public class RamlFileVisitorAdapter implements IRamlFileVisitor {

	
	public boolean startVisit(Resource resource) {
		return true;
	}

	
	public void endVisit(Resource resource) {

	}

	
	public boolean startVisit(Action action) {
		return true;
	}

	
	public boolean endVisit(Action action) {
		return true;
	}

	
	public void visit(String name, QueryParameter queryParameter) {

	}

	
	public void visit(String name, UriParameter uriParameter) {

	}

	
	public void visit(String name, Header header) {

	}

	
	public boolean startVisit(String code, Response response) {
		return true;
	}

	
	public void endVisit(Response response) {

	}

	
	public void visit(MimeType mimeType) {

	}

	
	public boolean startVisitBody() {
		return true;
	}

	
	public void endVisitBody() {

	}

	public void visitTrait(Action traitModel) {

	}

	
	public void visitResourceType(Resource typeModel) {

	}

}