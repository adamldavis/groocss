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
import org.groocss.valid.DefaultValidator
import org.groocss.proc.Processor

import java.lang.reflect.ParameterizedType

/**
 * Root node for CSS which might be characterized by a media type. Created by adavis on 8/10/16.
 */
@CompileStatic
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
        else if (it instanceof MediaCSS) {
            def mediaCSS = (MediaCSS) it
            if (mediaCSS.mediaRule) otherCss << mediaCSS
            else addInternalsFrom(mediaCSS)
        }
        else groups << it
        this
    }

    void addInternalsFrom(MediaCSS mediaCSS) {
        groups.addAll mediaCSS.groups
        kfs.addAll mediaCSS.kfs
        fonts.addAll mediaCSS.fonts
        otherCss.addAll mediaCSS.otherCss
    }

    MediaCSS add(Collection<? extends CSSPart> coll) {
        coll.each {add(it)}
        this
    }
    MediaCSS addAll(Collection<? extends CSSPart> coll) { add(coll) }
    MediaCSS leftShift(Collection<? extends CSSPart> coll) { add(coll) }

    @CompileStatic
    String toString() {
        StringBuilder sb = new StringBuilder()
        writeTo sb
        sb.toString()
    }

    @CompileStatic
    void doProcessing() {
        if (config.processors == null) config.processors = []
        if (!(config.processors.any { it.class == DefaultValidator.class })) {
            config.processors.add new DefaultValidator()
        }
        doProcessingOf(Processor.Phase.PRE_VALIDATE)
        doProcessingOf(Processor.Phase.VALIDATE)
        doProcessingOf(Processor.Phase.POST_VALIDATE)
    }

    @CompileStatic
    def handleErrors(List<String> errors) {
        throw new AssertionError((Object) "There were errors: $errors")
    }

    @CompileStatic
    private List<String> doProcessingOf(Processor.Phase phase) {
        List<String> errors = []
        List<? extends CSSPart> parts = []
        config.processors.forEach { Processor proc ->
            Class<CSSPart> type = ((ParameterizedType) proc.class.genericInterfaces[0]).actualTypeArguments[0]
            switch (type) {
                case Style: groups.findAll{it instanceof StyleGroup}.each { parts.addAll(((StyleGroup)it).styleList) }
                    break
                case StyleGroup: parts.addAll groups.findAll{it instanceof StyleGroup}; break
                case Raw: parts.addAll groups.findAll{it instanceof Raw}; break
                case Comment: parts.addAll groups.findAll{it instanceof Comment}; break
                case MediaCSS: parts.addAll otherCss; break
                case FontFace: parts.addAll fonts; break
                case KeyFrames: parts.addAll kfs; break
                default: parts.addAll groups; break
            }
            parts.each { proc.process(it, phase).ifPresent(errors.&add) }
        }
        if (errors) handleErrors(errors)
        errors
    }

    @CompileStatic
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
    StyleGroup sel(String selector,
                   @DelegatesTo(value=StyleGroup, strategy = Closure.DELEGATE_FIRST) Closure<StyleGroup> closure,
                   boolean addIt = true) {
        StyleGroup sg = new StyleGroup(selector, config, this)
        closure.delegate = sg
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure(sg)
        if (addIt) add sg
        sg
    }

    /** Creates a new StyleGroup element and runs given closure on it. */
    StyleGroup sg(String selector, @DelegatesTo(value=StyleGroup, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        sel(selector, closure)
    }

    @Override
    boolean isEmpty() {
        fonts.empty && groups.empty && kfs.empty && otherCss.empty
    }
}
