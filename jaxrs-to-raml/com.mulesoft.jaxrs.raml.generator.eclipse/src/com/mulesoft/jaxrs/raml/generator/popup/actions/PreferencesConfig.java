package com.mulesoft.jaxrs.raml.generator.popup.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;
import org.raml.model.ActionType;
import org.raml.model.Protocol;

import com.mulesoft.jaxrs.raml.annotation.model.IResourceVisitorExtension;
import com.mulesoft.jaxrs.raml.generator.eclipse.JAXRSTORamlPlagin;

final class PreferencesConfig implements IEditableRamlConfig {
	
	public static final String TITLE="title";
	public static final String VERSION="version";
	public static final String BASEURL="baseurl";
	public static final String PROTOCOLS="protocols";
	private static final String SINGLE = "single";
	private static final String SORTED = "sorted";
	private static final String FULL_TREE = "fullTree";
	
	protected IPreferenceStore preferences=JAXRSTORamlPlagin.getInstance().getPreferenceStore();
	
	
	public String getTitle() {
		String string = preferences.getString(TITLE);
		if (string==null||string.length()==0){
			return "Please type API title here";
		}
		return string;
	}

	
	public String getResponseCode(ActionType type) {
		String string = preferences.getString(getResponseCodeKey(type));
		if (string!=null&&string.trim().length()>0){
			return string;
		}
		return "200";
	}

	
	public Set<Protocol> getProtocols() {
		String string = preferences.getString(PROTOCOLS);
		if (string!=null&&string.length()>0){
			String[] split = string.split(",");
			HashSet<Protocol>s=new HashSet<Protocol>();
			for (String str:split){
				s.add(Protocol.valueOf(str));
			}
			return s;
		}
		return Collections.singleton(Protocol.HTTP);
	}
	
	public void setProtocols(Set<Protocol> ps) {
		StringBuilder bld=new StringBuilder();
		int a=0;
		for (Protocol p:ps){
			bld.append(p.name());
			a++;
			if (a!=ps.size()){
			bld.append(",");
			}
		}
		preferences.setValue(PROTOCOLS, bld.toString());
	}


	
	public String getBaseUrl() {
		String string = preferences.getString(BASEURL);
		if (string==null||string.length()==0){
			return "http://example.com";
		}
		return string;
	}

	
	public void setTitle(String title) {
		preferences.putValue(TITLE,title);
	}


	
	public void setBaseUrl(String baseUrl) {
		preferences.setValue(BASEURL, baseUrl);
	}

	
	public void setVersion(String value) {
		preferences.setValue(VERSION, value);
	}

	
	public String getVersion() {
		String string = preferences.getString(VERSION);
		if (string==null||string.length()==0){
			return "v1";
		}
		return string;
	}

	
	public boolean isSingle() {
		return preferences.getBoolean(SINGLE);
	}

	

	
	public void setSingle(boolean selection) {
		preferences.setValue(SINGLE, selection);
	}
	

	
	public boolean isSorted() {
		return !preferences.getBoolean(SORTED);
	}

	
	public void setSorted(boolean selection) {
		preferences.setValue(SORTED, !selection);
	}

	
	public void setDefaultResponseCode(ActionType a, String text) {
		preferences.putValue(getResponseCodeKey(a), text);
	}

	private String getResponseCodeKey(ActionType a) {
		return a.name()+".responseCode";
	}

	
	public boolean doFullTree() {
		return !preferences.getBoolean(FULL_TREE);
	}

	
	public void setDoFullTree(boolean selection) {
		preferences.setValue(FULL_TREE, !selection);
	}


	@Override
	public List<IResourceVisitorExtension> getExtensions() {
		return new ArrayList<IResourceVisitorExtension>();
	}
}