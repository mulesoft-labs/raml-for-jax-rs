package helloworld;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/*******************************************************************************
 * FILE NAME: States_REF.java
 * PURPOSE:   These comments are auto generated
 *
 *
 * Revision History: 
 * DATE:           							  AUTHOR:              CHANGE:  
 * Mon Jun 11 18:29:46 IST 2012               Auto generated       Initial Version  
 * 
 * 
 ******************************************************************************/
@XmlRootElement(name = "states")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "states" })
public class States_REF {

	@XmlAttribute(name = "size")
	private Integer totalSize;

	public Integer getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(Integer totalSize) {
		this.totalSize = totalSize;
	}

	@XmlElement(name = "state", required = false)
	private java.util.Collection<State> states = null;
	@XmlAttribute(name = "uri")
	private String uri = null;
	@XmlAttribute(name = "href", required = true)
	private String link = null;

	/**
	 * @param states
	 */
	public void setStates(java.util.Collection<State> states) {
		this.states = states;
	}

	/**
	 * @return states
	 */
	public java.util.Collection<State> getStates() {
		return states;
	}

	/**
	 * @param uri
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * @return uri
	 */
	public String getUri() {
		return uri;
	}

	@XmlRootElement(name = "state")
	@XmlAccessorType(XmlAccessType.NONE)
	@XmlType(propOrder = { "id", "name", "population" })
	public static class State {

		@XmlAttribute(name = "uri")
		private String uri = null;
		@XmlAttribute(name = "href", required = true)
		private String link = null;
		@XmlElement(name = "id", required = false, type = Integer.class)
		private int id;
		@XmlElement(name = "name", required = false)
		private String name;
		@XmlElement(name = "population", required = false)
		private Long population;

		/**
		 * @param uri
		 */
		public void setUri(String uri) {
			this.uri = uri;
		}

		/**
		 * @return uri
		 */
		public String getUri() {
			return uri;
		}

		/**
		 * @param link
		 */
		public void setLink(String link) {
			this.link = link;
		}

		/**
		 * @return link
		 */
		public String getLink() {
			return link;
		}

		/**
		 * @param id
		 */
		public void setId(int id) {
			this.id = id;
		}

		/**
		 * @return int
		 */
		public int getId() {
			return id;
		}

		/**
		 * @param name
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return String
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param population
		 */
		public void setPopulation(Long population) {
			this.population = population;
		}

		/**
		 * @return Long
		 */
		public Long getPopulation() {
			return population;
		}
	}

	/**
	 * @param link
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * @return link
	 */
	public String getLink() {
		return link;
	}
}
