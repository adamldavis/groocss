package org.groocss

/**
 * Class representing HTML5 element used by DSL. Used to allow the following syntax and others:
 * <PRE>
 *     div.styleClass { color blue }
 *     div % firstChild { color red }
 * </PRE>
 */
class Selector extends Selectable {

    MediaCSS owner

    Selector(String selector, MediaCSS owner) {
        this.selector = selector
        this.owner = owner
    }

    String getValue() {selector}
    void setValue(String sel) {selector = sel}

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
        else if (args[0] instanceof Selectable) {
            def sg = (Selectable) args[0]
            return sg.resetSelector("${selector}.$methodName $sg.selector")
        }
        else null
    }

    /** Creates a new Selector using the missing property name as the styleClass.
     * Used to allow syntax:
     * <PRE>
     *     div.styleClass { color blue }
     * </PRE>
     */
    def propertyMissing(String name) {
        new Selector("${value}.$name", owner)
    }

    // ---> Map syntax:

    /** Allows CSS-like syntax using attribute selectors. For example: input['class$="test"'] = {} */
    def putAt(String key, value) {
        if (value instanceof Closure) sel("[$key]", (Closure) value)
    }

    /** Allows attribute selector to create a new Element. */
    def getAt(String key) {
        new Selector("$value[$key]", owner)
    }

    // ---> Operators:

    StyleGroup plus(StyleGroup sg) { sg.resetSelector "$value + ${sg.selector}" }
    StyleGroup rightShift(StyleGroup sg) { sg.resetSelector "$value > ${sg.selector}" }
    StyleGroup minus(StyleGroup sg) { sg.resetSelector "$value ~ ${sg.selector}" }
    StyleGroup multiply(StyleGroup sg) { sg.resetSelector "$value * ${sg.selector}" }
    StyleGroup xor(StyleGroup sg) { sg.resetSelector "$value ${sg.selector}" }
    StyleGroup or(StyleGroup sg) { sg.resetSelector "$value,${sg.selector}" }
    StyleGroup and(StyleGroup sg) { or(sg) }

    Selector plus(Selector e) { new Selector("$value + $e.value", owner) }
    Selector rightShift(Selector e) { new Selector("$value > $e.value", owner) }
    Selector minus(Selector e) { new Selector("$value ~ $e.value", owner) }
    Selector multiply(Selector e) { new Selector("$value * $e.value", owner) }
    Selector xor(Selector e) { new Selector("$value $e.value", owner) }
    Selector or(Selector e) { new Selector("$value,$e.value", owner) }
    Selector and(Selector e) { or(e) }

    Selectable plus(e) { if (e instanceof Selector) plus((Selector) e); if (e instanceof StyleGroup) plus((StyleGroup) e) }
    Selectable rightShift(e) {
        if (e instanceof Selector) rightShift((Selector) e); if (e instanceof StyleGroup) rightShift((StyleGroup) e)}
    Selectable minus(e) { if (e instanceof Selector) minus((Selector) e); if (e instanceof StyleGroup) minus((StyleGroup) e)}
    Selectable multiply(e) { if (e instanceof Selector) multiply((Selector) e); if (e instanceof StyleGroup) multiply((StyleGroup) e)}
    Selectable xor(e) { if (e instanceof Selector) xor((Selector) e); if (e instanceof StyleGroup) xor((StyleGroup) e)}
    Selectable or(e) { if (e instanceof Selector) or((Selector) e); if (e instanceof StyleGroup) or((StyleGroup) e)}
    Selectable and(e) { if (e instanceof Selector) and((Selector) e); if (e instanceof StyleGroup) and((StyleGroup) e)}

    String toString() { value }

    /** Adds the given PseudoClass's value to this selector. Allows syntax: input % hover. */
    def mod(PseudoClass pc) {
        new Selector("$value$pc", owner)
    }

    /** Prepends the value of this selector to the given styleGroup's selector. Allows syntax: input % hover {...}. */
    def mod(PseudoClass.StyleGroup styleGroup) {
        styleGroup.resetSelector(value + styleGroup.selector)
        styleGroup
    }

    Selector bitwiseNegate() { new Selector("~ $value", owner) }

    Selector call(Selector other) { new Selector("$value $other.value", owner) }

    StyleGroup call(StyleGroup sg) { sg.resetSelector "$value $sg.selector" }
    
}
