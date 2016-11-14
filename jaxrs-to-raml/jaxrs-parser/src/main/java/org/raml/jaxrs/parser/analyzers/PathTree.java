package org.raml.jaxrs.parser.analyzers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.jcip.annotations.NotThreadSafe;

import org.glassfish.jersey.server.model.Resource;
import org.raml.jaxrs.model.Path;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@NotThreadSafe
class PathTree {

    private final Path path;
    private final Set<PathTree> children;
    //TODO: those can probably go now.
    private final Map<Path, PathTree> childrenPerPaths;
    private final List<Resource> correspondingResources;

    private PathTree(Path path, List<Resource> resources) {
        this.path = path;
        this.children = Sets.newHashSet();
        this.childrenPerPaths = Maps.newHashMap();
        this.correspondingResources = resources;
    }

    public static PathTree create(Path path, List<Resource> resources) {
        checkNotNull(path);

        return new PathTree(path, ImmutableList.copyOf(resources));
    }

    public Path getPath() {
        return path;
    }

    public PathTree getChildNodeFor(Path path) {
        return childrenPerPaths.get(path);
    }

    public void appendChild(Path path, List<Resource> resources) {
        checkNotNull(path);
        checkNotNull(resources);
        checkArgument(!hasChild(path), "path %s already a child", path);

        PathTree childNode = PathTree.create(path, resources);

        this.children.add(childNode);
        this.childrenPerPaths.put(path, childNode);
    }

    public boolean hasChild(Path path) {
        return childrenPerPaths.containsKey(path);
    }

    public List<Resource> getCorrespondingResources() {
        return correspondingResources;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public Set<PathTree> getChildren() {
        return children;
    }
}
