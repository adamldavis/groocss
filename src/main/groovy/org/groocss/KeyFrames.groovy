package org.groocss

/**
 * Represents a keyframes block in CSS. Created by adavis on 8/1/16.
 */
class KeyFrames {
    String name
    List<StyleGroup> groups = []
    Config config

    /** Adds a StyleGroup to this keyframes. */
    void leftShift(StyleGroup sg) { groups << sg }

    String toString() {
        if (name) "@keyframes $name {\n${groups.join('\n')}\n}" +
                (config.addWebkit ? "@-webkit-keyframes $name {\n${groups.join('\n')}\n}" : '')
        else groups.join('\n')
    }

    /** Adds a "frame" block for given percent. */
    KeyFrames frame(int percent, @DelegatesTo(StyleGroup) Closure clos) {
        frame([percent], clos)
    }

    /** Adds a "frame" block for given percents. */
    KeyFrames frame(List<Integer> percents, @DelegatesTo(StyleGroup) Closure clos) {
        StyleGroup sg = new StyleGroup(selector: percents.collect{"${it}%"}.join(", "), config: config)
        clos.delegate = sg
        clos()
        this << sg
        this
    }

    /** Adds a "from" block. */
    KeyFrames from(@DelegatesTo(StyleGroup) Closure clos) {
        StyleGroup sg = new StyleGroup(selector: "from", config: config)
        clos.delegate = sg
        clos()
        this << sg
        this
    }

    /** Adds a "to" block. */
    KeyFrames to(@DelegatesTo(StyleGroup) Closure clos) {
        StyleGroup sg = new StyleGroup(selector: "to", config: config)
        clos.delegate = sg
        clos()
        this << sg
        this
    }
}
