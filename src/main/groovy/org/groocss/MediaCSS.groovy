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

/**
 * Root node for CSS which might be characterized by a media type. Created by adavis on 8/10/16.
 */
class MediaCSS implements CSSPart {

    /** Media rule for when to use this css. Optional- null for root node. */
    final String mediaRule

    /** List of @font-face elements. */
    List<FontFace> fonts = []

    /** List of style groups, comments, raws. */
    List<CSSPart> groups = []

    /** List of @keyframes. */
    List<KeyFrames> kfs = []

    /** Other @media branches if they exist. */
    List<MediaCSS> otherCss = []

    Config config

    final CurrentKeyFrameHolder keyFrameHolder

    MediaCSS(CurrentKeyFrameHolder keyFrameHolder) {
        this.keyFrameHolder = keyFrameHolder
        this.mediaRule = null
    }
    MediaCSS(CurrentKeyFrameHolder keyFrameHolder, String mediaRule = null, Config config1) {
        this(keyFrameHolder)
        this.mediaRule = mediaRule
        this.config = config1
    }

    KeyFrames getCurrentKeyFrames() { keyFrameHolder.currentKf }
    void setCurrentKeyFrames(KeyFrames kf) { keyFrameHolder.currentKf = kf }

    MediaCSS leftShift(CSSPart sg) { add sg }
    MediaCSS add(CSSPart it) {
        if (it instanceof KeyFrames) kfs << ((KeyFrames) it)
        else if (it instanceof FontFace) fonts << ((FontFace) it)
        else if (it instanceof MediaCSS) otherCss << ((MediaCSS) it)
        else groups << it
        this
    }

    MediaCSS add(Collection<? extends CSSPart> coll) {
        coll.each {add(it)}
        this
    }
    MediaCSS addAll(Collection<? extends CSSPart> coll) { add(coll) }
    MediaCSS leftShift(Collection<? extends CSSPart> coll) { add(coll) }

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
        if (args.length > 0 && args[0] instanceof Closure) sg(".$name", (Closure) args[0])
    }

    /** Allows _.styleClass syntax to be used anywhere selectors are used. */
    def propertyMissing(String name) {
        new Selector(".$name", this)
    }

    @Override
    boolean isEmpty() {
        fonts.empty && groups.empty && kfs.empty && otherCss.empty
    }
}
