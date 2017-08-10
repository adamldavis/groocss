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
class Comment {
    String comment
    boolean isEmpty() { comment == '' }
    String toString() { "/**$comment*/" }
}
