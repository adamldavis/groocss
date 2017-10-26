package org.groocss

import spock.lang.Specification
import spock.lang.Unroll

import static org.groocss.Transition.TransitionProperty.backgroundColor
import static org.groocss.Transition.TransitionTimingFunction.easeIn

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

    def "should allow creating keyframes within animation command"() {
        when:
        def css = GrooCSS.withConfig { noExts() } process {
            get_().move {
                animation('mymove') {
                    from {
                        top 0
                    }
                    50 % { top '50px' }
                    to {
                        top '100px'
                    }
                }
            }
        }
        then:
        "$css" == ".move{animation: mymove;}\n" +
                "@keyframes mymove {\nfrom{top: 0;}\n50%{top: 50px;}\nto{top: 100px;}\n}"
    }

    @Unroll
    def "should create Transition using DSL with #name"() {
        expect:
        def gcss = GrooCSS.withConfig { noExts() } process { div { transition closure } }
        "$gcss" == css
        where:
        name    | css                                            |  closure
        'linear'|"div{transition: top 2s linear 500ms;}"         |  {top '2s' linear '500ms'}
        'ease'  |"div{transition: border-color 1s ease 9ms;}"    |  {borderColor '1s' ease '9ms'}
        'e-out' |"div{transition: border-color 1s ease-out 9ms;}"|  {borderColor '1s' easeOut '9ms'}
        'cubic' |"div{transition: border-color 1s cubic-bezier(0, 0, 1, 1) 0;}"|{borderColor '1s' cubicBezier(0,0,1,1) delay '0'}
        'enums' |"div{transition: background-color 1s ease-in;}" |  {property backgroundColor duration '1s' timingFunction easeIn}
        'flex'  |"div{transition: flex 2s linear 500ms;}"        |  {flex '2s' linear '500ms'}
        'flex2' |"div{transition: flex-basis 3s linear 200ms;}"  |  {flexBasis '3s' linear '200ms'}
    }

    def "should allow creating Transitions with multiple values"() {
        when:
        def css = GrooCSS.withConfig { noExts() } process {
            get_().highlight {
                transition {flex '2s'} {borderColor '1s' ease '0'}
            }
        }
        then:
        "$css" == ".highlight{transition: flex 2s,border-color 1s ease 0;}"
    }

}