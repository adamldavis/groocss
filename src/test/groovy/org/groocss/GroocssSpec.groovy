package org.groocss

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by adavis on 7/25/16.
 */
class GroocssSpec extends Specification {

    def "should process file using script that calls org.groocss.GrooCSS.process"() {
        when:
        def output = new File('build/test.css')
        def input = new File('src/test/groovy/test.css.groovy')
        println input.absolutePath
        GrooCSS.convertFile(new Config().noExts().compress(), input, output)
        def css = output.text
        then:
        css.toString() == 'body{font-size: 2em;color: Black;}article{padding: 2em;}#thing{font-size: 200%;}' +
                '@keyframes test {from{color: Black;}to{color: Red;}}'
    }

    def "should process file using script using convertWithoutBase using given Config in file"() {
        when:
        def output = new File('build/test.css')
        def input = new File('src/test/groovy/test.css.groovy')
        println input.absolutePath
        GrooCSS.convertWithoutBase(input, output)
        def css = output.text
        then:
        css.toString() == 'body{font-size: 2em;color: Black;}article{padding: 2em;}#thing{font-size: 200%;}' +
                '@keyframes test {from{color: Black;}to{color: Red;}}'
    }

    def "should create a css"() {
        when:
        def css = GrooCSS.process {
            sg('.class') {}
        }
        then:
        css != null
    }

    def "should create style groups"() {
        when:
        def css = GrooCSS.process {
            sel('.a') {}
            sel('.b') {}
            sg('.c') {}
        }.css
        then:
        css.groups.size() == 3
    }


    def "should set styles"() {
        when:
        def css = GrooCSS.process {
            sg '.a', {
                color('black')
                background('white')
            }
        }.css
        then:
        css.groups.size() == 1
        "$css" == ".a{color: black;\n\tbackground: white;}"
    }

    def "should set colors"() {
        when:
        def css = GrooCSS.process {
            def sea = c('5512ab')
            sg '.sea', {
                color(sea.darker())
                background(sea.brighter())
            }
        }.css
        then:
        css.groups.size() == 1
        "$css" == ".sea{color: #3b0c77;\n\tbackground: #7919f4;}"
    }


    def "should create font face"() {
        when:
        def css = GrooCSS.process {
            fontFace {
                fontFamily 'myFirstFont'
                fontWeight 'normal'
                src 'url(sensational.woff)'
            }
        }
        then:
        "$css" == "@font-face { font-family: myFirstFont; font-weight: normal; src: url(sensational.woff); }\n"
    }

    def "should create colors with rgb"() {
        when:
        def css = GrooCSS.process {
            sg '.colors', {
                color rgb(1, 2, 3)
                background rgba(250, 250, 250, 0.9)
            }
        }
        then:
        "$css" == ".colors{color: #010203;\n\tbackground: rgba(250, 250, 250, 0.90);}"
    }

    def "should create colors rgba using alpha0"() {
        when:
        def css = GrooCSS.process {
            sg '.colors', {
                background rgba(0,0,0,0)
            }
        }
        then:
        "$css" == ".colors{background: rgba(0, 0, 0, 0.0);}"
    }

    def "should create named colors "() {
        when:
        def css = GrooCSS.process {
            sg '.colors', {
                color aliceBlue
            }
        }
        then:
        "$css" == ".colors{color: AliceBlue;}"
    }

    def "should convert named color to rgba "() {
        when:
        def css = GrooCSS.process {
            sg '.colors', {
                color violet.alpha(0.5)
            }
        }
        then:
        "$css" == ".colors{color: rgba(238, 130, 238, 0.50);}"
    }

    def "should compress"() {
        when:
        def css = GrooCSS.process(new Config(compress: true, addMoz: false, addOpera: false)) {
            sg '.a', {
                color('black')
                background('white')
                transition('500ms')
            }
            sg '.b', {
                margin 0
            }
        }
        then:
        "$css" == ".a{color: black;background: white;transition: 500ms;-webkit-transition: 500ms;}.b{margin: 0;}"
    }

    def "charset should be added"() {
        when:
        def css = GrooCSS.process {
            charset utf8
        }
        then:
        "$css" == "@charset \"UTF-8\";\n"
    }

    def "should create media"() {
        when:
        def css = GrooCSS.process {
            media 'print', {
                sg 'body', {
                    display 'none'
                }
            }
        }
        then:
        "$css" == "@media print {\nbody{display: none;}\n}\n"
    }

