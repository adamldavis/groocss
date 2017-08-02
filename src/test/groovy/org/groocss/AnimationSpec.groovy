package org.groocss

import spock.lang.Specification

class AnimationSpec extends Specification {


    def "should set all transitions"() {
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

    def "animationDelay should throw AssertionError for non-time Measurements"() {
        when:
        GrooCSS.process {
            p { animationDelay 1.em }
        }
        then:
        thrown(AssertionError)
    }

    def "should create keyframes"() {
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

    def "should create keyframe using from to"() {
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

    def "should use Integer mod to create keyframe"() {
        when:
        def css = GrooCSS.withConfig { noExts() }.process {
            keyframes('bounce') {
                40% {
                    translateY('-30px')
                }
            }
        }
        then:
        "$css" == """
        @keyframes bounce {
        40%{transform: translateY(-30px);}
        }
        """.stripIndent().trim()
    }

    def "should do multiple transforms "() {
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