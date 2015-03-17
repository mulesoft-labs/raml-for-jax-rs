package org.raml.model;

import java.util.HashSet;

import org.raml.parser.annotation.Scalar;


/**
 * <p>TraitModel class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class TraitModel extends Action{

	@Scalar
	String displayName;
	
	@Scalar
	String usage;

	/**
	 * <p>Getter for the field <code>usage</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getUsage() {
		return usage;
	}

	/**
	 * <p>Setter for the field <code>usage</code>.</p>
	 *
	 * @param usage a {@link java.lang.String} object.
	 */
	public void setUsage(String usage) {
		this.usage = usage;
	}

	/**
	 * <p>Getter for the field <code>displayName</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * <p>Setter for the field <code>displayName</code>.</p>
	 *
	 * @param displayName a {@link java.lang.String} object.
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