    def "should create medias"() {
        when:
        def css = GrooCSS.process {
            media 'print', {
                sg 'body', {
                    display 'none'
                }
            }
            media 'screen', {
                sg 'body', {
                    color black
                }
            }
        }
        then:
        "$css" == "@media print {\nbody{display: none;}\n}\n@media screen {\nbody{color: Black;}\n}\n"
    }

    def "should be fluent"() {
        when:
        def css = GrooCSS.process {
            sg '.a', {
                add(style('-webkit-touch-callout', 'none')) << style('-webkit-text-size-adjust', 'none')
            }
        }
        then:
        "$css" == ".a{-webkit-touch-callout: none;\n\t-webkit-text-size-adjust: none;}"
    }

    def "should convert file"() {
        File temp, t1, t2
        given:
        temp = new File('build/temp')
        temp.mkdirs()
        t1 = new File(temp, "t1.groocss")
        t2 = new File(temp, "t2.css")
        t1.text = "sg('.a') {left 0}\nsg('.b') {left 0}\nsg('.c') {left 0}\n"
        when:
        GrooCSS.convert t1, t2
        then:
        "${t2.text}" == ".a{left: 0;}\n.b{left: 0;}\n.c{left: 0;}"
    }

    def "should convert file compress "() {
        File temp, t1, t2
        given:
        temp = new File('build/temp')
        temp.mkdirs()
        t1 = new File(temp, "t1.groocss")
        t2 = new File(temp, "t2.css")
        t1.text = "sg('.a') {left 0}\nsg('.b') {left 0}\nsg('.c') {left 0}\n"
        when:
        GrooCSS.convert new Config(compress: true), t1, t2
        then:
        "${t2.text}" == ".a{left: 0;}.b{left: 0;}.c{left: 0;}"
    }

    def "should add StyleGroup"() {
        when:
        def css = GrooCSS.process {
            sg '.a', {
                color black
                background white
                add('a:hover') {
                    color blue
                }
            }
        }
        then:
        "$css" == ".a a:hover{color: Blue;}\n.a{color: Black;\n\tbackground: White;}"
    }

    def "should use html elements "() {
        when:
        def css = GrooCSS.withConfig { noExts() }.process {
            a '.blue', { color blue }
            input { borderRadius '1em' }
            inputButton { borderRadius 0 }
        }
        then:
        "$css" == "a.blue{color: Blue;}\ninput{border-radius: 1em;}\ninput [type=\"button\"]{border-radius: 0;}"
    }

    def "should use a_hover "() {
        when:
        def css = GrooCSS.process { a_hover {color 'red'}; a_focus {color 'red'} }
        then:
        "$css" == "a:hover{color: red;}\na:focus{color: red;}"
    }

    def "should use subselect to add classes "() {
        when:
        def css = GrooCSS.process { a {subselect '.me'; top 0} }
        then:
        "$css" == "a.me{top: 0;}"
    }

    def "should use pseudo classes"() {
        when:
        def css = GrooCSS.process {
            a%not(get_().button)%firstChild() {
                color black
            }
        }
        then:
        "$css" == "a:not(.button):first-child{color: Black;}"
    }

    def "should use nth-child"() {
        when:
        def css = GrooCSS.process {
            a % nthChild('odd') { color '#abc' }
        }
        then:
        "$css" == "a:nth-child(odd){color: #abc;}"
    }

    def "should have math functions "() {
        when:
        def css = GrooCSS.process {
            body { left "${sqrt(16)}em"; top sin(0) as int }
            div { left "${floor(10.123)}px" }
        }
        then:
        "$css" == "body{left: 4.0em;\n\ttop: 0;}\ndiv{left: 10px;}"
    }

    def "getUnit should get unit"() {
        given:
        def css = new GrooCSS()
        expect:
        css.getUnit(value) == unit
        where:
        value | unit
        '11in'| 'in'
        '12px'| 'px'
        '1.1em'| 'em'
    }

    def "should extract value with _unit"() {
        given:
        def css = new GrooCSS()
        expect:
        css.unit(value) == unit
        where:
        value | unit
        '11in'| 11
        '12px'| 12
        '1.1em'| 1.1
    }

    def "should add unit"() {
        given:
        def css = new GrooCSS()
        expect:
        css.unit(11, 'px').toString() == '11px'
    }

