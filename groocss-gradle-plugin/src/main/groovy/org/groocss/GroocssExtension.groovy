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

    /** Element-names that you only want to use as CSS classes. */
    Set styleClasses = []

    /** Whether or not convert under-scores in CSS classes into dashes (main_content becomes main-content).
     * Default is false. */
    boolean convertUnderline = false
    
}
