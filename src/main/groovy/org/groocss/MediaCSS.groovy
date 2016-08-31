package org.groocss

/**
 * Root node for CSS which might be characterized by a media type. Created by adavis on 8/10/16.
 */
class MediaCSS {

    /** Media rule for when to use this css. Optional- null for root node. */
    final String mediaRule

    /** List of @font-face elements. */
    List<FontFace> fonts = []

    /** List of style groups. */
    List<StyleGroup> groups = []

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

    MediaCSS leftShift(StyleGroup sg) { add sg; this }
    MediaCSS add(StyleGroup sg) { groups << sg; this }
    MediaCSS leftShift(KeyFrames kf) { add kf; this }
    MediaCSS add(KeyFrames kf) { kfs << kf; this }
    MediaCSS leftShift(FontFace ff) { add ff; this }
    MediaCSS add(FontFace ff) { fonts << ff; this }

    MediaCSS leftShift(MediaCSS mediaCSS) { add mediaCSS; this }
    MediaCSS add(MediaCSS mediaCSS) { otherCss << mediaCSS; this }

    MediaCSS add(Collection coll) {
        coll.each {
            if (it instanceof StyleGroup) add((StyleGroup) it)
            if (it instanceof KeyFrames) add((KeyFrames) it)
            if (it instanceof FontFace) add((FontFace) it)
            if (it instanceof MediaCSS) add((MediaCSS) it)
        }
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

        if (otherCss)
            otherCss.each {
                it.writeTo(writer)
                writer.append(sn)
            }
    }

    /** Creates a new StyleGroup element and runs given closure on it. */
    StyleGroup sel(String selector, @DelegatesTo(StyleGroup) Closure<StyleGroup> closure) {
        StyleGroup sg = new StyleGroup(selector, config, this)
        closure.delegate = sg
        closure(sg)
        add sg
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
        new Selector(".$name")
    }
}
