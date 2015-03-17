package org.raml.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.raml.model.parameter.Header;
import org.raml.model.parameter.QueryParameter;
import org.raml.model.parameter.UriParameter;

/**
 * Extension to RAML model which keeps information about traits and resource types
 *
 * @author pavel
 * @version $Id: $Id
 */
public class Raml2 extends Raml {

	private Map<String, ResourceType> resourceTypesModel = new LinkedHashMap<String, ResourceType>();
	private Map<String, TraitModel> traitsModel = new LinkedHashMap<String, TraitModel>();
	private Map<String, String> schemaMap=new LinkedHashMap<String, String>();

    /**
     * <p>Getter for the field <code>schemaMap</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, String> getSchemaMap() {
		return schemaMap;
	}

    /**
     * <p>getSchemaContent.</p>
     *
     * @param schemaName a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String getSchemaContent(String schemaName){
    	for (Map<String,String>q:schemas){
    		if (q.containsKey(schemaName)){
    			return q.get(schemaName);
    		}
    	}
    	return null;
    }
    private HashMap<String, String> createSchemaMap(final String schemaName, final String content) {
        final HashMap<String, String> newSchemaLine = new HashMap<String, String>();
		newSchemaLine.put(schemaName, content);
        return newSchemaLine;
    }
	
	private boolean schemaDeclared(final String schemaName) {
	    return findMapWithSchemaDeclaration(schemaName) != null;
	}

	private Map<String, String> findMapWithSchemaDeclaration(String schemaName) {
	    for (final Map<String, String> line : schemas){
            if (line.keySet().contains(schemaName)){
                return line;
            }
        }
        
        return null;
    }
    
	/**
	 * <p>addGlobalSchema.</p>
	 *
	 * @param schemaName Schema name will be used as filename without extension and relative path.
	 * @param content a {@link java.lang.String} object.
	 * @param json If this parameter is false then it suppose that one should add an XML Schema.
	 * @param addSchemaSuffix a boolean.
	 */
	public void addGlobalSchema(
        final String schemaName,
        final String content,
        final boolean json,
        final boolean addSchemaSuffix)
	{
	    final HashMap<String, String> newSchemaLine = createSchemaMap(schemaName, content);
	    final String path = "schemas/" + schemaName + (addSchemaSuffix ? "-schema" : "") + "." + (json ? "json" : "xsd");
	    schemaMap.put(schemaName, path);
	    
	    if (!schemaDeclared(schemaName)) {
	        schemas.add(newSchemaLine);		
	    }
	}
	
	/**
	 * <p>addOrReplaceSchemaContent.</p>
	 *
	 * @param schemaName a {@link java.lang.String} object.
	 * @param content a {@link java.lang.String} object.
	 */
	public void addOrReplaceSchemaContent(
        final String schemaName,
        final String content)
    {
        final Map<String, String> foundMap = findMapWithSchemaDeclaration(schemaName);
        
        if (foundMap != null) {
            foundMap.put(schemaName, content);
        } else {
            final Map<String, String> map = createSchemaMap(schemaName, content);
            schemas.add(map);
            schemaMap.put(schemaName, "schemas/"+schemaName+".json");    	    
        }
    }
	
	/**
	 * <p>Getter for the field <code>resourceTypesModel</code>.</p>
	 *
	 * @return a {@link java.util.Map} object.
	 */
	public Map<String, ResourceType> getResourceTypesModel() {
		return resourceTypesModel;
	}

	/**
	 * <p>Setter for the field <code>resourceTypesModel</code>.</p>
	 *
	 * @param resourceTypesModel a {@link java.util.Map} object.
	 */
	public void setResourceTypesModel(Map<String, ResourceType> resourceTypesModel) {
		this.resourceTypesModel = resourceTypesModel;
	}

	/**
	 * <p>Getter for the field <code>traitsModel</code>.</p>
	 *
	 * @return a {@link java.util.Map} object.
	 */
	public Map<String, TraitModel> getTraitsModel() {
		return traitsModel;
	}

	/**
	 * <p>Setter for the field <code>traitsModel</code>.</p>
	 *
	 * @param traitsModel a {@link java.util.Map} object.
	 */
	public void setTraitsModel(Map<String, TraitModel> traitsModel) {
		this.traitsModel = traitsModel;
	}

	/**
	 * <p>visit.</p>
	 *
	 * @param v a {@link org.raml.model.IRamlFileVisitor} object.
	 */
	public void visit(IRamlFileVisitor v) {
		visitResources(this.getResources(), v);
		Map<String, ResourceType> resourceTypesModel2 = getResourceTypesModel();
		for (ResourceType z:resourceTypesModel2.values()){
			v.visitResourceType(z);
			visitResource(v, z);
		}
		for (TraitModel m:getTraitsModel().values()){
			visitAction(v, m);
		}
	}
	
	/**
	 * <p>addGlobalSchema.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param content a {@link java.lang.String} object.
	 * @param json a boolean.
	 */
	public void addGlobalSchema(String name,String content,boolean json){
		HashMap<String, String> e = new HashMap<String, String>();
		String path=name;
		if(json){
			path+="-schema.json";
		}
		else{
			path+="-schema.xsd";
		}
		e.put(name, content);
		getSchemaMap().put(name, "/schemas/"+path);
		for (Map<String,String>s:schemas){
			if (s.keySet().contains(name)){
				return;
			}
		}
		schemas.add(e);		
	}
	

	private void visitResources(Map<String, Resource> resourceTypesModel2,
			IRamlFileVisitor v) {
		for (Resource q : resourceTypesModel2.values()) {
			visitResource(v, q);
		}
	}

	private void visitResource(IRamlFileVisitor v, Resource q) {
		boolean startVisit = v.startVisit(q);
		if (startVisit) {
			Map<String, UriParameter> uriParameters = q.getUriParameters();
			for (String u:uriParameters.keySet()){
				v.visit(u,uriParameters.get(u));
			}
			Map<ActionType, Action> actions = q.getActions();
			for (Action a:actions.values()){
				visitAction(v, a);
			}
			visitResources(q.getResources(), v);
		}
		v.endVisit(q);
	}

	private void visitAction(IRamlFileVisitor v, Action a) {
		boolean startVisit2 = v.startVisit(a);
		if (startVisit2){
			Map<String, Header> headers = a.getHeaders();
			for (String h:headers.keySet()){
				v.visit(h,headers.get(h));
			}
			Map<String, QueryParameter> qp = a.getQueryParameters();
			for (String h:qp.keySet()){
				v.visit(h,qp.get(h));
			}
			v.startVisitBody();
			Map<String, MimeType> body = a.getBody();
			for (MimeType m:body.values()){
				v.visit(m);
			}
			v.endVisitBody();
			Map<String, Response> responses = a.getResponses();
			for (String c:responses.keySet()){
				Response response = responses.get(c);
				v.startVisit(c,response);
				for (MimeType m:response.getBody().values()){
					v.visit(m);
				}
				Map<String, Header> headers2 = response.getHeaders();
				for (String h:headers2.keySet()){
					v.visit(h,headers2.get(h));
				}							
				v.endVisit(responses.get(c));
			}
		}
		v.endVisit(a);
	}

}
