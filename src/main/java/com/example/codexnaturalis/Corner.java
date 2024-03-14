package com.example.codexnaturalis;

public class Corner {

    private boolean availableCorner;
    private ResourceElement resourceObject;

    /**
     * Constructor of Corner
     * @param availableCorner: boolean value, defines whether a corner is available or not
     * @param resourceElement: defines what is contained in the corner if available
     */
    public Corner(boolean availableCorner, ResourceElement resourceElement) {
        this.availableCorner = availableCorner;
        this.resourceObject = resourceElement;
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
     * Getter of resourceObject
     * @return ResourceElement that is contained in an available corner
     */
    public ResourceElement getResourceObject() {
        return resourceObject;
    }
}
