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
            sg '.b', {}
        }
        then:
        "$css" == ".a{color: black;background: white;transition: 500ms;-webkit-transition: 500ms;}.b{}"
    }

    def should_charset() {
        when:
        def css = GrooCSS.process {
            charset utf8
            sg '.a', {}
        }
        then:
        "$css" == "@charset \"UTF-8\";\n.a{}"
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
        t1.text = "sg('.a') {}\nsg('.b') {}\nsg('.c') {}\n"
        when:
        GrooCSS.convert t1, t2
        then:
        "${t2.text}" == ".a{}\n.b{}\n.c{}"
    }

    def should_convert_file_compress() {
        File temp, t1, t2
        given:
        temp = new File('build/temp')
        temp.mkdirs()
        t1 = new File(temp, "t1.groocss")
        t2 = new File(temp, "t2.css")
        t1.text = "sg('.a') {}\nsg('.b') {}\nsg('.c') {}\n"
        when:
        GrooCSS.convert new Config(compress: true), t1, t2
        then:
        "${t2.text}" == ".a{}.b{}.c{}"
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
}
