package org.groocss

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

/**
 * Created by adavis on 8/9/17.
 */
@TupleConstructor
@EqualsAndHashCode
@CompileStatic
class Raw implements CSSPart {
    String rawCss
    boolean isEmpty() { rawCss == '' }
    String toString() {rawCss}
}
