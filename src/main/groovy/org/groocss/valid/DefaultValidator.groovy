/*
   Copyright 2019 Adam L. Davis

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
limitations under the License.
 */
package org.groocss.valid

import groovy.transform.CompileStatic
import org.groocss.Measurement
import org.groocss.Style

/**
 * Does default GrooCSS validation of Measurements.
 * <p>Makes sure that time values are time Measurements and size values (such as top, left, width, and fontSize)
 * are size values such as 1.px or 1.em or 10%.
 *
 * @see org.groocss.Config
 * @see RequireMeasurements
 */
@CompileStatic
class DefaultValidator extends AbstractValidator<Style> implements Processor<Style> {

    static final List<String> timeNames = ['animationDuration', 'animationDelay',
                                           'transitionDelay', 'transitionDuration']

    static final List<String> sizeNames = ['top', 'left', 'right', 'width', 'height',
                                           'padding', 'margin', 'paddingBottom', 'paddingLeft',
                                           'paddingRight', 'paddingTop', 'marginBottom', 'marginTop',
                                           'marginLeft', 'marginRight', 'minWidth', 'minHeight',
                                           'maxWidth', 'maxHeight', 'fontSize',
                                           'borderBottomWidth', 'borderLeftWidth', 'borderRightWidth',
                                           'borderTopWidth']

    @Override
    Optional<String> validate(Style style) {
        //println "inside validate $style ${style.value instanceof Measurement}"
        if (style.value instanceof Measurement) {
            def x = (Measurement) style.value

            switch (style.name) {
                case timeNames:
                    if (!x.time)
                        return Optional.of("$style.value is not a time Measurement".toString())
                    break
                case sizeNames:
                    if (!(x.distance || x.relative || x.percent || x.pixel))
                        return Optional.of("$style.value is not a length Measurement".toString())
                    break
            }
        }
        Optional.empty()
    }
}
