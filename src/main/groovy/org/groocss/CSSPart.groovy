package org.groocss

import groovy.transform.CompileStatic

/**
 * Represents part of a CSS file. Created by adavis on 10/20/17.
 */
@CompileStatic
interface CSSPart {

    /** Whether or not this part is empty and can be ignored. */
    boolean isEmpty()
}