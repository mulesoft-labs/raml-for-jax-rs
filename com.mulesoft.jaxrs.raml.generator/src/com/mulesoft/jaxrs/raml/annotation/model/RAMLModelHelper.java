package com.mulesoft.jaxrs.raml.annotation.model;

import java.util.HashSet;
import java.util.Map;


import org.raml.model.Raml2;
import org.raml.model.Resource;
import org.raml.model.parameter.UriParameter;


public class RAMLModelHelper {

	protected Raml2 coreRaml = new Raml2();

	
	public String getMediaType() {
		return coreRaml.getMediaType();
	}
	
	public void addResource(Resource res){
		Map<String, Resource> resources = getCoreRaml().getResources();
		placeResource(resources, res);
	}
	
	//TODO More accurate resource merging
	public static void placeResource(Map<String, Resource> resources,
			Resource createResource) {
		String relativeUri = createResource.getRelativeUri();		
		Path path = new Path(relativeUri);
		boolean restructure = false;
		for (String s : new HashSet<String>(resources.keySet())) {
			Path rp = new Path(s);
			if (path.isPrefixOf(rp)&&!path.equals(rp)) {
				restructure = true;
				Resource remove = resources.remove(s);
				Path removeFirstSegments = rp.removeFirstSegments(path
						.segmentCount());
				String portableString = "/"
						+ removeFirstSegments.toPortableString();
				remove.setRelativeUri(portableString);
				Resource old = resources.put(relativeUri, createResource);
				if (old!=null){
					createResource.getActions().putAll(old.getActions());
				}
				Map<String, UriParameter> uriParameters = createResource
						.getUriParameters();
				Map<String, UriParameter> uriParameters2 = remove
						.getUriParameters();
				for (String q : uriParameters.keySet()) {
					uriParameters2.remove(q);
				}
				createResource.getResources().put(portableString, remove);
				Resource put = resources.put(relativeUri, createResource);
				if (put!=null){
					createResource.getActions().putAll(put.getActions());
				}
			}
		}
		if (restructure) {
			return;
		}
		for (String s : resources.keySet()) {
			Path rp = new Path(s);
			if (rp.isPrefixOf(path)&&path.segmentCount()-rp.segmentCount()>=1) {
				Path removeFirstSegments2 = path.removeFirstSegments(rp
						.segmentCount());
				Path removeFirstSegments = removeFirstSegments2;
				String portableString = "/"
						+ removeFirstSegments.toPortableString();
				
				createResource.setRelativeUri(portableString);				
				Resource resource = resources.get(s);
				Map<String, UriParameter> uriParameters = resource
						.getUriParameters();
				Map<String, UriParameter> uriParameters2 = createResource
						.getUriParameters();
				for (String sa : uriParameters.keySet()) {
					uriParameters2.remove(sa);
				}
				placeResource(resource.getResources(), createResource);
				return;
			}
		}				
		Resource put = resources.put(relativeUri, createResource);
		if (put!=null){
			createResource.getActions().putAll(put.getActions());
			createResource.getResources().putAll(put.getResources());
		}
	}

	public void setMediaType(String mediaType) {
		coreRaml.setMediaType(mediaType);
	}

	public Raml2 getCoreRaml() {
		return coreRaml;
	}

}