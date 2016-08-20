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
        'a{\n  color \'blue\'\n}'                 | 'a{\ncolor: blue\n}'
        "input{\n  color 'blue'\n}"               | 'INPUT{\ncolor: blue\n}'
        'input{\n  fontSize \'20px\'\n}'          | 'INPUT{\nfont-size: 20px\n}'
        'fontFace{\n  fontSize \'20px\'\n}'       | '@font-face{\nfont-size: 20px\n}'
        "media 'screen', {\na{\n  color 'blue'\n}\n}"| '@media screen{\na{\ncolor: blue\n}\n}'
        'a{\n  color \'blue\'\n}'                 | 'a{ color: blue }'
        "input{\n  color 'blue'\n}"               | 'INPUT{ color: blue }'
        "{\n  color 'blue'\n}"                    | '{ color: blue }'
        'a{\n  color \'blue\''                    | 'a{ color: blue'
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
