package org.raml.jaxrs.generator;

/**
 * Created by Jean-Philippe Belanger on 12/7/16.
 * Just potential zeroes and ones
 */
public enum GObjectType implements GeneratorObjectType {

    PLAIN_OBJECT_TYPE {
        @Override
        public void dispatch(GObjectTypeDispatcher dispatcher) {

            dispatcher.onPlainObject();
        }
    },
    UNION_TYPE {
        @Override
        public void dispatch(GObjectTypeDispatcher dispatcher) {

            dispatcher.onUnion();
        }
    },
    XML_OBJECT_TYPE {
        @Override
        public void dispatch(GObjectTypeDispatcher dispatcher) {
            dispatcher.onXmlObject();
        }
    },
    JSON_OBJECT_TYPE {
        @Override
        public void dispatch(GObjectTypeDispatcher dispatcher) {
            dispatcher.onJsonObject();
        }
    },
    SCALAR {
        @Override
        public void dispatch(GObjectTypeDispatcher dispatcher) {
            throw new GenerationException("scalar object cannot be handled");
        }
    },
    ENUMERATION_TYPE {
        @Override
        public void dispatch(GObjectTypeDispatcher dispatcher) {
            dispatcher.onEnumeration();
        }
    };

    public static class GObjectTypeDispatcher {
        public void onPlainObject() {};
        public void onXmlObject(){};
        public void onJsonObject(){};
        public void onEnumeration(){};
        public void onUnion(){};
    }

    public abstract void dispatch(GObjectTypeDispatcher dispatcher);
}
