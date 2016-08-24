package org.groocss

import spock.lang.Specification

/**
 * Created by adavis on 8/19/16.
 */
class TranslatorSpec extends Specification {

    def should_translate() {
        expect:
        groo == Translator.convertFromCSS(css).trim()
        where:
        groo                                    | css
        'a{\n  color blue\n}'                   | 'a{\ncolor: blue\n}'
        "input{\n  color blue\n}"               | 'INPUT{\ncolor: blue\n}'
        'input{\n  fontSize \'20px\'\n}'        | 'INPUT{\nfont-size: 20px\n}'
        'fontFace {\n  fontSize \'20px\'\n}'    | '@font-face{\nfont-size: 20px\n}'
        'keyframes {'                           | '@keyframes {'
        "media 'screen', {\na{\n  color blue\n}\n}"| '@media screen{\na{\ncolor: blue\n}\n}'
        'a{\n  color blue\n}'                   | 'a{ color: blue }'
        "input{\n  color blue\n}"               | 'INPUT{ color: blue }'
        "{\n  color blue\n}"                    | '{ color: blue }'
        'a{\n  color blue'                      | 'a{ color: blue'
        'a{\n  color aliceBlue'                 | 'a{ color: aliceblue'
        'a{\n  color whiteSmoke'                | 'a{ color: WHITESMOKE'
        'a{\n  color white'                     | 'a{ color: white'
        'a { hover()\n  color blue'             | 'a:hover { color: blue'
        'p.red | a.red {\n  color red\n}'       | 'p.red, a.red { color : red }'
        'p >> a {\n  color blue\n}'      | 'p > a {\n\tcolor: blue }' //>> => >
        'p * a {\n  color blue\n}'       | 'p * a {\ncolor: blue; }' // * => *
        'p - a {\n  color blue\n}'       | 'p ~ a {\ncolor: blue; }' // - => ~(tilde)
        'p ^ a {\n  color blue\n}'       | 'p a { \ncolor: blue; }'   // ^ =>  (space)
        'a.clazz {'                      | 'a.clazz {'
        'p ^ a.clazz {\n  color blue\n}' | 'p a.clazz { \ncolor: blue; }'   // ^ =>  (space)
    }

    def should_translate_file() {
        given:
        def inf = new File('index.css')
        def out = new File('index.groocss')
        println inf.absolutePath
        Translator.convertFromCSS(inf, out)
        expect:
        out.exists()
    }
}
