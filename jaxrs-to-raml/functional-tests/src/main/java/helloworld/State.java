package helloworld;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/*******************************************************************************
 * FILE NAME: State.java
 * PURPOSE:   These comments are auto generated
 *
 *
 * Revision History: 
 * DATE:           							  AUTHOR:              CHANGE:  
 * Mon Jun 11 16:57:55 IST 2012               Auto generated       Initial Version  
 * 
 * 
 ******************************************************************************/
@XmlRootElement(name = "state")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "id", "name", "population" })
public class State {

	@XmlElement(name = "id", required = false, type = Integer.class)
	private int id;
	@XmlElement(name = "name", required = true)
	private String name;
	@XmlElement(name = "population", required = true)
	private Long population;
	@XmlAttribute(name = "uri")
	private String uri = null;

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
