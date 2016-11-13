package org.raml.utilities.tuples;

public class ImmutablePair<L, R> implements Pair<L, R> {

    private final L left;
    private final R right;

    private ImmutablePair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * @param left Left value, can be null
     * @param right Right value, can be null
     * @return a new {@link ImmutablePair} with the given fields.
     */
    public static <L, R> ImmutablePair<L, R> create(L left, R right) {
        return new ImmutablePair<>(left, right);
    }

    @Override
    public L getLeft() {
        return left;
    }

    @Override
    public R getRight() {
        return right;
    }
}
