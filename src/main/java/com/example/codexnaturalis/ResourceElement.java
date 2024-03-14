package com.example.codexnaturalis;

/**
 * ResourceElement parent of classes Resource and Element
 */
public class ResourceElement {
    /**
     * Resource is subClass of ResourceElement
     * Extra Fields: resourceType
     */
    public static class Resource extends ResourceElement {
        /**
         * Defines the type of resource
         */
        private String resourceType;

        /**
         * Constructor of Resource
         * @param resourceType: Defines the type of resource
         */
        public Resource(String resourceType) {
            this.resourceType = resourceType;
        }
        /**
         * Animal is subClass of Resource
         */
        public static class Animal extends Resource {
            /**
             * Constructor of Animal
             */
            public Animal() {
                super("Animal");
            }
        }
        /**
         * Insect is subClass of Resource
         */
        public static class Insect extends Resource {
            /**
             * Constructor of Insect
             */
            public Insect() {
                super("Insect");
            }
        }
        /**
         * Mushroom is subClass of Resource
         */
        public static class Mushroom extends Resource {
            /**
             * Constructor of Mushroom
             */
            public Mushroom() {
                super("Mushroom");
            }
        }
        /**
         * Plant is subClass of Resource
         */
        public static class Plant extends Resource {
            /**
             * Plant of Mushroom
             */
            public Plant() {
                super("Plant");
            }
        }
    }
    /**
     * Plant is subClass of ResourceElement
     * Extra Fields: elementType
     */
    public abstract static class Element extends ResourceElement {
        /**
         * Defines the type of Element
         */
        private String elementType;

        /**
         * Constructor of Element
         * @param elementType
         */
        public Element(String elementType) {
            this.elementType = elementType;
        }

        /**
         * Feather is subClass of Element
         */
        public static class Feather extends Element {
            /**
             * Constructor of Feather
             */
            public Feather() {
                super("Feather");
            }
        }
        /**
         * Ink is subClass of Element
         */
        public static class Ink extends Element {
            /**
             * Constructor of Ink
             */
            public Ink() {
                super("Ink");
            }
        }
        /**
         * Papyrus is subClass of Element
         */
        public static class Papyrus extends Element {
            /**
             * Constructor of Papyrus
             */
            public Papyrus() {
                super("Papyrus");
            }
        }
    }
}
