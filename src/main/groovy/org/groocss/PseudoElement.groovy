package org.groocss

import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

/**
 * Represents a CSS3 Pseudo-element such as "::before", "::after", "::first-letter", "::first-line",
 * and "::placeholder". "::placeholder" is NOT supported by IE as of July 11 2019.
 *
 * @see GrooCSS
 * @since 1.0-M4
 */
@InheritConstructors
@CompileStatic
class PseudoElement extends PseudoClass {

    /** Only here to restrict the DSL so that pseudo-element is used properly. */
    @InheritConstructors
    static class StyleGroup extends org.groocss.StyleGroup {}

    /** Allows this to be chainable to pseudo-classes. */
    PseudoElement mod(PseudoClass other) {
        new PseudoElement(this.value + "$other")
    }

    /** Allows this to be chainable to pseudo-classes. */
    PseudoElement.StyleGroup mod(PseudoClass.StyleGroup other) {
        new PseudoElement.StyleGroup(this.value + "$other", other.config, other.owner)
    }

    @Override
    String toString() {
        return "::$value"
    }
}
