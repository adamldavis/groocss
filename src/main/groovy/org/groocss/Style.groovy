package org.groocss

import groovy.transform.*
import java.util.regex.*

/** Represents a CSS style. */
@CompileStatic
@TupleConstructor
@EqualsAndHashCode
class Style {

    /** Name of the Style (in camel-case) .*/
    String name

    /** Value for this style, as a String. */
    String value
    
    String toString() { nameToDashed() + ": $value;" }
    
    private String nameToDashed() {
        Matcher matcher = Pattern.compile('[A-Z]').matcher(name)
        def result = name
        while (matcher.find()) {
            result = result.replace(matcher.group(), '-' + matcher.group().toLowerCase())
        }
        result
    }

}