    def "should convert values"() {
        given:
        def css = new GrooCSS()
        expect:
        css.convert("${val}", unit) == value
        where:
        val     | unit | value
        '254mm' | 'in' | '10.0in'
        '2.54cm'| 'in' | '1in'
        '1s'    | 'ms' | '1000ms'
        '1cm'   | 'mm' | '10mm'
        '66pt'  | 'pc' | '5.5pc'
        '1pc'   | 'pt' | '12pt'
        '1in'   | 'pt' | '72pt'
        '1in'   | 'pc' | '6pc'
        '333pt' | 'cm' | '11.74750cm'
        '6.284rad'|'deg'| '360.0466784602093deg'
    }

    def "getImageSize should get image size"() {
        given:
        def css = new GrooCSS()
        expect:
        css.getImageSize('black.png') == '640px 480px'
    }

    def "should add units to integer"() {
        when:
        def css = GrooCSS.process {
            a { fontSize 11.px }
        }
        then:
        "$css" == "a{font-size: 11px;}"
    }

    def "should add sub styles "() {
        when:
        def css = GrooCSS.process {
            a {
                color 'blue'
                add ':hover', {
                    background '#eee'
                }
                add '.btn', {
                    border '1px solid white'
                }
            }
        }
        then:
        "$css" == "a:hover{background: #eee;}\na.btn{border: 1px solid white;}\na{color: blue;}"
    }

    def "should use underscore method missing"() {
        when:
        def result = GrooCSS.process {
            get_().blue { color 'blue' }
        }
        then:
        "$result" == ".blue{color: blue;}"
    }

    def "should use element withClass"() {
        when:
        def result = GrooCSS.process {
            table.withClass('blue') { color 'blue' }
        }
        then:
        "$result" == "table.blue{color: blue;}"
    }

    def "should use element method missing "() {
        when:
        def result = GrooCSS.process {
            table.blue { color 'blue' }
        }
        then:
        "$result" == "table.blue{color: blue;}"
    }

    def "should use element sel"() {
        when:
        def result = GrooCSS.process {
            input.sel '[class$="test"]', { color 'blue' }
        }
        then:
        "$result" == 'input[class$="test"]{color: blue;}'
    }

    def "should use element putAt"() {
        when:
        def result = GrooCSS.process {
            input['class$="test"'] = { color 'blue' }
        }
        then:
        "$result" == 'input[class$="test"]{color: blue;}'
    }

    def "should convert file with _underscore"() {
        File temp, u1in, u1out
        given:
        temp = new File('build/temp')
        temp.mkdirs()
        u1in = new File(temp, "u1.groocss")
        u1out = new File(temp, "u1.css")
        u1in.text = "_.a {left 0}\n_.b {left 0}\n_.c {left 0}\n"
        when:
        GrooCSS.convert u1in, u1out
        then:
        "${u1out.text}" == ".a{left: 0;}\n.b{left: 0;}\n.c{left: 0;}"
    }

    def "should use operators"() {
        expect:
        "$groo" == css
        where:
        groo | css
        GrooCSS.process {div + a { color blue }} | 'div + a{color: Blue;}'
        GrooCSS.process {div - a { color blue }} | 'div ~ a{color: Blue;}'
        GrooCSS.process {div >> a { color blue }} | 'div > a{color: Blue;}'
        GrooCSS.process {div ^ a { color blue }} | 'div a{color: Blue;}'
        GrooCSS.process {div.multiply a { color blue }} | 'div * a{color: Blue;}'
        GrooCSS.process {input | a { color blue }} | 'input,a{color: Blue;}'
        GrooCSS.process {select | input | a { color blue }} | 'select,input,a{color: Blue;}'
        GrooCSS.process {div + span + a { color blue }} | 'div + span + a{color: Blue;}'
        GrooCSS.process {div - span - a { color blue }} | 'div ~ span ~ a{color: Blue;}'
        GrooCSS.process {div >> p >> a { color blue }} | 'div > p > a{color: Blue;}'
        GrooCSS.process {div ^ p ^ a { color blue }} | 'div p a{color: Blue;}'
        GrooCSS.process {div['class$="test"'] | svg { color blue }} | 'div[class$="test"],svg{color: Blue;}'
        GrooCSS.process {div + a.blue { color blue }} | 'div + a.blue{color: Blue;}'
        GrooCSS.process {a.blue + div { color blue }} | 'a.blue + div{color: Blue;}'
    }

