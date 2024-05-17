package com.example.codexnaturalis;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Corner implements Serializable {

    /**
     * availableCorner: boolean value, defines whether a corner is available or not
     */
    private boolean availableCorner;
    private ResourceGoldCard.ResourceElement resourceElement;

    /**
     * Constructor of Corner
     * @param resourceElement: defines what is contained in the corner if available
     */
    public Corner(@JsonProperty("available") boolean availableCorner, @JsonProperty("Element") ResourceGoldCard.ResourceElement resourceElement) {
        this.availableCorner = availableCorner;
        this.resourceElement = resourceElement;
    }

    /**
     * Setter of availableCorner
     * @param availableCorner: boolean value, defines whether a corner is available or not
     */
    public void setAvailableCorner(boolean availableCorner) {
        this.availableCorner = availableCorner;
    }

    /**
     * Getter of availableCorner
     * @return boolean that defines the availability of the corner
     */
    public boolean isAvailableCorner() {
        return availableCorner;
    }

    /**
     * Getter of resourceElement
     * @return ResourceElement that is contained in an available corner
     */
    public ResourceGoldCard.ResourceElement getResourceElement() {
        return resourceElement;
    }

    /**
     * Setter of ResourceElement
     * @param resourceElement: defines the element or the resource on the corner
     */
    public void setResourceElement(ResourceGoldCard.ResourceElement resourceElement) {
        this.resourceElement = resourceElement;
    }
}
