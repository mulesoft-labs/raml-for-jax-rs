
package org.raml.jaxrs.example.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * A single product Presentation
 * 
 */
@Generated("org.jsonschema2pojo")
public class Presentation_ {

    /**
     * 
     * (Required)
     * 
     */
    private String id;
    /**
     * 
     * (Required)
     * 
     */
    private String title;
    private String description;
    /**
     * 
     * (Required)
     * 
     */
    private String fileUrl;
    /**
     * 
     * (Required)
     * 
     */
    private String productId;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    protected final static Object NOT_FOUND_VALUE = new Object();

    /**
     * 
     * (Required)
     * 
     * @return
     *     The id
     */
    public String getId() {
        return id;
    }

    /**
     * 
     * (Required)
     * 
     * @param id
     *     The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * 
     * (Required)
     * 
     * @param title
     *     The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 
     * @return
     *     The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * 
     * @param description
     *     The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The fileUrl
     */
    public String getFileUrl() {
        return fileUrl;
    }

    /**
     * 
     * (Required)
     * 
     * @param fileUrl
     *     The fileUrl
     */
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    /**
     * 
     * (Required)
     * 
     * @return
     *     The productId
     */
    public String getProductId() {
        return productId;
    }

    /**
     * 
     * (Required)
     * 
     * @param productId
     *     The productId
     */
    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    protected boolean declaredProperty(String name, Object value) {
        if ("id".equals(name)) {
            if (value instanceof String) {
                setId(((String) value));
            } else {
                throw new IllegalArgumentException(("property \"id\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
            }
            return true;
        } else {
            if ("title".equals(name)) {
                if (value instanceof String) {
                    setTitle(((String) value));
                } else {
                    throw new IllegalArgumentException(("property \"title\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
                }
                return true;
            } else {
                if ("description".equals(name)) {
                    if (value instanceof String) {
                        setDescription(((String) value));
                    } else {
                        throw new IllegalArgumentException(("property \"description\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
                    }
                    return true;
                } else {
                    if ("fileUrl".equals(name)) {
                        if (value instanceof String) {
                            setFileUrl(((String) value));
                        } else {
                            throw new IllegalArgumentException(("property \"fileUrl\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
                        }
                        return true;
                    } else {
                        if ("productId".equals(name)) {
                            if (value instanceof String) {
                                setProductId(((String) value));
                            } else {
                                throw new IllegalArgumentException(("property \"productId\" is of type \"java.lang.String\", but got "+ value.getClass().toString()));
                            }
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }
        }
    }

    protected Object declaredPropertyOrNotFound(String name, Object notFoundValue) {
        if ("id".equals(name)) {
            return getId();
        } else {
            if ("title".equals(name)) {
                return getTitle();
            } else {
                if ("description".equals(name)) {
                    return getDescription();
                } else {
                    if ("fileUrl".equals(name)) {
                        return getFileUrl();
                    } else {
                        if ("productId".equals(name)) {
                            return getProductId();
                        } else {
                            return notFoundValue;
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings({
        "unchecked"
    })
    public<T >T get(String name) {
        Object value = declaredPropertyOrNotFound(name, Presentation_.NOT_FOUND_VALUE);
        if (Presentation_.NOT_FOUND_VALUE!= value) {
            return ((T) value);
        } else {
            return ((T) getAdditionalProperties().get(name));
        }
    }

    public void set(String name, Object value) {
        if (!declaredProperty(name, value)) {
            getAdditionalProperties().put(name, ((Object) value));
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(title).append(description).append(fileUrl).append(productId).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Presentation_) == false) {
            return false;
        }
        Presentation_ rhs = ((Presentation_) other);
        return new EqualsBuilder().append(id, rhs.id).append(title, rhs.title).append(description, rhs.description).append(fileUrl, rhs.fileUrl).append(productId, rhs.productId).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