    def "should add using dsl"() {
        when:
        def css = GrooCSS.process {
            div {
                color black
                add(span['id="123"']) {
                    background white
                }
            }
        }
        then:
        "$css" == 'div span[id="123"]{background: White;}\ndiv{color: Black;}'
    }

    def "should use DSL for pseudo-classes"() {
        when:
        def css = GrooCSS.process {
            div % hover | p % hover {
                background('#000')
            }
            li % nthChild('2n') { marginTop 2.px }
        }
        then:
        "$css" == 'div:hover,p:hover{background: #000;}\nli:nth-child(2n){margin-top: 2px;}'
    }

    def "should use odd pseudo-class shortcut for nth-child"() {
        when:
        def css = GrooCSS.process {
            td % odd | td % firstChild { color '#abc' }
        }
        then:
        "$css" == "td:nth-child(odd),td:first-child{color: #abc;}"
    }

    def "should use Selector object as parameter to sg"() {
        when:
        def css = GrooCSS.process {
            sg (div % hover | p % hover) {
                background('#000')
            }
            sg (li % nthChild('2n')) { marginTop 2.px }
        }
        then:
        "$css" == 'div:hover,p:hover{background: #000;}\nli:nth-child(2n){margin-top: 2px;}'
    }


    def "odd and even should become nth-child(odd even)"() {
        when:
        def css = GrooCSS.process {
            odd { backgroundColor '#eee' }
            even { backgroundColor '#fff' }
        }
        then:
        "$css" == ':nth-child(odd){background-color: #eee;}\n:nth-child(even){background-color: #fff;}'
    }

    def "convert should accept groocss string"() {
        expect:
        css == GrooCSS.convert(groocss)
        where:
        css                                         | groocss
        'a{color: #eee;}'                           | "a { color '#eee' }"
        ':nth-child(odd){background-color: #eee;}'  | "odd { backgroundColor '#eee' }"
    }

    def "process should accept groocss string"() {
        expect:
        css == GrooCSS.process(groocss)
        where:
        css                                         | groocss
        'a{color: #eee;}'                           | "a { color '#eee' }"
        ':nth-child(odd){background-color: #eee;}'  | "odd { backgroundColor '#eee' }"
    }

    def "should compress using withConfig process String"() {
        when:
        def css = GrooCSS.withConfig { compress().onlyWebkit() }.process('''
            sg '.a', {
                color('black')
                background('white')
                transition('500ms')
            }
            sg '.b', {
                margin 0
            }''')
        then:
        "$css" == ".a{color: black;background: white;transition: 500ms;-webkit-transition: 500ms;}.b{margin: 0;}"
    }

    def "sel with one parameter should create a Selector"() {
        expect:
        GrooCSS.process {
            assert sel('#abc') instanceof Selector
            assert sel(form % hover) instanceof Selector
        }
    }

    def "should use styles to create an unattached group of styles"() {
        expect:
        assert (GrooCSS.process {
            styles {
                color red
            }
        }.toString() == '')
    }

    def "should use styles to add styles to an existing StyleGroup"() {
        when:
        def css = GrooCSS.process {
            def colorRed = styles {color red}
            form {
                if (true) add colorRed
            }
        }
        then:
        "$css" == 'form{color: Red;}'
    }

    def "should use styles to leftShift styles to an existing StyleGroup"() {
        when:
        def css = GrooCSS.process {
            def colorRed = styles {color red}
            form {
                if (true) it << colorRed
            }
        }
        then:
        "$css" == 'form{color: Red;}'
    }

    def "should use styles and plus to create a new StyleGroup and add it to media"() {
        when:
        def css = GrooCSS.process {
            def mobileLineHeight = styles {lineHeight '1.1em'}
            def form1 = form {
                fontSize 2.em
            }
            media 'mobile', {
                it << form1 + mobileLineHeight
            }
        }
        then:
        "$css" == 'form{font-size: 2em;}\n@media mobile {\nform{font-size: 2em;\n\tline-height: 1.1em;}\n}\n'
    }

    def "should use minus to remove styles"() {
        when:
        def css = GrooCSS.process {
            def mobileLineHeight = styles {lineHeight '1.1em'}
            def form1 = form {
                fontSize 2.em
            }
            media 'mobile', {
                it << form1 - styles {fontSize 2.em} + mobileLineHeight
            }
        }
        then:
        "$css" == 'form{font-size: 2em;}\n@media mobile {\nform{line-height: 1.1em;}\n}\n'
    }

