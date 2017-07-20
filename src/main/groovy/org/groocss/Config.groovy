package org.groocss

import groovy.transform.Canonical
import groovy.transform.builder.Builder

/**
 * Configuration for GrooCSS conversions.
 */
@Canonical
@Builder
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

    Config() {}

    Config(Map map) {
        this.properties.keySet().each { if (it != 'class' && map.containsKey(it)) this[it] = map[it] }
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
