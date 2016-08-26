package org.groocss

import groovy.transform.*

/**
 * Represents a CSS pseudo-class such as :active, :focus, or :nthChild(odd).
 */
@TypeChecked
@TupleConstructor
class PseudoClass {

    /** Only here to restrict the DSL so that pseudo-class is used properly. */
    @InheritConstructors
    static class StyleGroup extends org.groocss.StyleGroup {}

    String value

    /** Allows chainable pseudo-classes (%active %hover becomes :active:hover). */
    PseudoClass mod(PseudoClass other) {
        new PseudoClass(this.value + ":$other.value")
    }

    String toString() {
        ":$value"
    }
}
