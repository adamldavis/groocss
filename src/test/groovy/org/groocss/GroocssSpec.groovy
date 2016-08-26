package org.groocss

import spock.lang.Specification

/**
 * Created by adavis on 7/25/16.
 */
class GroocssSpec extends Specification {

    def should_create_a_css() {
        when:
        def css = GrooCSS.process {
            sg('.class') {}
        }
        then:
        css != null
    }

    def should_create_rules() {
        when:
        def css = GrooCSS.process {
            sel('.a') {}
            sel('.b') {}
            sg('.c') {}
        }.css
        then:
        css.groups.size() == 3
    }


    def should_set_styles() {
        when:
        def css = GrooCSS.process {
            sg '.a', {
                color('black')
                background('white')
                transition('500ms')
            }
        }.css
        then:
        css.groups.size() == 1
        "$css" == ".a{color: black;\n\tbackground: white;\n\ttransition: 500ms;" +
                "\n\t-webkit-transition: 500ms;\n\t-moz-transition: 500ms;\n\t-o-transition: 500ms;}"
    }

    def should_set_colors() {
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

    def should_create_keyframes() {
        when:
        def css = GrooCSS.process(new Config(addWebkit: false, addMoz: false, addOpera: false)) {
            keyframes('bounce') {
                frame(40) {
                    transform 'translateY(-30px)'
                }
                frame([0,20,50,80,100]) {
                    transform 'translateY(0)'
                }
            }
        }
        then:
        "$css" == """@keyframes bounce {
40%{transform: translateY(-30px);\n\t-ms-transform: translateY(-30px);}
0%, 20%, 50%, 80%, 100%{transform: translateY(0);\n\t-ms-transform: translateY(0);}
}"""
    }

    def should_create_keyframe_from_to() {
        when:
        def css = GrooCSS.process(new Config(addMoz: false, addOpera: false)) {
            keyframes('mymove') {
                from {
                    top 0
                }
                to {
                    top '100px'
                }
            }
        }
        then:
        "$css" == "@keyframes mymove {\nfrom{top: 0;}\nto{top: 100px;}\n}@-webkit-keyframes mymove {\nfrom{top: 0;}\nto{top: 100px;}\n}"
    }

    def should_create_font_face() {
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

    def should_create_colors_rgb() {
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

    def should_create_colors_rgba_alpha0() {
        when:
        def css = GrooCSS.process {
            sg '.colors', {
                background rgba(0,0,0,0)
            }
        }
        then:
        "$css" == ".colors{background: rgba(0, 0, 0, 0.0);}"
    }

    def should_create_named_colors() {
        when:
        def css = GrooCSS.process {
            sg '.colors', {
                color aliceBlue
            }
        }
        then:
        "$css" == ".colors{color: AliceBlue;}"
    }

    def should_convert_named_color_to_rgba() {
        when:
        def css = GrooCSS.process {
            sg '.colors', {
                color violet.alpha(0.5)
            }
        }
        then:
        "$css" == ".colors{color: rgba(238, 130, 238, 0.50);}"
    }

    def should_compress() {
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

    def should_charset() {
        when:
        def css = GrooCSS.process {
            charset utf8
        }
        then:
        "$css" == "@charset \"UTF-8\";\n"
    }

    def should_create_media() {
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

    def should_create_medias() {
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

    def should_be_fluent() {
        when:
        def css = GrooCSS.process {
            sg '.a', {
                add(style('-webkit-touch-callout', 'none')) << style('-webkit-text-size-adjust', 'none')
            }
        }
        then:
        "$css" == ".a{-webkit-touch-callout: none;\n\t-webkit-text-size-adjust: none;}"
    }

    def should_convert_file() {
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

    def should_convert_file_compress() {
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

    def should_add_StyleGroup() {
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

    def should_extend_StyleGroup() {
        when:
        def css = GrooCSS.process {
            sg '.a', {
                color black
                background white
            }
            sg '.b', {
                extend '.a'
                color blue
            }
        }
        then:
        "$css" == ".a,.b{color: Black;\n\tbackground: White;}\n.b{color: Blue;}"
    }

    def should_extend_StyleGroup_twice() {
        when:
        def css = GrooCSS.process {
            sg '.a', {
                color black
            }
            sg '.b', { extend '.a' }
            sg '.c', { extend '.a' }
        }
        then:
        "$css" == ".a,.b,.c{color: Black;}"
    }

    def should_do_multiple_transforms() {
        when:
        def css = GrooCSS.process(new Config(addMoz: false, addWebkit: false, addOpera: false, addMs: false)) {
            sg '.a', {
                translateX '1px'
                translateY '1px'
            }
        }
        then:
        "$css" == ".a{transform: translateX(1px) translateY(1px);}"
    }

    def should_use_Config_builder() {
        when:
        def css = GrooCSS.process(Config.builder().addMs(false).addOpera(false).compress(true).build()) {
            sg '.a', {left 0}
            sg '.b', {left 0}
        }
        then:
        "$css" == ".a{left: 0;}.b{left: 0;}"
    }

    def should_use_withConfig_closure() {
        when:
        def css = GrooCSS.withConfig { noExts().compress().utf8() }.process {
            sg '.a', {boxShadow('0 0 5px')}
            sg '.b', {boxShadow('0 0 5px')}
        }
        then:
        "$css" == "@charset \"UTF-8\";.a{box-shadow: 0 0 5px;}.b{box-shadow: 0 0 5px;}"
    }

    def should_use_html_elements() {
        when:
        def css = GrooCSS.withConfig { noExts() }.process {
            a '.blue', { color blue }
            input { borderRadius '1em' }
            inputButton { borderRadius 0 }
        }
        then:
        "$css" == "a.blue{color: Blue;}\ninput{border-radius: 1em;}\ninput [type=\"button\"]{border-radius: 0;}"
    }

    def should_use_a_hover() {
        when:
        def css = GrooCSS.process { a_hover {color 'red'}; a_focus {color 'red'} }
        then:
        "$css" == "a:hover{color: red;}\na:focus{color: red;}"
    }

    def should_use_subselect_to_add_classes() {
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

    def should_have_math_functions() {
        when:
        def css = GrooCSS.process {
            body { left "${sqrt(16)}em"; top sin(0) as int }
            div { left "${floor(10.123)}px" }
        }
        then:
        "$css" == "body{left: 4.0em;\n\ttop: 0;}\ndiv{left: 10px;}"
    }

    def getUnit_should_get_unit() {
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

    def should_extract_value_with_unit() {
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

    def should_add_unit() {
        given:
        def css = new GrooCSS()
        expect:
        css.unit(11, 'px') == '11px'
    }

    def should_convert_values() {
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

    def getImageSize_should_get_image_size() {
        given:
        def css = new GrooCSS()
        expect:
        css.getImageSize('black.png') == '640px 480px'
    }

    def should_add_units_to_integer() {
        when:
        def css = GrooCSS.process {
            a { fontSize 11.px }
        }
        then:
        "$css" == "a{font-size: 11px;}"
    }

    def should_add_sub_styles() {
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

    def should_use_underscore_method_missing() {
        when:
        def result = GrooCSS.process {
            get_().blue { color 'blue' }
        }
        then:
        "$result" == ".blue{color: blue;}"
    }

    def should_use_element_withClass() {
        when:
        def result = GrooCSS.process {
            table.withClass('blue') { color 'blue' }
        }
        then:
        "$result" == "table.blue{color: blue;}"
    }

    def should_use_element_method_missing() {
        when:
        def result = GrooCSS.process {
            table.blue { color 'blue' }
        }
        then:
        "$result" == "table.blue{color: blue;}"
    }

    def should_use_element_sel() {
        when:
        def result = GrooCSS.process {
            input.sel '[class$="test"]', { color 'blue' }
        }
        then:
        "$result" == 'input[class$="test"]{color: blue;}'
    }

    def should_use_element_putAt() {
        when:
        def result = GrooCSS.process {
            input['class$="test"'] = { color 'blue' }
        }
        then:
        "$result" == 'input[class$="test"]{color: blue;}'
    }

    def should_convert_file_with_underscore() {
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
    
    def "should prettyPrint styles"() {
        when:
        def css = GrooCSS.withConfig { prettyPrint().noExts() }.process {
            sg '.a', {
                color('black')
                background('white')
                transition('500ms')
            }
        }
        then:
        "$css" ==
'''.a {
    color: black;
    background: white;
    transition: 500ms;
}'''
    }

    def "should prettyPrint colors"() {
        when:
        def css = GrooCSS.withConfig { prettyPrint().noExts() }.process {
            def sea = c('5512ab')
            sg '.sea', {
                color(sea.darker())
                background(sea.brighter())
            }
        }
        then:
        "$css" == ".sea {\n    color: #3b0c77;\n    background: #7919f4;\n}"
    }

    def "should prettyPrint keyframes"() {
        when:
        def css = GrooCSS.withConfig { prettyPrint().noExts() }.process {
            keyframes('bounce') {
                frame(40) {
                    transform 'translateY(-30px)'
                }
                frame([0,20,50,80,100]) {
                    transform 'translateY(0)'
                }
            }
        }
        then:
        "$css" == """@keyframes bounce {
    40% {
        transform: translateY(-30px);
    }
    0%, 20%, 50%, 80%, 100% {
        transform: translateY(0);
    }
}"""
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

    def "should extend using dsl"() {
        when:
        def css = GrooCSS.process {
            input {
                color black
                background white
            }
            sg '.b', {
                extend input
                color blue
            }
        }
        then:
        "$css" == "input,.b{color: Black;\n\tbackground: White;}\n.b{color: Blue;}"
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

}
