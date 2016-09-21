package org.groocss

/**
 * Anything that has a selector. Created by adavis on 9/20/16.
 */
class Selectable {

    /** Complete selector used by this Selectable thing. */
    String selector

    /** Fluent way to set the selector and return this entity. */
    Selectable resetSelector(String sel) {
        selector = sel
        this
    }

    String toString() {selector}

    @Override
    boolean equals(other) {
        (other instanceof Selectable) ? selector == other.selector : false
    }
    @Override
    int hashCode() { selector.hashCode() }
}
