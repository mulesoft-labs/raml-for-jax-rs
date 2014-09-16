package org.raml.model;

import org.raml.model.parameter.Header;
import org.raml.model.parameter.QueryParameter;
import org.raml.model.parameter.UriParameter;


public interface IRamlFileVisitor {

	public boolean startVisit(Resource resource);
	
	public void endVisit(Resource resource);
	
	public boolean startVisit(Action action);
	
	public boolean endVisit(Action action);
	
	public void visit(String name,QueryParameter queryParameter);
	
	public void visit(String name,UriParameter uriParameter);
	
	public void visit(String name,Header header);
	
	public boolean startVisit(String code,Response response);
	public void endVisit(Response response);
	
	public void visit(MimeType mimeType);
	
	public boolean startVisitBody();
	
	public void endVisitBody();
	
	public void visitTrait(Action traitModel);
	
	public void visitResourceType(Resource typeModel);
}
