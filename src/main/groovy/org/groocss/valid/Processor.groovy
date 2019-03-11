package org.groocss.valid

import groovy.transform.CompileStatic
import org.groocss.CSSPart

/**
 * Interface for defining a custom Processor that can modify or validate GrooCSS input.
 * <p>
 * By extending this interface you can write your own custom validators and add them via {@link org.groocss.Config}.
 * <p>
 * You could also write your own Processor that modifies values in the PRE_VALIDATE phase or any other Phase.
 * For example:<pre><code>
 * class ConvertAllIntsToPixels implements Processor< Style > {
 *     Optional<String> process(Style style, Phase phase) {
 *         if (phase == Phase.PRE_VALIDATE && style.value instanceof Integer) {
 *             style.value = new Measurement(style.value, 'px')
 *         }
 *         return Optional.empty();
 *     }
 * }     </code></pre>
 * @see org.groocss.Config
 * */
@CompileStatic
interface Processor<T extends CSSPart> {

    /** Enum of phases for which this Processor should be called. */
    enum Phase { PRE_VALIDATE, VALIDATE, POST_VALIDATE }

    /**
     * Returns empty if valid, otherwise returns an optional containing an error string.
     *
     * @param
     */
    Optional<String> process(T cssPart, Phase phase)

}
