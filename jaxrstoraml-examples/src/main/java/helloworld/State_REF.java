package helloworld;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/*******************************************************************************
 * FILE NAME: State_REF.java
 * PURPOSE:   These comments are auto generated
 *
 *
 * Revision History: 
 * DATE:           							  AUTHOR:              CHANGE:  
 * Mon Jun 11 18:00:48 IST 2012               Auto generated       Initial Version  
 * 
 * 
 ******************************************************************************/
@XmlRootElement(name = "state")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "id", "name", "population" })
public class State_REF {

	@XmlElement(name = "id", required = false, type = Integer.class)
	private int id;
	@XmlElement(name = "name", required = false)
	private String name;
	@XmlElement(name = "population", required = false)
	private Long population;
	@XmlAttribute(name = "uri")
	private String uri = null;
	@XmlAttribute(name = "href", required = true)
	private String link = null;

	/**
	 * @param link
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * @param population
	 */
	public void setPopulation(Long population) {
		this.population = population;
	}

	/**
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Long
	 */
	public Long getPopulation() {
		return population;
	}

	/**
	 * @return int
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
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
}
