package org.groocss

import groovy.transform.TupleConstructor

/**
 * Class representing HTML5 element used by DSL. Used to allow syntax:
 * <PRE>
 *     div.styleClass { color blue }
 * </PRE>
 */
@TupleConstructor
class Selector {

    String value
    MediaCSS owner

    /** Creates a new StyleGroup element and runs given closure on it. */
    StyleGroup sg(String selector = '', @DelegatesTo(StyleGroup) Closure closure) {
        owner.sg(value + selector, closure)
    }

    /** Creates a new StyleGroup element and runs given closure on it. */
    StyleGroup sel(String selector = '', @DelegatesTo(StyleGroup) Closure closure) {
        owner.sg(value + selector, closure)
    }

    /** Creates a new StyleGroup element with given style class and runs given closure on it. */
    StyleGroup withClass(String styleClass, @DelegatesTo(StyleGroup) Closure closure) {
        owner.sg("${value}.$styleClass", closure)
    }

    /** Creates a new StyleGroup element with given pseudoClass and runs given closure on it. */
    StyleGroup withPseudoClass(String pseudoClass, @DelegatesTo(StyleGroup) Closure closure) {
        owner.sg("${value}:$pseudoClass", closure)
    }

    // ---> MethodMissing and PropertyMissing
    /** Creates a new StyleGroup using the missing methodName as the styleClass. */
    def methodMissing(String methodName, args) {
        if (args[0] instanceof Closure) return withClass(methodName, (Closure) args[0])
        else null
    }

    def propertyMissing(String name) {
        new Selector(value: "${value}.$name", owner: owner)
    }

    // ---> Map syntax:

    /** Allows CSS-like syntax using attribute selectors. For example: input['class$="test"'] = {} */
    def putAt(String key, value) {
        if (value instanceof Closure) sel("[$key]", (Closure) value)
    }

    /** Allows attribute selector to create a new Element. */
    def getAt(String key) {
        new Selector(value: "$value[$key]", owner: owner)
    }

    // ---> Operators:

    def plus(StyleGroup sg) { sg.resetSelector "$value + ${sg.selector}" }
    def rightShift(StyleGroup sg) { sg.resetSelector "$value > ${sg.selector}" }
    def minus(StyleGroup sg) { sg.resetSelector "$value ~ ${sg.selector}" }
    def multiply(StyleGroup sg) { sg.resetSelector "$value * ${sg.selector}" }
    def xor(StyleGroup sg) { sg.resetSelector "$value ${sg.selector}" }
    def or(StyleGroup sg) { sg.resetSelector "$value,${sg.selector}" }
    def and(StyleGroup sg) { or(sg) }

    def plus(Selector e) { new Selector(value: "$value + $e.value", owner: owner) }
    def rightShift(Selector e) { new Selector(value: "$value > $e.value", owner: owner) }
    def minus(Selector e) { new Selector(value: "$value ~ $e.value", owner: owner) }
    def multiply(Selector e) { new Selector(value: "$value * $e.value", owner: owner) }
    def xor(Selector e) { new Selector(value: "$value $e.value", owner: owner) }
    def or(Selector e) { new Selector(value: "$value,$e.value", owner: owner) }
    def and(Selector e) { or(e) }

    def plus(e) { if (e instanceof Selector) plus((Selector) e); if (e instanceof StyleGroup) plus((StyleGroup) e) }
    def rightShift(e) { 
        if (e instanceof Selector) rightShift((Selector) e); if (e instanceof StyleGroup) rightShift((StyleGroup) e)}
    def minus(e) { if (e instanceof Selector) minus((Selector) e); if (e instanceof StyleGroup) minus((StyleGroup) e)}
    def multiply(e) { if (e instanceof Selector) multiply((Selector) e); if (e instanceof StyleGroup) multiply((StyleGroup) e)}
    def xor(e) { if (e instanceof Selector) xor((Selector) e); if (e instanceof StyleGroup) xor((StyleGroup) e)}
    def or(e) { if (e instanceof Selector) or((Selector) e); if (e instanceof StyleGroup) or((StyleGroup) e)}
    def and(e) { if (e instanceof Selector) and((Selector) e); if (e instanceof StyleGroup) and((StyleGroup) e)}

    String toString() { value }
}
