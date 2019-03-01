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

import groovy.transform.TypeChecked

import java.util.regex.Matcher

/**
 * Translates from/to CSS. WARNING: EXPERIMENTAL, DO NOT ASSUME IT WORKS.
 */
@TypeChecked
class Translator {

    interface Printer { void println(Object value) }
    interface Reader { void eachLine(Closure closure) }

    /** Converts files from CSS to GrooCSS (WARNING: EXPERIMENTAL, DO NOT ASSUME IT WORKS). */
    static void convertFromCSS(File inf, File out) {
        out.withPrintWriter { pw -> convertFromCSS(new Reader() {
            void eachLine(Closure closure) { inf.eachLine closure }
        }, new Printer() {
            void println(Object value) { pw.println value }
        }) }
    }

    /** Converts files from CSS to GrooCSS (WARNING: EXPERIMENTAL, DO NOT ASSUME IT WORKS). */
    static String convertFromCSS(String text) {
        StringBuilder sb = new StringBuilder()
        convertFromCSS(new Reader() {
            void eachLine(Closure closure) { text.split('\n').each(closure) }
        }, new Printer() {
            void println(Object value) { sb.append(value).append('\n') }
        })
        sb.toString()
    }

    /** Converts files from CSS to GrooCSS (WARNING: EXPERIMENTAL, DO NOT ASSUME IT WORKS). */
    static void convertFromCSS(Reader inf, Printer pw) {
        def state = [:]
        inf.eachLine { String originalLine ->
            process originalLine, pw, state
        }
    }

