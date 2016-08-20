package org.groocss

import groovy.transform.TypeChecked

import java.util.regex.Matcher

/**
 * Translates from/to CSS.
 */
@TypeChecked
class Translator {

    interface Printer { void println(Object value) }
    interface Reader { void eachLine(Closure closure) }

    /** Converts from CSS to Groocss. */
    static void convertFromCSS(File inf, File out) {
        out.withPrintWriter { pw -> convertFromCSS(inf as Reader, pw as Printer) }
    }

    static String convertFromCSS(String text) {
        StringBuilder sb = new StringBuilder()
        convertFromCSS(new Reader() {
            void eachLine(Closure closure) { text.split('\n').each(closure) }
        }, new Printer() {
            void println(Object value) { sb.append(value).append('\n') }
        })
        sb.toString()
    }

    /** Converts from CSS to Groocss. */
    static void convertFromCSS(Reader inf, Printer pw) {
        inf.eachLine { String originalLine ->
            processLine originalLine, inf, pw
        }
    }

    static void processLine(String originalLine, Reader inf, Printer pw) {
        def line = originalLine.toLowerCase().trim()
        def styleRegex = /[-\w]+\s*:\s*[^\{\};]+;?/
        def frameRegex = /[0-9]+%\s*\{/
        def selector = /[-*#\[,:\]="\w\.]+\s*\{/

        if (line ==~ /(@[-\w]+)(\s+[\w\s]+)?\{/) {
            Matcher m = line =~ /(@[-\w]+)(\s+[\w\s]+)?\{/
            m.find()
            pw.println( nameToCamel(m.group(1)) + (m.group(2) ? " '${m.group(2).trim()}', {" : '{') )
        }
        else if (line ==~ /\w+\s*\{/) pw.println line //just element name
        else if (line ==~ /\.\w+\s*\{/) pw.println "_$originalLine" //just a class
        else if (line ==~ /\w+\.\w+\s*\{/) pw.println line //just element.class
        else if (line ==~ /\w*\.[-\w]+\s*\{/) // class with dashes
            pw.println "sg '${line[0..-2].trim()}', {"
        else if (line ==~ /\w+:\w+\s*\{/) { //pseudo-class
            Matcher m = line =~ /(\w+):(\w+)\s*\{/
            m.find()
            pw.println "${m.group(1)} { ${m.group(2)}()"
        }
        else if (line ==~ /(-webkit-|-ms-|-o-|-moz-)/ + styleRegex) {
            println "warning: skipping: $originalLine"
        }
        else if (line ==~ frameRegex) // selector
            pw.println "frame ${line[0..-2].trim().replace('%','')}, {"
        else if (line ==~ selector) // selector
            pw.println "sg '${line[0..-2].trim()}', {"
        else if (line ==~ /\}/) // close bracket
            pw.println originalLine
        else if (line ==~ styleRegex) { // styles
            def ci = line.indexOf ':'
            def name = nameToCamel line.substring(0, ci).trim()
            def value = line.substring(ci + 1).trim().replace(';', '')
            pw.println((value ==~ /([0-9\.]+)|(rgba?.*)|(white)|(black)/) ? "  $name $value" : "  $name '$value'")
        }
        else if (line ==~ selector + / */ + styleRegex + / *}?/ || /* selector { stuff } */
                line ==~ frameRegex + / */ + styleRegex + / *}?/) /* frame { stuff } */ {
            int i = line.indexOf('{'), end = line.indexOf('}')
            if (end == -1) end = 0
            processLine line[0..i], inf, pw
            processLine line[(i + 1)..(end-1)], inf, pw
            if (end > 0) processLine line[end], inf, pw
        }
        else if (line ==~ /\{? */ + styleRegex + / *}?/) { // {? stuff }?
                int i = line.indexOf('{'), end = line.indexOf('}')
                if (end == -1) end = 0
                if (i >= 0) processLine line[0..i], inf, pw
                processLine line[(i+1)..(end-1)], inf, pw
                if (end > 0) processLine line[end], inf, pw
        } else {
            println "warning: unmatched line: $originalLine"
            pw.println originalLine
        }
    }

    static void convertFromGroocss(File inf, File out) {
        GrooCSS.convertFile inf, out
    }

    static String nameToCamel(String it) {
        def name = it.toLowerCase()
        Matcher mtr = name =~ /-[a-z]/
        def result = name.replace("@", "")
        while (mtr.find()) { result = result.replace mtr.group(), mtr.group()[1].toUpperCase() }
        result
    }

}
