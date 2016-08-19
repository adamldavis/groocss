package org.groocss

import groovy.transform.TupleConstructor

/**
 * Class representing HTML5 element used by DSL. Used to allow syntax:
 * <PRE>
 *     div.styleClass { color blue }
 * </PRE>
 */
@TupleConstructor
class Element {

    String elementName
    MediaCSS owner

    /** Creates a new StyleGroup element and runs given closure on it. */
    StyleGroup sg(String selector = '', @DelegatesTo(StyleGroup) Closure closure) {
        owner.sg(elementName + selector, closure)
    }

    /** Creates a new StyleGroup element and runs given closure on it. */
    StyleGroup sel(String selector = '', @DelegatesTo(StyleGroup) Closure closure) {
        owner.sg(elementName + selector, closure)
    }

    /** Creates a new StyleGroup element with given style class and runs given closure on it. */
    StyleGroup withClass(String styleClass, @DelegatesTo(StyleGroup) Closure closure) {
        owner.sg("${elementName}.$styleClass", closure)
    }

    /** Creates a new StyleGroup element with given pseudoClass and runs given closure on it. */
    StyleGroup withPseudoClass(String pseudoClass, @DelegatesTo(StyleGroup) Closure closure) {
        owner.sg("${elementName}:$pseudoClass", closure)
    }

    def methodMissing(String methodName, args) {
        if (args[0] instanceof Closure) withClass(methodName, (Closure) args[0])
        null
    }
    
    void putAt(String key, value) {
        if (value instanceof Closure) sel("[$key]", (Closure) value)
    }
}
