package com.mulesoft.jaxrs.raml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.aml.apimodel.Resource;
import org.aml.apimodel.impl.ApiImpl;
import org.aml.apimodel.impl.ResourceImpl;

/**
 * <p>RAMLModelHelper class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class RAMLModelHelper {

	protected ApiImpl coreRaml = new ApiImpl();

	/**
	 * <p>Constructor for RAMLModelHelper.</p>
	 */
	public RAMLModelHelper() {
		coreRaml.setBaseUrl("http://example.com");
		coreRaml.setTitle("Please type API title here");
		coreRaml.setProtocols(Collections.singletonList("http"));
	}

	
	/**
	 * <p>setMediaType.</p>
	 *
	 * @param mediaType a {@link java.lang.String} object.
	 */
	public void setMediaType(String mediaType) {
		final List<String> singletonList = Collections.singletonList(mediaType);
		coreRaml.setMediaTypes(singletonList);
	}
	

	/**
	 * <p>Getter for the field <code>coreRaml</code>.</p>
	 *
	 * @return a {@link ApiImpl} object.
	 */
	public ApiImpl getCoreRaml() {
		return coreRaml;
	}

	/**
	 * <p>optimize.</p>
	 */
	@SuppressWarnings("unchecked")
	public void optimize() {
		optimizeDocumentation((List)coreRaml.getResources());
		Collections.sort(coreRaml.resources());
	}

	private void optimizeDocumentation(List<ResourceImpl> resources) {
		for(ResourceImpl res: resources){
			LinkedHashSet<String> set = new LinkedHashSet<String>();
			gatherDocumentation(res,set);
			StringBuilder bld = new StringBuilder();
			for(String str : set){
				bld.append("\n\n").append(str);
			}
			if(bld.length()>0){
				String desc = bld.toString().trim();
				res.setDescription(desc);
			}
		}
	}

	private void gatherDocumentation(Resource res, LinkedHashSet<String> set) {
		String desc = res.description();
		if(desc!=null){
			((ResourceImpl)res).setDescription(null);
			desc = desc.trim();
			if(desc.length()>0){
				set.add(desc);
			}
		}
		for(Resource r : res.resources()){
			gatherDocumentation(r, set);
		}
	}

	/**
	 * <p>optimizeResourceMap.</p>
	 *
	 * @param resources a {@link java.util.Map} object.
	 */
	protected void optimizeResourceMap(Map<String, ResourceImpl> resources) {
		extractCommonPaths(resources);
	}
	boolean extractCommonParts=true;

	private void extractCommonPaths(Map<String, ResourceImpl> resources) {
		if(!extractCommonParts){
			return;
		}
		LinkedHashMap<String, ResourceImpl> rs = (LinkedHashMap<String, ResourceImpl>) resources;
		ArrayList<Entry> rt = getEntries(rs);
		HashMap<String, ArrayList<Entry>>map=new HashMap<String, ArrayList<Entry>>();
		for (Entry e:rt){
			Path c=new Path(e.path);
			if (c.segmentCount()>1){
				String segment = c.segment(0);
				ArrayList<Entry> arrayList = map.get(segment);
				if (arrayList==null){
					arrayList=new ArrayList<RAMLModelHelper.Entry>();
					map.put(segment, arrayList);
				}
				arrayList.add(e);
			}
		}
		if (!map.isEmpty()){
			//list of entries to collapse segment
			for (String  s:map.keySet()){
				ArrayList<Entry>e=map.get(s);
				if(e.size()<2){
					continue;
				}
				LinkedHashSet<String> docs = new LinkedHashSet<String>();
				Entry base=e.get(0);
				ResourceImpl r0=base.res;
				String desc0 = r0.description();
				if(desc0 != null){
					docs.add(desc0);
				}
				String relativeUri = "/"+s;
				ResourceImpl newRes=new ResourceImpl(relativeUri);
				base.path=relativeUri;
				stripSegment(r0);
				newRes.update(r0.relativeUri(), r0);
				base.res=newRes;
				for (int a=1;a<e.size();a++){
					Entry entry = e.get(a);
					String desc = entry.res.description();
					if(desc != null){
						docs.add(desc);
						entry.res.setDescription(null);
					}
					stripSegment(entry.res);
					rt.remove(entry);
					newRes.update(entry.res.relativeUri(), entry.res);
				}
				StringBuilder bld = new StringBuilder();
				for(String desc : docs){
					bld.append(desc).append("\n\n");
				}
				newRes.setDescription(bld.toString().trim());
			}
		}
		resources.clear();
		entriesToMap(resources, rt);
	}

	private void stripSegment(ResourceImpl r0) {
		String relativeUri = r0.relativeUri();
		Path p=new Path(relativeUri);
		p=p.removeFirstSegments(1);
		r0.setRelativeUri("/"+p.toPortableString());
	}

	private ArrayList<Entry> getEntries(LinkedHashMap<String, ResourceImpl> rs) {
		ArrayList<Entry> rt = new ArrayList<RAMLModelHelper.Entry>();
		for (String path : rs.keySet()) {
			rt.add(new Entry(path, rs.get(path)));
		}
		return rt;
	}

	private void entriesToMap(Map<String, ResourceImpl> resources,
			ArrayList<Entry> rt) {
		for (Entry e : rt) {
			resources.put(e.path, e.res);
		}
	}

	static class Entry implements Comparable<Entry> {
		protected String path;

		public Entry(String path, ResourceImpl res) {
			super();
			this.path = path;
			this.res = res;
		}

		protected ResourceImpl res;

		
		public int compareTo(Entry o) {
			return path.compareTo(o.path);
		}
	}

}