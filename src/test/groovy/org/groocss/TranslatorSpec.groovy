package org.groocss

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by adavis on 8/19/16.
 */
class TranslatorSpec extends Specification {

    def "should translate basics"() {
        expect:
        groo == Translator.convertFromCSS(css).trim()
        where:
        groo                                         | css
        'a {\n  color blue\n}'                       | 'a{\ncolor: blue\n}'
        "input {\n  color blue\n}"                   | 'INPUT{\ncolor: blue\n}'
        'input {\n  fontSize \'20px\'\n}'            | 'INPUT{\nfont-size: 20px\n}'
        'fontFace {\n  fontSize \'20px\'\n}'         | '@font-face{\nfont-size: 20px\n}'
        'keyframes {'                                | '@keyframes {'
        "media 'screen', {\na {\n  color blue\n}\n}" | '@media screen{\na{\ncolor: blue\n}\n}'
        'a {\n  color blue\n}'                       | 'a{ color: blue }'
        "input {\n  color blue\n}"                   | 'INPUT{ color: blue }'
        "{\n  color blue\n}"                         | '{ color: blue }'
    }


    def "should translate colors"() {
        expect:
        groo == Translator.convertFromCSS(css).trim()
        where:
        groo                     | css
        'a {\n  color blue'       | 'a{ color: blue'
        'a {\n  color aliceBlue'  | 'a{ color: aliceblue'
        'a {\n  color whiteSmoke' | 'a{ color: WHITESMOKE'
        'a {\n  color white'      | 'a{ color: white'
    }

    def "should translate symbols"() {
        expect:
        groo == Translator.convertFromCSS(css).trim()
        where:
        groo                                    | css
        'a%hover {\n  color blue'               | 'a:hover { color: blue'
        'p.red | a.red {\n  color red\n}'       | 'p.red, a.red { color : red }'
        '_.red | _.rose {\n  color red\n}'      | '.red, .rose { color: red }'
        'p >> a {\n  color blue\n}'      | 'p > a {\n\tcolor: blue }' //>> => >
        'p * a {\n  color blue\n}'       | 'p * a {\ncolor: blue; }' // * => *
        'p - a {\n  color blue\n}'       | 'p ~ a {\ncolor: blue; }' // - => ~(tilde)
        'p + a {\n  color blue\n}'       | 'p + a {\ncolor: blue; }' // + => +
        'p a {\n  color blue\n}'       | 'p a { \ncolor: blue; }'   // ^ =>  (space)
        'a.clazz {'                      | 'a.clazz {'
        'sg \'a.clazz-dash\', {'                | 'a.clazz-dash {'
        'p a.clazz {\n  color blue\n}'          | 'p a.clazz { \ncolor: blue; }'   // ^ =>  (space)
        'a%nthChild(\'odd\') {'                 | 'a:nth-child(odd) {'
        'a%nthChild(\'2n\') {'                  | 'a:nth-child(2n) {'
        'a%not(\'a.blue\') {'                   | 'a:not(a.blue) {'
        'a%nthChild(\'2n\') | a%not(\'.x\') {'  | 'a:nth-child(2n), a:not(.x) {'
        '}'                                     | '}'
    }

    def "should translate ids"() {
        expect:
        groo == Translator.convertFromCSS(css).trim()
        where:
        groo                                    | css
        'sg \'#id\', {'                         | '#id {'
        'sg \'#weird-id\', {'                   | '#weird-id {'
        'sg \'aside#id\', {'                    | 'aside#id {'
    }

    @Unroll
    def "should translate #name"() {
        expect:
        groo == Translator.convertFromCSS(css).trim()
        where:
        name      |   groo                                   | css
        'comment' |  'comment \'here\''                      | '/*here*/'
        'comment2'|  "comment(''' Bigger comment\nline2 ''')"| '/** Bigger comment\nline2 */'
        'raw'     |  'raw \'$$html { height: 0}\''           | '$$html { height: 0}'
        'raw2'    |  'raw \'$$html { height: 300px\\0/ }\''  | '$$html { height: 300px\\0/ }'
        'frame'   |  '50 % {'                                | '50% {'
        'frame2'  |  '50 %'                                  | '50%'
        'from'    |  'from {'                                | 'from{'
        'to'      |  'to'                                    | 'to'
        'frames'  |  'frame([0, 20, 50]) {'                  | '0%, 20%, 50% {'
        'frames2' |  'frame([0, 20, 50])'                    | '0%, 20%, 50%'
        'maxwidth'|  'maxWidth \'1000px\''                   | 'max-width: 1000px;'
        'transform'| 'transform \'skew(20deg,10deg)\''       | 'transform: skew(20deg,10deg);'
        'gradient'|  'background \'radial-gradient(1,2,3)\'' | 'background: radial-gradient(1,2,3);'
    }
    
    def "should translate lines ending in comma"() {
        expect:
        groo == Translator.convertFromCSS(css).trim()
        where:
        groo                      | css
        'a%hover |'               | 'a:hover, '
        'p.red | a.red |'         | 'p.red, a.red ,'
        'p >> a |'                | 'p > a ,\t ' //>> => >
        'p * a |'                 | 'p * a , ' // * => *
        'p - a |'                 | 'p ~ a , ' // - => ~(tilde)
        'p a |'                 | 'p a ,  '   // ^ =>  (space)
        'a.clazz |'               | 'a.clazz ,'
        'sel(\'a.clazz-dash\') |' | 'a.clazz-dash ,'
        'p a.clazz |'           | 'p a.clazz ,  '   // ^ =>  (space)
        'a%nthChild(\'odd\') |'   | 'a:nth-child(odd) ,'
        'a%not(\'a.blue\') |'     | 'a:not(a.blue) ,'
    }

    def "should use multiple spaces with elements and classes to create StyleGroup now"() {
        expect:
        groo == Translator.convertFromCSS(css).trim()
        where:
        css                                             | groo
        "div p.test a{text-decoration: none;}"          | "div p.test a {\n  textDecoration 'none'\n}"
        "div.man p.test a{text-decoration: none;}"      | "div.man p.test a {\n  textDecoration 'none'\n}"
        "div.man .test a{text-decoration: none;}"       | "div.man ^ _.test ^ a {\n  textDecoration 'none'\n}"
        "body div p a{text-decoration: none;}"          | "body div p a {\n  textDecoration 'none'\n}"
        "body div.test p a{text-decoration: none;}"     | "body div.test p a {\n  textDecoration 'none'\n}"
        "body div.test p li a{text-decoration: none;}"  | "body div.test p li a {\n  textDecoration 'none'\n}"
    }

    def "should_translate_file"() {
        given:
        def inf = new File('index.css')
        def out = new File('index.groocss')
        println inf.absolutePath
        Translator.convertFromCSS(inf, out)
        expect:
        out.exists()
    }
}
