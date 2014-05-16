package com.mulesoft.jaxrs.raml.generator.popup.actions;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;
import org.raml.model.ActionType;
import org.raml.model.Protocol;

import com.mulesoft.jaxrs.raml.generator.eclipse.JAXRSTORamlPlagin;

final class PreferencesConfig implements IEditableRamlConfig {
	
	public static final String TITLE="title";
	public static final String VERSION="version";
	public static final String BASEURL="baseurl";
	public static final String PROTOCOLS="protocols";
	private static final String SINGLE = "single";
	private static final String SORTED = "sorted";
	
	protected IPreferenceStore preferences=JAXRSTORamlPlagin.getInstance().getPreferenceStore();
	
	@Override
	public String getTitle() {
		String string = preferences.getString(TITLE);
		if (string==null||string.length()==0){
			return "Please type API title here";
		}
		return string;
	}

	@Override
	public String getResponseCode(ActionType type) {
		return "200";
	}

	@Override
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
	@Override
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


	@Override
	public String getBaseUrl() {
		String string = preferences.getString(BASEURL);
		if (string==null||string.length()==0){
			return "http://example.com";
		}
		return string;
	}

	@Override
	public void setTitle(String title) {
		preferences.putValue(TITLE,title);
	}


	@Override
	public void setBaseUrl(String baseUrl) {
		preferences.setValue(BASEURL, baseUrl);
	}

	@Override
	public void setVersion(String value) {
		preferences.setValue(VERSION, value);
	}

	@Override
	public String getVersion() {
		String string = preferences.getString(VERSION);
		if (string==null||string.length()==0){
			return "v1";
		}
		return string;
	}

	@Override
	public boolean isSingle() {
		return preferences.getBoolean(SINGLE);
	}

	

	@Override
	public void setSingle(boolean selection) {
		preferences.setValue(SINGLE, selection);
	}
	

	@Override
	public boolean isSorted() {
		return !preferences.getBoolean(SORTED);
	}

	@Override
	public void setSorted(boolean selection) {
		preferences.setValue(SORTED, !selection);
	}
}