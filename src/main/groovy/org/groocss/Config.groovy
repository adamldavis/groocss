package org.groocss

import groovy.transform.Canonical

/**
 * Configuration for GrooCSS conversions.
 */
@Canonical
class Config {

    boolean addWebkit = true,
            addMs = true,
            addOpera = true,
            addMoz = true,
            compress = false

    String charset = null
}
