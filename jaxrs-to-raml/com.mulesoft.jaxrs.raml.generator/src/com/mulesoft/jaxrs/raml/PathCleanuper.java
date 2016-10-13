package com.mulesoft.jaxrs.raml;

/**
 * <p>PathCleanuper class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class PathCleanuper {

	/**
	 * <p>cleanupPath.</p>
	 *
	 * @param path a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String cleanupPath(String path){
		StringBuilder bld=new StringBuilder();
		int inParamCount = 0;
		boolean inConstraint=false;
		for (int a=0;a<path.length();a++){
			char c = path.charAt(a);
			if (c=='{') {
				inParamCount++;
			}
			if (c=='}') {
				inParamCount--;
			}
			if (inParamCount > 0){
				if (Character.isWhitespace(c)){
					continue;
				}
				if (c==':'){
					inConstraint=true;
				}
				if (inConstraint){
					continue;
				}
			}
			bld.append(c);
		}
		return bld.toString();
	}
	
	
}