    private static void process(String originalLine, Printer pw, Map state = [:]) {
        def line = originalLine.toLowerCase().trim()

        if (state.skipping) {
            println "warning: skipping: $originalLine"
            if (line =~ /}/) state.skipping = false
        }
        else if (state.inComment) {
            if (originalLine ==~ /(.*)\*+\//) {
                state.inComment = false
                def matcher = originalLine =~ /(.*)\*+\//
                if (matcher.find()) pw.println "${matcher.group(1)}''')"
            } else {
                pw.println originalLine // pass through
            }
        }
        else {
            processLineSwitch(originalLine, line, pw, state)
        }
    }

    static final String STYLE_RE = /[-\w]+\s*:\s*[^\{\};]+;?\s*/
    static final String FRAME_RE = /([0-9]+|from|to)%?\s*\{?\s*/
    static final String FRAME_LIST_RE = /([0-9]+%,? *){2,}\s*\{?\s*/
    static final String SELECTOR_RE = /[- >\~\+*#\[,:\]="\w\.\(\)]+\s*[\{,]\s*/
    
    private static void processLineSwitch(String originalLine, String line, Printer pw, Map state) {
        switch (line) {
        case ~ /@(-webkit-|-ms-|-o-|-moz-)[-\w\s]+\{\s*/: //skip browser-specific stuff
            state.skipping = true
            println "warning: skipping: $originalLine"
            break
        case ~(/(-webkit-|-ms-|-o-|-moz-)/ + STYLE_RE): //skip browser-specific stuff
            println "warning: skipping: $originalLine"
            break
        case ~ /(@[-\w]+)(\s+[\w\s]+)?\s*\{/: // @media or @font-face
            Matcher m = line =~ /(@[-\w]+)(\s+[\w\s]+)?\s*\{/
            m.find()
            pw.println( nameToCamel(m.group(1)) + (m.group(2) ? " '${m.group(2).trim()}', {" : ' {') )
            break
        case ~ /\/\*+.*\*\//: // comment
            def matcher = originalLine =~ /\/\*(.*)\*\//
            if (matcher.find()) pw.println "comment '${matcher.group(1)}'"
            break
        case ~ /\/\*+.*/: // multiline comment
            state.inComment = true
            def matcher = originalLine =~ /\/\*+(.*)/
            if (matcher.find()) pw.println "comment('''${matcher.group(1)}"
            break
        case ~ FRAME_LIST_RE: processFrame(line, FRAME_LIST_RE, pw, true); break
        case ~ FRAME_RE: processFrame(line, FRAME_RE, pw); break
        case ~ SELECTOR_RE: processSelector(originalLine.trim(), pw); break
        case ~ /[}{]/: // close or open bracket
            pw.println originalLine
            break
        case ~STYLE_RE: // styles
            def ci = line.indexOf ':'
            def name = nameToCamel line.substring(0, ci).trim()
            def value = line.substring(ci + 1).replace(';', '').trim()
            pw.println "  $name ${convertValue(value)}"
            break

        case ~(FRAME_RE + / */ + STYLE_RE + / *}?/): /* frame { stuff } */
        case ~(SELECTOR_RE + / */ + STYLE_RE + / *}?/): /* selector { stuff } */
            int i = line.indexOf('{'), end = line.indexOf('}')
            if (end == -1) end = 0
            process line[0..i], pw, state
            process line[(i + 1)..(end-1)], pw, state
            if (end > 0) process line[end], pw, state
            break
            
        case ~(/\{? */ + STYLE_RE + / *}?/): // {? stuff }?
            int i = line.indexOf('{'), end = line.indexOf('}')
            if (end == -1) end = 0
            if (i >= 0) process line[0..i], pw, state
            process line[(i + 1)..(end - 1)], pw, state
            if (end > 0) process line[end], pw, state
            break
        case '':
            pw.println('')
            break
        default:
            println "warning: unmatched line (using raw): $originalLine"
            pw.println "raw '$originalLine'"
        }
    }

    private static void processFrame(String line, String regex, Printer pw, boolean multiple=false) {
        def frames = line.replaceAll(/\s*\{\s*$/, '')
        def matcher = line =~ regex
        if (matcher.find()) {
            String value = multiple ? "[$frames]" : matcher.group(1)

            def rest = line.length() > frames.length() ? line[frames.length()..-1] : ''

            if (multiple) pw.println "frame(${value.replace('%','').trim()})$rest"
            else if (line =~ /%/) pw.println "$value %$rest"
            else pw.println "$value $rest"
        }
    }

    static void processSelector(String original, Printer pw) {
        pw.println(processSelector(original))
    }
    
    static String processSelector(String original) {
        if (original == '') return original
        def ending = original.find(/\s*[\{,]\s*$/)?.trim()?.replaceAll(/,/, '|') ?: ''
        def line = original.replaceAll(/[\{,]$/, '').trim()

        if (line ==~ /(\w*)(\.\w+)?/) { // element.class?
            def element = line.find(/\w*/)?.toLowerCase() ?: '_'
            def clazz = line.find(/\.[\w]+/) ?: ''
            "${element}${clazz}${ending ? ' ' : ''}$ending"
        }
        else if (line ==~ /\w*\.[-\w]+\s*,?/) {// class with dashes
            def selector = line.find(/\w*\.[-\w]+/) ?: ''
            if (ending == '{') "sg '$selector', $ending"
            else if (ending == '|') "sel('$selector') $ending"
            else "sel('$selector')"
        }
        else if (line ==~ /\w+:[-+\w\.\(\)]+/) { //pseudo-class
            processPseudo(original.replaceAll(/ *, */, ' |'))
        }
        else if (line ==~ /\w+(\.\w+)?(\s+\w+(\.\w+)?)*/) { /* Spaces separating simple elements. */
            "$line $ending" // don't change a thing
        }
        else if (line.contains(',')) {
            line.split(/\s*,\s*/).collect(this.&processSelector).join(' | ') + " $ending"
        }
        else if (line =~ /\s+/ && !line.contains(':') && !line.contains('-')) {
            def phase1 = line.replaceAll(/\s*>\s*/, '>>').replaceAll(/\s*~\s*/, '-')
                .replaceAll(/\s*(\+|\*)\s*/, '$1')
            def array = phase1.split(/\s+/)
            def phase2 = array.length > 1 ? array.collect(this.&processSelector).join(' ^ ') : phase1

            phase2.replaceAll(/(>>|-|\+|\*)/, ' $1 ') + " $ending"
        }
        else if (ending == '{') "sg '${line.trim()}', {"
        else if (ending)        "sel('${line.trim()}') $ending"
        else                    "sel('$line')"
    }

    static String processPseudo(String line) {
        String result = line
        Matcher pseudo = line =~ /:([-\w]+)/
        Matcher parens = line =~ /\(([-+\w\.]+)\)/
        boolean hasParens = parens.find()

        if (pseudo.find())
            result = result.replace( ':'+ pseudo.group(1), '%' + nameToCamel(pseudo.group(1)) )

        if (hasParens)
            result = result.replace(parens.group(1), '\'' + parens.group(1) + '\'')

        result
    }

    private static String convertValue(String value) {
        if (colors.find { value.equalsIgnoreCase(it) }) {
            colors.find { value.equalsIgnoreCase(it) }
        }
        else if (value ==~ /([0-9\.]+)|(rgba?.*)/) {
            value
        }
        else "'${value.replace('\'', '\\\'')}'"
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
