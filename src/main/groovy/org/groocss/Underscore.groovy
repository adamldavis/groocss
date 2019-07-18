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

import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor

/** Allows _.styleClass syntax to be used anywhere selectors are used. */
@TupleConstructor
class Underscore {

    final GrooCSS grooCSS

    /** Allows _.styleClass syntax to be used anywhere selectors are used. */
    @CompileStatic
    def propertyMissing(String name) {
        new Selector(".$name", grooCSS.currentCss)
    }

    /** Allows _** syntax for pseudo-elements like ::before and ::after. */
    StyleGroup power(PseudoElement.StyleGroup styleGroup) {
        return styleGroup
    }

    /** Allows _% syntax for pseudo-classes like :active and :hover. */
    StyleGroup mod(PseudoClass.StyleGroup styleGroup) {
        return styleGroup
    }

    def methodMissing(String name, args) {
        if (args.length > 0 && args[0] instanceof Closure)
            grooCSS.sg(".$name", (Closure) args[0])

        else if (args.length == 1 && args[0] instanceof Selector)
            new Selector(".$name", grooCSS.currentCss) ^ args[0]
        // _.name(a)

        else if (args.length > 1 && args.every {it instanceof Selector})
            new Selector([".$name"] + (args as List), grooCSS.currentCss)
        // _.name(a, b)

        else if (args.length == 2  && args[0] instanceof Selector && args[1] instanceof Closure)
            (new Selector(".$name", grooCSS.currentCss) ^ ((Selector) args[0])).sg((Closure) args[1])
        // _.name(a) { css }

        else if (args.length > 0 && args[-1] instanceof Closure && args[0..-1].every {it instanceof Selector})
            grooCSS.sg([".$name"] + (args[0..-1] as List), (Closure) args[-1])
        // _.name(a, b) { css }
    }
}
