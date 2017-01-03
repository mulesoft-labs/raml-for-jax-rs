package org.raml.jaxrs.generator;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by Jean-Philippe Belanger on 12/10/16.
 * Just potential zeroes and ones
 */
public class TypeFindingListener implements GFinderListener {
    private final Map<String, GeneratorType> foundTypes;

    public TypeFindingListener(Map<String, GeneratorType> foundTypes) {
        this.foundTypes = foundTypes;
    }

    @Override
    public void newTypeDeclaration(GType typeDeclaration) {

        GeneratorType generator = GeneratorType.generatorFrom(typeDeclaration);
        foundTypes.put(typeDeclaration.name(), generator);
    }


}
