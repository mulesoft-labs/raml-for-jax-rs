package org.raml.jaxrs.parser.analyzers;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.PeekingIterator;

import org.glassfish.jersey.server.model.Resource;
import org.raml.jaxrs.model.JaxRsResource;
import org.raml.jaxrs.model.Path;
import org.raml.jaxrs.model.impl.JaxRsResourceImpl;
import org.raml.jaxrs.model.impl.PathImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

class JerseyUniquePathsResourceResolver implements ResourceResolver<Resource> {

    private static final Logger logger = LoggerFactory.getLogger(JerseyUniquePathsResourceResolver.class);

    private JerseyUniquePathsResourceResolver() {
    }

    public static JerseyUniquePathsResourceResolver create() {
        return new JerseyUniquePathsResourceResolver();
    }

    @Override
    public Iterable<JaxRsResource> resolve(Iterable<Resource> resources) {
        return jerseyResourcesToOurs(resources);
    }

    private static Iterable<JaxRsResource> jerseyResourcesToOurs(Iterable<Resource> jerseyResources) {
        //We are going to order all paths lexicographically for later processing.
        SortedMap<Path, List<Resource>> jerseyResourcesPerPath = Maps.newTreeMap(
                new Comparator<Path>() {
                    @Override
                    public int compare(Path left, Path right) {
                        return left.getStringRepresentation().compareTo(right.getStringRepresentation());
                    }
                }
        );

        mapResourcesToUniquePaths(jerseyResourcesPerPath, jerseyResources);

        logger.debug("found {} unique resources", jerseyResourcesPerPath.size());

        //At this point, all the paths are ordered inside the tree, with the shortest paths being first.
        List<PathTree> roots = associateResourcesWithNodes(jerseyResourcesPerPath);


        return mergeResources(roots);
    }

    private static List<PathTree> associateResourcesWithNodes(SortedMap<Path, List<Resource>> jerseyResourcesPerPath) {
        List<PathTree> roots = Lists.newArrayList();

        PeekingIterator<Map.Entry<Path, List<Resource>>> iterator =
                Iterators.peekingIterator(jerseyResourcesPerPath.entrySet().iterator());
        while (iterator.hasNext()) {
            PathTree root = processRoot(iterator);
            roots.add(root);
        }

        return roots;
    }

    private static PathTree processRoot(PeekingIterator<Map.Entry<Path, List<Resource>>> iterator) {
        Map.Entry<Path, List<Resource>> firstEntry = iterator.next();

        Path rootPath = firstEntry.getKey();
        PathTree root = PathTree.create(rootPath, firstEntry.getValue());

        if (iterator.hasNext()) {
            Map.Entry<Path, List<Resource>> currentEntry = iterator.peek();
            while (rootPath.isSuperPathOf(currentEntry.getKey())) {
                iterator.next();
                root.appendChild(rootPath.relativize(currentEntry.getKey()), currentEntry.getValue());
                currentEntry = iterator.peek();
            }
        }

        return root;
    }

    private static void analyzeResource(Path prefix, Map<Path, List<Resource>> jerseyResourcesPerPath, Resource resource) {
        logger.debug("analyzing jersey resource: {}", resource);

        Path path = prefix.resolve(PathImpl.fromString(resource.getPath()));

        if (!jerseyResourcesPerPath.containsKey(path)) {
            jerseyResourcesPerPath.put(path, Lists.<Resource>newArrayList());
        }
        jerseyResourcesPerPath.get(path).add(resource);

        List<Resource> children = resource.getChildResources();
        if (children != null && !children.isEmpty()) {
            logger.debug("found {} children in resource", children.size());
            analyzeChildren(path, jerseyResourcesPerPath, children);
        }
    }

    private static void mapResourcesToUniquePaths(Map<Path, List<Resource>> jerseyResourcesPerPath, Iterable<Resource> resources) {
        logger.debug("about to analyze {} top level jersey resources", Iterables.size(resources));
        for (Resource resource : resources) {
            analyzeResource(PathImpl.empty(), jerseyResourcesPerPath, resource);
        }
    }

    private static void analyzeChildren(Path prefix, Map<Path, List<Resource>> jerseyResourcesPerPath, Iterable<Resource> resources) {
        for (Resource resource : resources) {
            analyzeResource(prefix, jerseyResourcesPerPath, resource);
        }
    }

    private static Iterable<JaxRsResource> mergeResources(Iterable<PathTree> roots) {
        List<JaxRsResource> jaxRsResources = Lists.newArrayList();

        for (PathTree pathTree : roots) {
            jaxRsResources.add(mergeResource(pathTree));
        }

        return jaxRsResources;
    }

    private static JaxRsResource mergeResource(PathTree tree) {
        List<JaxRsResource> children = Lists.newArrayList();

        if (tree.hasChildren()) {

            for (PathTree child : tree.getChildren()) {
                children.add(mergeResource(child));
            }
        }

        return mergeResource(tree.getPath(), tree.getCorrespondingResources(), children);
    }

    private static JaxRsResource mergeResource(Path path, List<Resource> correspondingResources, List<JaxRsResource> children) {
        //TODO: get all the methods here.
        return JaxRsResourceImpl.create(path, children);
    }
}
