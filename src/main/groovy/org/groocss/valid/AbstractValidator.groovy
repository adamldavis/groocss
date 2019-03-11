package org.groocss.valid

import groovy.transform.CompileStatic
import org.groocss.CSSPart
import org.groocss.proc.Processor

/**
 * Convenient abstract class for extending and making Validators but not necessary to extend.
 * Just checks that phase is VALIDATE Phase and if so calls validate method.
 * @see Processor
 * @see DefaultValidator
 */
@CompileStatic
abstract class AbstractValidator<T extends CSSPart> implements Processor<T> {

    /** Implements the interface's process method. */
    @Override
    Optional<String> process(T cssPart, Phase phase) {
        if (phase == Phase.VALIDATE) return validate(cssPart)
        else return Optional.empty()
    }

    /** Returns empty if no problem, otherwise returns Optional wrapped error message.*/
    abstract Optional<String> validate(T style)

}
