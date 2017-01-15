package org.raml.jaxrs.generator;

/**
 * Created by Jean-Philippe Belanger on 1/15/17.
 * Just potential zeroes and ones
 */
public interface NameFixer {

    NameFixer CAMEL_LOWER = new NameFixer() {
        @Override
        public String fixFirst(String name) {

            return Character.toLowerCase(name.charAt(0)) + name.substring(1);
        }

        @Override
        public String fixOthers(String name) {
            return CAMEL_UPPER.fixOthers(name);
        }
    };

    NameFixer CAMEL_UPPER = new NameFixer() {
        @Override
        public String fixFirst(String name) {

            return Character.toUpperCase(name.charAt(0)) + name.substring(1);
        }

        @Override
        public String fixOthers(String name) {
            return fixFirst(name);
        }
    };

    NameFixer ALL_UPPER = new NameFixer() {
        @Override
        public String fixFirst(String name) {

            return name.toUpperCase();
        }

        @Override
        public String fixOthers(String name) {
            return name.toUpperCase();
        }
    };

    String fixFirst(String name);
    String fixOthers(String name);

}
