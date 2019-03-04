package org.groocss.valid

import groovy.transform.CompileStatic
import org.groocss.CSSPart

/** Interface for defining a custom Processor that can modify or validate GrooCSS input. */
@CompileStatic
interface Processor<T extends CSSPart> {

    /** Thrown for methods that are not implemented by the processor. */
    class NotImplementedException extends RuntimeException {
    }

    /** Enum of phases for which this Processor should be called. */
    enum Phase { PRE_VALIDATE, VALIDATE, POST_VALIDATE }

    /**
     * Returns empty if valid, otherwise returns an optional containing an error string.
     *
     * @param
     */
    Optional<String> process(T cssPart, Phase phase)

}
