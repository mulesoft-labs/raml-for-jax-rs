package com.mulesoft.jaxrs.raml.annotation.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;


import org.raml.model.Protocol;
import org.raml.model.Raml2;
import org.raml.model.Resource;
import org.raml.model.parameter.UriParameter;


public class RAMLModelHelper {

	protected Raml2 coreRaml = new Raml2();

	public RAMLModelHelper() {
		coreRaml.setBaseUri("http://example.com");
		coreRaml.setTitle("Please type API title here");
		coreRaml.setProtocols(Collections.singletonList(Protocol.HTTP));
	}
	public String getMediaType() {
		return coreRaml.getMediaType();
	}
	
	public void addResource(Resource res) {
		cleanupUrl(res);
		String relativeUri = res.getRelativeUri();
		int c = 0;
		for (int a = 0; a < relativeUri.length(); a++) {
			if (relativeUri.charAt(a) == '/') {
				c++;
			}
		}
		Map<String, Resource> resources = getCoreRaml().getResources();
		if (c == 1) {
			resources.put(relativeUri, res);
			return;
		}
		if (res.getRelativeUri().length() == 0 && res.getActions().isEmpty()) {
			return;
		}
		placeResource(resources, res);
	}

	private void cleanupUrl(Resource res) {
		String relativeUri = res.getRelativeUri();
		String string = doCleanup(relativeUri);
		if (string.length()==0){
			string="/";
		}
		res.setRelativeUri(string);
	}

	private static String doCleanup(String relativeUri) {
		StringBuilder bld=new StringBuilder();
		char pc=0;
		for (int a=0;a<relativeUri.length();a++){
			char c=relativeUri.charAt(a);
			if (c=='/'&&pc=='/'){
				continue;
			}
			else{
				bld.append(c);
				pc=c;
			}
		}
		String string = bld.toString();
		if (!string.startsWith("/")){
			string="/"+string;
		}
		return string;
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
				portableString=doCleanup(portableString);
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
				
				createResource.setRelativeUri(doCleanup(portableString));				
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