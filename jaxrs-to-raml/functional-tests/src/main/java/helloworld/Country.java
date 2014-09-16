package helloworld;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/*******************************************************************************
 * FILE NAME: Country.java
 * PURPOSE:   These comments are auto generated
 *
 *
 * Revision History: 
 * DATE:           							  AUTHOR:              CHANGE:  
 * Mon Jun 11 16:57:27 IST 2012               Auto generated       Initial Version  
 * 
 * 
 ******************************************************************************/
@XmlRootElement(name = "country")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "id", "name", "population", "states" })
public class Country {

	@XmlElement(name = "id", required = false, type = Integer.class)
	private int id;
	@XmlElement(name = "states", required = false)
	private States states = null;
	@XmlAttribute(name = "uri")
	private String uri = null;
	@XmlElement(name = "name", required = true)
	private String name;
	@XmlElement(name = "population", required = true)
	private Integer population;

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
	 * @param states
	 */
	public void setStates(States states) {
		this.states = states;
	}

	/**
	 * @return states
	 */
	public States getStates() {
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

	@XmlRootElement(name = "states")
	@XmlAccessorType(XmlAccessType.NONE)
	@XmlType(propOrder = { "states" })
	public static class States {

		@XmlAttribute(name = "uri")
		private String uri = null;
		@XmlElement(name = "state", required = false)
		private java.util.Collection<State> states;

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

			@XmlAttribute(name = "href", required = true)
			private String link = null;
			@XmlAttribute(name = "uri")
			private String uri = null;
			@XmlElement(name = "id", required = false, type = Integer.class)
			private int id;
			@XmlElement(name = "name", required = true)
			private String name;
			@XmlElement(name = "population", required = true)
			private Long population;

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
		 * @param states
		 */
		public void setStates(java.util.Collection<State> states) {
			this.states = states;
		}

		/**
		 * @return java.util.Collection<State>
		 */
		public java.util.Collection<State> getStates() {
			return states;
		}
	}

	/**
	 * @param population
	 */
	public void setPopulation(Integer population) {
		this.population = population;
	}

	/**
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Integer
	 */
	public Integer getPopulation() {
		return population;
	}
}
