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
package org.groocss.ext

import org.groocss.Color
import org.groocss.GrooCSS
import org.groocss.KeyFrames
import org.groocss.Measurement as M
import org.groocss.StyleGroup
import org.groocss.Underscore

/**
 * Extends Number and Integer classes allowing creation of Measurements, Colors and Keyframes from numbers.
 *
 * Allows syntax like 12.px (meaning 12 pixels) for CSS values.
 */
class NumberExtension {

    static M getMs (Number n) { new M(n, 'ms') }
    static M getS (Number n) { new M(n, 's') }
    static M getPt (Number n) { new M(n, 'pt') }
    static M getPc (Number n) { new M(n, 'pc') }
    static M getMm (Number n) { new M(n, 'mm') }
    static M getCm (Number n) { new M(n, 'cm') }
    static M getIn (Number n) { new M(n, 'in') }
    static M getM (Number n) { new M(n, 'm') }
    static M getRad (Number n) { new M(n, 'rad') }
    static M getDeg (Number n) { new M(n, 'deg') }
    static M getEm (Number n) { new M(n, 'em') }
    static M getEx (Number n) { new M(n, 'ex') }
    static M getPx (Number n) { new M(n, 'px') }
    static M getCh (Number n) { new M(n, 'ch') }
    static M getRem (Number n) { new M(n, 'rem') }
    static M getVh (Number n) { new M(n, 'vh') }
    static M getVw (Number n) { new M(n, 'vw') }
    static M getVmin (Number n) { new M(n, 'vmin') }
    static M getVmax (Number n) { new M(n, 'vmax') }

    static M getPercent (Number n) { new M(n, '%') }
    static M mod(Number n, Underscore underscore) { new M(n, '%') }

    /** Useful for colors: allows 0xaabbcc.color syntax (color hex value). */
    static Color getColor(Integer n) { new Color(n) }

    /** Useful for colors: allows 0xaabbcc.toColor() syntax (color hex value). */
    static Color toColor(Integer n) { new Color(n) }

    /** Used within keyframes block such as 50% { opacity: 1 }. */
    static KeyFrames mod(Integer n,
                         @DelegatesTo(value=StyleGroup, strategy = Closure.DELEGATE_FIRST) Closure frameCl) {
        def css = GrooCSS.threadLocalInstance.get()
        if (css) css.currentKf.frame(n, frameCl)
    }
}
