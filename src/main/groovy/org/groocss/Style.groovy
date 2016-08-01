package org.groocss

import groovy.transform.*
import java.util.regex.*

/** Represents a CSS style. */
@CompileStatic
class Style {


    String name, value
    
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
