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

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import groovy.transform.MapConstructor
import groovy.transform.builder.Builder
import org.groocss.valid.Processor

/**
 * Configuration for GrooCSS conversions. There are at least four different ways to configure GrooCSS:
 *
 * <li>Using the groovy constructor: new Config(compress: true)
 * <li>Using the builder syntax: Config.builder().compress(true).build()
 * <li>Using the DSL: GrooCSS.withConfig { noExts().compress().utf8() }...
 * <li>Using StringExtension with config use: 'main.css'.groocss(new Config()) { ... }.
 *
 * @see GrooCSS
 * @see org.groocss.ext.StringExtension
 */
@MapConstructor
@Canonical
@Builder
@CompileStatic
class Config {

    boolean addWebkit = true,
            addMs = true,
            addOpera = true,
            addMoz = true,
            prettyPrint = false,
            compress = false

    String charset = null

    /** Element-names that you only want to use as CSS classes. */
    Set styleClasses = []

    /** Whether or not convert under-scores in CSS classes into dashes (main_content becomes main-content).
     * Default is false. */
    boolean convertUnderline = false

    /** Custom processors/validators to use.
     * @see Processor
     * @see org.groocss.valid.DefaultValidator
     */
    Set<Processor> processors = []

    Config() {}

    Config withProcessors(Collection<Processor> list) {
        processors.addAll(list)
        this
    }

    /** Add one Element-name, like 'link', that you only want to use as CSS class. */
    Config useAsClass(String name) {
        styleClasses.add name
        this
    }
    /** Add Element-names, like 'link', that you only want to use as CSS classes. */
    Config useAsClasses(Collection classes) {
        styleClasses.addAll classes
        this
    }

    /** Set the convertUnderline flag to true. */
    Config convertUnderline() {
        convertUnderline = true
        this
    }

    /** Sets the compress flag to true. */
    Config compress() {
        compress = true
        this
    }

    /** Sets the prettyPrint flag to true. */
    Config prettyPrint() {
        prettyPrint = true
        this
    }

    /** Sets all extension adding flags to false. */
    Config noExts() {
        addWebkit = addMs = addOpera = addMoz = false
        this
    }

    /** Sets all extension adding flags to false except webkit. */
    Config onlyWebkit() {
        noExts(); addWebkit = true
        this
    }

    /** Sets all extension adding flags to false except ms. */
    Config onlyMs() {
        noExts(); addMs = true
        this
    }

    /** Sets all extension adding flags to false except opera. */
    Config onlyOpera() {
        noExts(); addOpera = true
        this
    }

    /** Sets all extension adding flags to false except moz. */
    Config onlyMoz() {
        noExts(); addMoz = true
        this
    }

    /** Sets the charset to UTF-8. */
    Config utf8() {
        charset = 'UTF-8'
        this
    }
}