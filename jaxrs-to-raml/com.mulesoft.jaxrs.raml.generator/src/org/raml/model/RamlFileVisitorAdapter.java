package org.raml.model;

import org.raml.model.Action;
import org.raml.model.MimeType;
import org.raml.model.Resource;
import org.raml.model.Response;
import org.raml.model.parameter.Header;
import org.raml.model.parameter.QueryParameter;
import org.raml.model.parameter.UriParameter;

/**
 * <p>RamlFileVisitorAdapter class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class RamlFileVisitorAdapter implements IRamlFileVisitor {

	
	/**
	 * <p>startVisit.</p>
	 *
	 * @param resource a {@link org.raml.model.Resource} object.
	 * @return a boolean.
	 */
	public boolean startVisit(Resource resource) {
		return true;
	}

	
	/** {@inheritDoc} */
	public void endVisit(Resource resource) {

	}

	
	/** {@inheritDoc} */
	public boolean startVisit(Action action) {
		return true;
	}

	
	/**
	 * <p>endVisit.</p>
	 *
	 * @param action a {@link org.raml.model.Action} object.
	 * @return a boolean.
	 */
	public boolean endVisit(Action action) {
		return true;
	}

	
	/**
	 * <p>visit.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param queryParameter a {@link org.raml.model.parameter.QueryParameter} object.
	 */
	public void visit(String name, QueryParameter queryParameter) {

	}

	
	/**
	 * <p>visit.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param uriParameter a {@link org.raml.model.parameter.UriParameter} object.
	 */
	public void visit(String name, UriParameter uriParameter) {

	}

	
	/** {@inheritDoc} */
	public void visit(String name, Header header) {

	}

	
	/** {@inheritDoc} */
	public boolean startVisit(String code, Response response) {
		return true;
	}

	
	/**
	 * <p>endVisit.</p>
	 *
	 * @param response a {@link org.raml.model.Response} object.
	 */
	public void endVisit(Response response) {

	}

	
	/** {@inheritDoc} */
	public void visit(MimeType mimeType) {

	}

	
	/**
	 * <p>startVisitBody.</p>
	 *
	 * @return a boolean.
	 */
	public boolean startVisitBody() {
		return true;
	}

	
	/**
	 * <p>endVisitBody.</p>
	 */
	public void endVisitBody() {

	}

	/** {@inheritDoc} */
	public void visitTrait(Action traitModel) {

	}

	
	/** {@inheritDoc} */
	public void visitResourceType(Resource typeModel) {

	}

}
