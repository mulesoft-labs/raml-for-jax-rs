
package com.mulesoft.jaxrs.raml.annotation.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * <p>FileUtil class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class FileUtil {

	/**
	 * <p>fileToString.</p>
	 *
	 * @param file a {@link java.io.File} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String fileToString(File file) {
		String text;
		try {
			text = new Scanner(file).useDelimiter("\\A").next(); //$NON-NLS-1$
			return text;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
