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

import groovy.transform.CompileStatic
import org.groocss.Color
import org.groocss.Config
import org.groocss.GrooCSS
import org.groocss.KeyFrames
import org.groocss.MediaCSS
import org.groocss.StyleGroup

/**
 * Provides string extensions for GrooCSS to allow for static compilation and code completion.
 */
@CompileStatic
class StringExtension {

    /**
     * Useful for starting a GrooCSS DSL without importing anything ('main.css'.groocss { CSS DSL }).
     * To use config use: 'main.css'.groocss(new Config()) { CSS DSL }.
     **/
    static GrooCSS groocss(String string, Config config = new Config(),
                           @DelegatesTo(value=GrooCSS, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        println "processing $string"
        GrooCSS.process(config, closure)
    }

    /** Useful for image urls: allows 'images/image.png'.url syntax. */
    static String getUrl(String str) { 'url(' + str + ')' }

    /** Useful for colors: allows 'aabbcc'.color or even 'abc'.color syntax (color hex value). */
    static Color getColor(String str) { new Color(str) }
    static Color toColor(String str) { getColor(str) }

    /** Used for creating style-groups using selector: allows 'body div.style'.sg {} syntax. */
    static StyleGroup sg(String selector,
                         @DelegatesTo(value = StyleGroup, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        GrooCSS main = GrooCSS.threadLocalInstance.get()
        StyleGroup sg = new StyleGroup(selector, main.currentCss.config, main.currentCss)
        closure.delegate = sg
        closure(sg)
        main.currentCss.add sg
        sg
    }

    /** Used for creating style-groups using id selector: allows 'your_id'.id {} syntax. */
    static StyleGroup id(String selector,
                         @DelegatesTo(value = StyleGroup, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        sg('#' + selector, closure)
    }

    /** Used for creating style-groups using id selector: allows 'a.selector'.$ {} syntax. */
    static StyleGroup $(String selector,
                        @DelegatesTo(value = StyleGroup, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        sg(selector, closure)
    }

    /** Used for creating keyframes using name. */
    static KeyFrames kf(String name,
                        @DelegatesTo(value = StyleGroup, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        keyframes(name, closure)
    }

    /** Used for creating keyframes using name. */
    static KeyFrames keyframes(String name,
                               @DelegatesTo(value = StyleGroup, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        GrooCSS.threadLocalInstance.get().keyframes(name, closure)
    }

    static MediaCSS media(String mediaRule,
                          @DelegatesTo(value=GrooCSS, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        GrooCSS.threadLocalInstance.get().media(mediaRule, closure)
    }

}
