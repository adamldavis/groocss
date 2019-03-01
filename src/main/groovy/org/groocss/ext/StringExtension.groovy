package org.groocss.ext

import groovy.transform.CompileStatic
import org.groocss.Color
import org.groocss.GrooCSS
import org.groocss.KeyFrames
import org.groocss.MediaCSS
import org.groocss.StyleGroup

@CompileStatic
class StringExtension {

    /** Useful for image urls: allows 'images/image.png'.url syntax. */
    static String getUrl(String str) { 'url(' + str + ')' }

    /** Useful for colors: allows 'aabbcc'.color or even 'abc'.color syntax (color hex value). */
    static Color getColor(String str) { new Color(str) }
    static Color toColor(String str) { getColor(str) }

    /** Used for creating style-groups using selector: allows 'body div.style'.sg {} syntax. */
    static StyleGroup sg(String selector, @DelegatesTo(StyleGroup) Closure closure) {
        GrooCSS main = GrooCSS.threadLocalInstance.get()
        StyleGroup sg = new StyleGroup(selector, main.currentCss.config, main.currentCss)
        closure.delegate = sg
        closure(sg)
        main.currentCss.add sg
        sg
    }

    /** Used for creating style-groups using id selector: allows 'your_id'.id {} syntax. */
    static StyleGroup id(String selector, @DelegatesTo(StyleGroup) Closure closure) {
        sg('#' + selector, closure)
    }

    /** Used for creating style-groups using id selector: allows 'a.selector'.$ {} syntax. */
    static StyleGroup $(String selector, @DelegatesTo(StyleGroup) Closure closure) {
        sg(selector, closure)
    }

    /** Used for creating keyframes using name. */
    static KeyFrames kf(String name, @DelegatesTo(StyleGroup) Closure closure) {
        keyframes(name, closure)
    }

    /** Used for creating keyframes using name. */
    static KeyFrames keyframes(String name, @DelegatesTo(StyleGroup) Closure closure) {
        GrooCSS.threadLocalInstance.get().keyframes(name, closure)
    }

    static MediaCSS media(String mediaRule, @DelegatesTo(GrooCSS) Closure closure) {
        GrooCSS.threadLocalInstance.get().media(mediaRule, closure)
    }

}
