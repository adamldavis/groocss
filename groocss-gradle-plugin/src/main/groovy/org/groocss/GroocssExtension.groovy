package org.groocss

/** Extension to Gradle for configuring GrooCSS. */
class GroocssExtension {

    boolean addWebkit = true,
            addMs = true,
            addOpera = true,
            addMoz = true,
            prettyPrint = false,
            compress = false

    String charset = null

}
