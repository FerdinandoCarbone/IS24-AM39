package com.example.codexnaturalis;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Corner {

    /**
     * availableCorner: boolean value, defines whether a corner is available or not
     */
    private boolean availableCorner;
    private ResourceGoldCard.ResourceElement resourceElement;

    /**
     * Constructor of Corner
     * @param resourceElement: defines what is contained in the corner if available
     */
    public Corner(@JsonProperty("Element") ResourceGoldCard.ResourceElement resourceElement) {
        this.availableCorner = true;
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
    public ResourceGoldCard.ResourceElement getresourceElement() {
        return resourceElement;
    }
}
