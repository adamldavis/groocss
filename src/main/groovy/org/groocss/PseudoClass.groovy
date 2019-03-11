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
package org.groocss

import groovy.transform.*

/**
 * Represents a CSS pseudo-class such as :active, :focus, or :nthChild(odd).
 * <p>
 * Pseudo classes are appended to selectors using the % operator. They are chainable as well meaning
 * the following is possible: <code>a %active %hover</code> becomes a:active:hover.
 * <p>
 * Special abbreviations exist such as "odd" for ":nthChild(odd)" and "even" for :nthChild(even).
 *
 * @see GrooCSS
 */
@TypeChecked
@TupleConstructor
class PseudoClass {

    /** Only here to restrict the DSL so that pseudo-class is used properly. */
    @InheritConstructors
    static class StyleGroup extends org.groocss.StyleGroup {}

    String value

    /** Allows chainable pseudo-classes (%active %hover becomes :active:hover). */
    PseudoClass mod(PseudoClass other) {
        new PseudoClass(this.value + ":$other.value")
    }

    String toString() {
        ":$value"
    }
}
