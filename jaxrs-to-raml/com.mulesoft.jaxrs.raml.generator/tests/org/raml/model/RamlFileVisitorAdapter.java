package org.raml.model;

import org.raml.model.Action;
import org.raml.model.MimeType;
import org.raml.model.Resource;
import org.raml.model.Response;
import org.raml.model.parameter.Header;
import org.raml.model.parameter.QueryParameter;
import org.raml.model.parameter.UriParameter;

public class RamlFileVisitorAdapter implements IRamlFileVisitor {

	@Override
	public boolean startVisit(Resource resource) {
		return true;
	}

	@Override
	public void endVisit(Resource resource) {

	}

	@Override
	public boolean startVisit(Action action) {
		return true;
	}

	@Override
	public boolean endVisit(Action action) {
		return true;
	}

	@Override
	public void visit(String name, QueryParameter queryParameter) {

	}

	@Override
	public void visit(String name, UriParameter uriParameter) {

	}

	@Override
	public void visit(String name, Header header) {

	}

	@Override
	public boolean startVisit(String code, Response response) {
		return true;
	}

	@Override
	public void endVisit(Response response) {

	}

	@Override
	public void visit(MimeType mimeType) {

	}

	@Override
	public boolean startVisitBody() {
		return true;
	}

	@Override
	public void endVisitBody() {

	}

	public void visitTrait(Action traitModel) {

	}

	@Override
	public void visitResourceType(Resource typeModel) {

	}

}