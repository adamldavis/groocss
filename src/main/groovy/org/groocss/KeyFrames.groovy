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

import groovy.transform.TypeChecked

/**
 * Represents a keyframes block in CSS. Created by adavis on 8/1/16.
 */
@TypeChecked
class KeyFrames implements CSSPart {
    String name
    List<StyleGroup> groups = []
    Config config

    /** Adds a StyleGroup to this keyframes. */
    void leftShift(StyleGroup sg) { groups << sg }
    void add(StyleGroup sg) { groups << sg }

    String toString() {
        def delim = config.compress ? '' : (config.prettyPrint ? '\n    ' : '\n')
        def newline = config.compress ? '' : '\n'
        def joined = config.prettyPrint ?
                groups.collect{ "$it".replace(' '*4,' '*8).replace('}','    }') }.join(delim) :
                groups.join(delim)

        if (name) "@keyframes $name {$delim$joined$newline}" +
                (config.addWebkit ? "@-webkit-keyframes $name {$delim$joined$newline}" : '')
        else groups.join(newline)
    }

    /** Adds a "frame" block for given percent. */
    KeyFrames frame(int percent, @DelegatesTo(StyleGroup) Closure clos) {
        frame([percent], clos)
    }

    /** Adds a "frame" block for given percents. */
    KeyFrames frame(List<Integer> percents, @DelegatesTo(StyleGroup) Closure clos) {
        StyleGroup sg = new StyleGroup(percents.collect{"${it}%"}.join(", "), config, null)
        callWithDelegate(clos, sg)
        this << sg
        this
    }

    /** Adds a "from" block. */
    KeyFrames from(@DelegatesTo(StyleGroup) Closure clos) {
        StyleGroup sg = new StyleGroup("from", config, null)
        callWithDelegate(clos, sg)
        this << sg
        this
    }

    /** Adds a "to" block. */
    KeyFrames to(@DelegatesTo(StyleGroup) Closure clos) {
        StyleGroup sg = new StyleGroup("to", config, null)
        callWithDelegate(clos, sg)
        this << sg
        this
    }

    private void callWithDelegate(@DelegatesTo(StyleGroup) Closure clos, StyleGroup sg) {
        clos.delegate = sg
        clos.resolveStrategy = Closure.DELEGATE_FIRST
        clos()
    }

    boolean isEmpty() {
        groups.empty
    }
}
