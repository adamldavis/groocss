package org.groocss.valid

import groovy.transform.CompileStatic
import org.groocss.Measurement
import org.groocss.Style

/**
 * Does default GrooCSS validation of Measurements.
 */
@CompileStatic
class DefaultValidator extends AbstractValidator<Style> implements Processor<Style> {

    static final List<String> timeNames = ['delay', 'duration', 'animationDuration', 'animationDelay']

    @Override
    Optional<String> validate(Style style) {
        if (style.value instanceof Measurement) {
            def measurement = (Measurement) style.value

            switch (style.name) {
                case timeNames:
                    if (!measurement.time)
                        return Optional.of("$style.value is not a time Measurement".toString())
            }
        }
        Optional.empty()
    }
}
