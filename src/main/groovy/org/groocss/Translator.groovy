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
        def styleRegex = /[-\w]+\s*:\s*[^\{\};]+;?\s*/
        def frameRegex = /[0-9]+%\s*\{\s*/
        def selector = /[- >\~\*#\[,:\]="\w\.]+\s*\{\s*/

        if (line ==~ /(@[-\w]+)(\s+[\w\s]+)?\s*\{/) {
            Matcher m = line =~ /(@[-\w]+)(\s+[\w\s]+)?\s*\{/
            m.find()
            pw.println( nameToCamel(m.group(1)) + (m.group(2) ? " '${m.group(2).trim()}', {" : ' {') )
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
        else if (line ==~ frameRegex) // frame
            pw.println "frame ${line[0..-2].trim().replace('%','')}, {"
        else if (line ==~ selector) // selector
            processSelector(originalLine, pw)
        else if (line ==~ /\}/) // close bracket
            pw.println originalLine
        else if (line ==~ styleRegex) { // styles
            def ci = line.indexOf ':'
            def name = nameToCamel line.substring(0, ci).trim()
            def value = line.substring(ci + 1).replace(';', '').trim()
            pw.println "  $name ${convertValue(value)}"
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

    static void processSelector(String original, Printer pw) {
        def line = original.replace('{', '').trim()
        if (line ==~ /[\w\.]+(\s+[\w\.]+)*/)  /* Matches just spaces separating elements. */
            pw.println line.replaceAll(/\s+/, ' ^ ') + " {"
        else if (!line.contains(':'))
            pw.println line.replace('>', '>>').replace('~', '-').replace(',', ' |') + " {"
        else
            pw.println "sg '${line[0..-2].trim()}', {"
    }

    private static String convertValue(String value) {
        if (colors.find { value.equalsIgnoreCase(it) }) {
            colors.find { value.equalsIgnoreCase(it) }
        }
        else if (value ==~ /([0-9\.]+)|(rgba?.*)/) {
            value
        }
        else "'$value'"
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

    static List<String> colors =
            ['aliceBlue', 'antiqueWhite', 'aqua', 'aquamarine', 'azure', 'beige', 'bisque', 'black',
                  'blanchedAlmond', 'blue', 'blueViolet', 'brown', 'burlyWood', 'cadetBlue', 'chartreuse',
                  'chocolate', 'coral', 'cornflowerBlue', 'cornsilk', 'crimson', 'cyan', 'darkBlue', 'darkCyan',
                  'darkGoldenRod', 'darkGray', 'darkGrey', 'darkGreen', 'darkKhaki', 'darkMagenta', 'darkOliveGreen',
                  'darkOrange', 'darkOrchid', 'darkRed', 'darkSalmon', 'darkSeaGreen', 'darkSlateBlue', 'darkSlateGray',
                  'darkSlateGrey', 'darkTurquoise', 'darkViolet', 'deepPink', 'deepSkyBlue', 'dimGray', 'dimGrey',
                  'dodgerBlue', 'fireBrick', 'floralWhite', 'forestGreen', 'fuchsia', 'gainsboro', 'ghostWhite',
                  'gold', 'goldenRod', 'gray', 'grey', 'green', 'greenYellow', 'honeyDew', 'hotPink', 'indianRed ',
                  'indigo ', 'ivory', 'khaki', 'lavender', 'lavenderBlush', 'lawnGreen', 'lemonChiffon', 'lightBlue',
                  'lightCoral', 'lightCyan', 'lightGoldenRodYellow', 'lightGray', 'lightGrey', 'lightGreen',
                  'lightPink', 'lightSalmon', 'lightSeaGreen', 'lightSkyBlue', 'lightSlateGray', 'lightSlateGrey',
                  'lightSteelBlue', 'lightYellow', 'lime', 'limeGreen', 'linen', 'magenta', 'maroon',
                  'mediumAquaMarine', 'mediumBlue', 'mediumOrchid', 'mediumPurple', 'mediumSeaGreen',
                  'mediumSlateBlue', 'mediumSpringGreen', 'mediumTurquoise', 'mediumVioletRed', 'midnightBlue',
                  'mintCream', 'mistyRose', 'moccasin', 'navajoWhite', 'navy', 'oldLace', 'olive', 'oliveDrab',
                  'orange', 'orangeRed', 'orchid', 'paleGoldenRod', 'paleGreen', 'paleTurquoise', 'paleVioletRed',
                  'papayaWhip', 'peachPuff', 'peru', 'pink', 'plum', 'powderBlue', 'purple', 'rebeccaPurple', 'red',
                  'rosyBrown', 'royalBlue', 'saddleBrown', 'salmon', 'sandyBrown', 'seaGreen', 'seaShell', 'sienna',
                  'silver', 'skyBlue', 'slateBlue', 'slateGray', 'slateGrey', 'snow', 'springGreen', 'steelBlue',
                  'tan', 'teal', 'thistle', 'tomato', 'turquoise', 'violet', 'wheat', 'white', 'whiteSmoke',
                  'yellow', 'yellowGreen']
}
