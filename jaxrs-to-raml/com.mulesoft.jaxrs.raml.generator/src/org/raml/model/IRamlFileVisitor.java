package org.raml.model;

import org.raml.model.parameter.Header;
import org.raml.model.parameter.QueryParameter;
import org.raml.model.parameter.UriParameter;


/**
 * <p>IRamlFileVisitor interface.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public interface IRamlFileVisitor {

	/**
	 * <p>startVisit.</p>
	 *
	 * @param resource a {@link org.raml.model.Resource} object.
	 * @return a boolean.
	 */
	public boolean startVisit(Resource resource);
	
	/**
	 * <p>endVisit.</p>
	 *
	 * @param resource a {@link org.raml.model.Resource} object.
	 */
	public void endVisit(Resource resource);
	
	/**
	 * <p>startVisit.</p>
	 *
	 * @param action a {@link org.raml.model.Action} object.
	 * @return a boolean.
	 */
	public boolean startVisit(Action action);
	
	/**
	 * <p>endVisit.</p>
	 *
	 * @param action a {@link org.raml.model.Action} object.
	 * @return a boolean.
	 */
	public boolean endVisit(Action action);
	
	/**
	 * <p>visit.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param queryParameter a {@link org.raml.model.parameter.QueryParameter} object.
	 */
	public void visit(String name,QueryParameter queryParameter);
	
	/**
	 * <p>visit.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param uriParameter a {@link org.raml.model.parameter.UriParameter} object.
	 */
	public void visit(String name,UriParameter uriParameter);
	
	/**
	 * <p>visit.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param header a {@link org.raml.model.parameter.Header} object.
	 */
	public void visit(String name,Header header);
	
	/**
	 * <p>startVisit.</p>
	 *
	 * @param code a {@link java.lang.String} object.
	 * @param response a {@link org.raml.model.Response} object.
	 * @return a boolean.
	 */
	public boolean startVisit(String code,Response response);
	/**
	 * <p>endVisit.</p>
	 *
	 * @param response a {@link org.raml.model.Response} object.
	 */
	public void endVisit(Response response);
	
	/**
	 * <p>visit.</p>
	 *
	 * @param mimeType a {@link org.raml.model.MimeType} object.
	 */
	public void visit(MimeType mimeType);
	
	/**
	 * <p>startVisitBody.</p>
	 *
	 * @return a boolean.
	 */
	public boolean startVisitBody();
	
	/**
	 * <p>endVisitBody.</p>
	 */
	public void endVisitBody();
	
	/**
	 * <p>visitTrait.</p>
	 *
	 * @param traitModel a {@link org.raml.model.Action} object.
	 */
	public void visitTrait(Action traitModel);
	
	/**
	 * <p>visitResourceType.</p>
	 *
	 * @param typeModel a {@link org.raml.model.Resource} object.
	 */
	public void visitResourceType(Resource typeModel);
}
