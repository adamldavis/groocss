package org.groocss

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

/**
 * Represents a comment to include in output. Created by adavis on 8/9/17.
 */
@TupleConstructor
@EqualsAndHashCode
@CompileStatic
class Comment implements CSSPart {
    String comment
    boolean isEmpty() { comment == '' }
    String toString() { "/* $comment */" }
}
