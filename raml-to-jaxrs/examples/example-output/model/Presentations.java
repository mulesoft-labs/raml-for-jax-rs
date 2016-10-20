
package org.raml.jaxrs.example.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * A collection of product Presentations
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "size",
    "presentations"
})
public class Presentations {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("size")
    private Integer size;
    @JsonProperty("presentations")
    private List<Presentation> presentations = new ArrayList<Presentation>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("size")
    public Integer getSize() {
        return size;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("size")
    public void setSize(Integer size) {
        this.size = size;
    }

    public Presentations withSize(Integer size) {
        this.size = size;
        return this;
    }

    @JsonProperty("presentations")
    public List<Presentation> getPresentations() {
        return presentations;
    }

    @JsonProperty("presentations")
    public void setPresentations(List<Presentation> presentations) {
        this.presentations = presentations;
    }

    public Presentations withPresentations(List<Presentation> presentations) {
        this.presentations = presentations;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
