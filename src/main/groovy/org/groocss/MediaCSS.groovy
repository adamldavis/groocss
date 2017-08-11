package org.groocss

/**
 * Root node for CSS which might be characterized by a media type. Created by adavis on 8/10/16.
 */
class MediaCSS {

    /** Media rule for when to use this css. Optional- null for root node. */
    final String mediaRule

    /** List of @font-face elements. */
    List<FontFace> fonts = []

    /** List of style groups, comments, raws. */
    List groups = []

    /** List of @keyframes. */
    List<KeyFrames> kfs = []

    /** Other @media branches if they exist. */
    List<MediaCSS> otherCss = []

    Config config

    MediaCSS() { this.mediaRule = null }
    MediaCSS(String mediaRule, Config config1) {
        this.mediaRule = mediaRule
        this.config = config1
    }

    MediaCSS leftShift(sg) { add sg }
    MediaCSS add(it) {
        if (it instanceof KeyFrames) kfs << ((KeyFrames) it)
        else if (it instanceof FontFace) fonts << ((FontFace) it)
        else if (it instanceof MediaCSS) otherCss << ((MediaCSS) it)
        else groups << it
        this
    }

    MediaCSS add(Collection coll) {
        coll.each {add(it)}
        this
    }
    MediaCSS addAll(Collection coll) { add(coll) }
    MediaCSS leftShift(Collection coll) { add(coll) }

    String toString() {
        StringBuilder sb = new StringBuilder()
        writeTo sb
        sb.toString()
    }

    void writeTo(Appendable writer) {
        def sn = config.compress ? '' : '\n'
        def charset = config.charset

        if (!mediaRule && charset) writer.append "@charset \"$charset\";$sn"
        if (fonts) writer.append (fonts.join(sn) + sn)
        if (mediaRule) writer.append(fonts ? sn : '').append "@media $mediaRule {$sn${groups.join(sn)}$sn}"
        else writer.append (groups.findAll{ !it.isEmpty() }.join(sn))
        if (kfs) writer.append((groups || fonts) ? sn : '').append (kfs.join(sn))

        if (otherCss) {
            if (groups || fonts || kfs) writer.append(sn)
            otherCss.each {
                it.writeTo(writer)
                writer.append(sn)
            }
        }
    }

    /** Creates a new StyleGroup element and runs given closure on it. */
    StyleGroup sel(String selector, @DelegatesTo(StyleGroup) Closure<StyleGroup> closure, boolean addIt = true) {
        StyleGroup sg = new StyleGroup(selector, config, this)
        closure.delegate = sg
        closure(sg)
        if (addIt) add sg
        sg
    }

    /** Creates a new StyleGroup element and runs given closure on it. */
    StyleGroup sg(String selector, @DelegatesTo(StyleGroup) Closure closure) {
        sel(selector, closure)
    }

    def methodMissing(String name, args) {
        if (args[0] instanceof Closure) sg(".$name", (Closure) args[0])
    }

    /** Allows _.styleClass syntax to be used anywhere selectors are used. */
    def propertyMissing(String name) {
        new Selector(".$name", this)
    }
}