    def "removing empty styles has no effect"() {
        when:
        def css = GrooCSS.process {
            def mobileLineHeight = styles {lineHeight '1.1em'}
            def form1 = form {
                fontSize 2.em
            }
            media 'mobile', {
                it << form1 + mobileLineHeight - styles {}
            }
        }
        then:
        "$css" == 'form{font-size: 2em;}\n@media mobile {\nform{font-size: 2em;\n\tline-height: 1.1em;}\n}\n'
    }

    def "removing non-matching styles has no effect"() {
        when:
        def css = GrooCSS.process {
            def mobileLineHeight = styles {lineHeight '1.1em'}
            def form1 = form {
                fontSize 2.em
            }
            media 'mobile', {
                it << form1 + mobileLineHeight - styles { fontSize 22.em }
            }
        }
        then:
        "$css" == 'form{font-size: 2em;}\n@media mobile {\nform{font-size: 2em;\n\tline-height: 1.1em;}\n}\n'
    }

    def "styles math is idempotent"() {
        when:
        def css = GrooCSS.process {
            def form1 = form { fontSize 2.em }
            media 'mobile', {
                it << form1 - styles {fontSize 2.em} + styles {lineHeight '1.1em'}
            }
            media 'only screen and (max-width: 960px)', {
                it << form1 + styles {lineHeight 12.pt}
            }
        }
        then:
        "$css" == 'form{font-size: 2em;}\n' +
                '@media mobile {\nform{line-height: 1.1em;}\n}\n' +
                '@media only screen and (max-width: 960px) {\n' +
                'form{font-size: 2em;\n\tline-height: 12pt;}\n' +
                '}\n'
    }

    def "should use space in selector definitions now"() {
        when:
        def css = GrooCSS.process {
            div p { color black }
        }
        then:
        "$css" == 'div p{color: Black;}'
    }

    def "should use bitwiseNegate to implement tilde"() {
        when:
        def css = GrooCSS.process {
            div ~ img {
                border '1px solid black'
            }
        }
        then:
        "$css" == 'div ~ img{border: 1px solid black;}'
    }

    def "should use space and xor in selector with 3 elements"() {
        when:
        def css = GrooCSS.process {
            div p ^ ul { color black }
        }
        then:
        "$css" == 'div p ul{color: Black;}'
    }

    def "should use bitwiseNegate and xor with 3 elements"() {
        when:
        def css = GrooCSS.process {
            div ~ ul ^ img {
                border '1px solid black'
            }
        }
        then:
        "$css" == 'div ~ ul img{border: 1px solid black;}'
    }

    def "should use xor and minus in selector with 3 elements"() {
        when:
        def css = GrooCSS.process {
            div ^ p - ul { color black }
        }
        then:
        "$css" == 'div p ~ ul{color: Black;}'
    }

    def "should use space to create Selector now"() {
        expect:
        GrooCSS.process {
            def divp = div p
            assert divp instanceof Selector
        }
    }

    def "should use space to create StyleGroup now"() {
        expect:
        def css = GrooCSS.process {
            def divp = div p { color black }
            assert divp instanceof StyleGroup
        }
        assert "$css" == "div p{color: Black;}"
    }

    def "should use multiple spaces with elements to create StyleGroup now"() {
        expect:
        def css = GrooCSS.process {
            div p a { color black }
        }
        assert "$css" == "div p a{color: Black;}"
    }

    def "should use multiple spaces with elements and classes to create StyleGroup now"() {
        expect:
        assert "${GrooCSS.process closure}" == css
        where:
        css | closure
        "div p.test a{text-decoration: none;}" | { div p.test a { textDecoration 'none' } }
        "div.man p.test a{text-decoration: none;}" | { div.man p.test a { textDecoration 'none' } }
        "div.man .test a{text-decoration: none;}" | { div.man get_().test a { textDecoration 'none' } }
        "body div p a{text-decoration: none;}" | { body div p a { textDecoration 'none' } }
        "body div.test p a{text-decoration: none;}" | { body div.test p a { textDecoration 'none' } }
        "body div.test p li a{text-decoration: none;}" | { body div.test p li a { textDecoration 'none' } }
    }

    def "should use bitwiseNegate to create Selector with tilde"() {
        expect:
        GrooCSS.process {
            def divimg = div ~ img
            assert divimg instanceof Selector
        }
    }

