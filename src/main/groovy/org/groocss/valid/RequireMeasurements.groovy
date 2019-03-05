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

/** Validates that every value that can be a Measurement is one.
 * @see Measurement
 */
@CompileStatic
class RequireMeasurements extends AbstractValidator<Style> implements Processor<Style> {

    static final Set<String> names = new HashSet<>(DefaultValidator.sizeNames + DefaultValidator.timeNames)

    @Override
    Optional<String> validate(Style style) {

        if (names.contains(style.name) && !(style.value instanceof Measurement)) {
            return Optional.of("$style.name with value $style.value is not a Measurement".toString())
        }
        Optional.empty()
    }
}
