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
        toDashed(name)
    }

    static String toDashed(String camelCase) {
        Matcher matcher = Pattern.compile('[A-Z]').matcher(camelCase)
        def result = camelCase
        while (matcher.find()) {
            result = result.replace(matcher.group(), '-' + matcher.group().toLowerCase())
        }
        result
    }

}
