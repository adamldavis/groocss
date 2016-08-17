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
    void add(StyleGroup sg) { groups << sg }

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
        StyleGroup sg = new StyleGroup(percents.collect{"${it}%"}.join(", "), config, null)
        clos.delegate = sg
        clos()
        this << sg
        this
    }

    /** Adds a "from" block. */
    KeyFrames from(@DelegatesTo(StyleGroup) Closure clos) {
        StyleGroup sg = new StyleGroup("from", config, null)
        clos.delegate = sg
        clos()
        this << sg
        this
    }

    /** Adds a "to" block. */
    KeyFrames to(@DelegatesTo(StyleGroup) Closure clos) {
        StyleGroup sg = new StyleGroup("to", config, null)
        clos.delegate = sg
        clos()
        this << sg
        this
    }
}