    def "should use .methodMissing and bitwiseNegate to create Selector with tilde"() {
        expect:
        GrooCSS.process {
            def s1 = div.container ~ img
            assert "$s1" == 'div.container ~ img'
            assert  s1 instanceof Selector }
    }

    def "should use getAt, call, and bitwiseNegate to create Selector with tilde"() {
        expect:
        GrooCSS.process {
            def s1 = div['test'] ~ img.test
            assert  s1 instanceof Selector
            assert "$s1" == 'div[test] ~ img.test'
        }
    }
    def "should use getAt, call, and bitwiseNegate to create StyleGroup with tilde"() {
        expect:
        GrooCSS.process {
            def s1 = div['test'] ~ img.test { color darkRed }
            assert  s1 instanceof StyleGroup
            assert "$s1.selector" == 'div[test] ~ img.test'
        }
    }

    def "should use a condition to add styles"() {
        expect:
        GrooCSS.process {
            def mycolor = { alpha -> styles {
                if (alpha == 0) color '#123'
                else color rgba(0, 0, 0, alpha)
            }}
            assert mycolor(0).styleList[0] == new Style('color', '#123')
            assert mycolor(0.5).styleList[0]==new Style('color', 'rgba(0, 0, 0, 0.50)')
        }
    }

    def "should use 0x numbers as colors"() {
        expect:
        assert "${GrooCSS.withConfig { convertUnderline() }.process closure}" == css
        where:
        css | closure
        "a{color: #cafeee;}"        | { a { color 0xcafeee} }
        "a{color: #eeeeee;}"        | { a { color 0xEEEEEE} }
        "a{color: #cafeee;}"        | { a { color c(0xcafeee)} }
        "a{color: #eeeeee;}"        | { a { color c(0xEEEEEE)} }
    }

    @Unroll
    def "should create a css with html5 element #element"() {
        expect:
        def css = GrooCSS.process closure
        "${css}" == "${element}{background: #000;}"
        where:
        element | closure
        "aside" | {aside{ background '#000' }}
        "article" | {article{ background '#000' }}
        "details" | {details{ background '#000' }}
        "dialog" | {dialog{ background '#000' }}
        "figure" | {figure{ background '#000' }}
        "footer" | {footer{ background '#000' }}
        "header" | {header{ background '#000' }}
        "main" | {main{ background '#000' }}
        "meter" | {meter{ background '#000' }}
        "nav" | {nav{ background '#000' }}
        "progress" | {progress{ background '#000' }}
        "section" | {section{ background '#000' }}
        "summary" | {summary{ background '#000' }}
        "time" | {time{ background '#000' }}
    }

    @Unroll
    def "should create a selector using _.info #element"() {
        expect:
        def css = GrooCSS.process closure
        "${css}" == ".info ${element}{background: #000;}"
        where:
        element | closure
        "aside" | { sg(get_().info(aside)) { background '#000' }}
        "article" | { sg(get_().info(article)) { background '#000' }}
        "details" | { sg(get_().info(details)) { background '#000' }}
        "dialog" | { sg(get_().info(dialog)) { background '#000' }}
        "figure" | { sg(get_().info(figure)) { background '#000' }}
        "footer" | { sg(get_().info(footer)) { background '#000' }}
        "header" | { sg(get_().info(header)) { background '#000' }}
        "main" | { sg(get_().info(main)){ background '#000' }}
        "meter" | { sg(get_().info(meter)){ background '#000' }}
        "nav" | { sg(get_().info(nav)){ background '#000' }}
        "progress" | { sg(get_().info(progress)){ background '#000' }}
        "section" | { sg(get_().info(section)){ background '#000' }}
        "summary" | { sg(get_().info(summary)){ background '#000' }}
        "time" | { sg(get_().info(time)){ background '#000' }}
    }

    @Unroll
    def "should create style groups using commas: #css"() {
        expect:
        g.toString() == css
        where:
        g | css
        GrooCSS.process { body a, b { fontWeight 'bold' } }.css |'body a, b{font-weight: bold;}'
        GrooCSS.process { body a,b,i { fontWeight 'bold' } }.css |'body a, b, i{font-weight: bold;}'
        GrooCSS.process { body a,b i { fontWeight 'bold' } }.css        |'body a, b i{font-weight: bold;}'
        GrooCSS.process { body a,section h1 { fontWeight 'bold' } }.css |'body a, section h1{font-weight: bold;}'
        GrooCSS.process { form li, tr { fontWeight 'bold' } }.css |'form li, tr{font-weight: bold;}'
    }

}
