package com.example.codexnaturalis;

public class ResourceElement {
    public static class Resource extends ResourceElement {

        private String resourceType;

        public Resource(String resourceType) {
            this.resourceType = resourceType;
        }

        public static class Animal extends Resource {
            public Animal() {
                super("Animal");
            }
        }

        public static class Insect extends Resource {
            public Insect() {
                super("Insect");
            }
        }

        public static class Mushroom extends Resource {
            public Mushroom() {
                super("Mushroom");
            }
        }

        public static class Plant extends Resource {

            public Plant() {
                super("Plant");
            }
        }
    }

    public abstract static class Element extends ResourceElement {

        private String objectType;

        public Element(String objectType) {
            this.objectType = objectType;
        }

        public static class Feather extends Element {
            public Feather() {
                super("Feather");
            }
        }

        public static class Ink extends Element {
            public Ink() {
                super("Ink");
            }
        }

        public static class Papyrus extends Element {
            public Papyrus() {
                super("Papyrus");
            }
        }
    }
}
