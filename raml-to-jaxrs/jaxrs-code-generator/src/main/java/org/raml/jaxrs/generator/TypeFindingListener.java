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
    private final Multimap<String, GType> childMap;

    public TypeFindingListener(Map<String, GeneratorType> foundTypes, Multimap<String, GType> childMap) {
        this.foundTypes = foundTypes;
        this.childMap = childMap;
    }

    @Override
    public void newTypeDeclaration(GType typeDeclaration) {

        GeneratorType generator = GeneratorType.generatorFrom(typeDeclaration);
        if (! typeDeclaration.isInline() ) {

            addChildToParent(typeDeclaration.parentTypes(), typeDeclaration, childMap);
        }
        foundTypes.put(typeDeclaration.name(), generator);
    }

    private void addChildToParent(List<GType> parents, GType child, Multimap<String, GType> mm) {

        for (GType parent : parents) {
            mm.put(parent.name(), child);
            addChildToParent(parent.parentTypes(), child, mm);
        }
    }
}
