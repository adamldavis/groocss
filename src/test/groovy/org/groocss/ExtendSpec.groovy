package org.groocss

import spock.lang.Specification

class ExtendSpec extends Specification {


    def "should extend StyleGroup"() {
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

    def "should extend StyleGroup twice "() {
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

    def "should extend using more complex dsl"() {
        when:
        def css = GrooCSS.process {
            input.foo {
                color black
                background white
            }
            sg '.bar', {
                extend(input.foo)
                color blue
            }
        }
        then:
        "$css" == "input.foo,.bar{color: Black;\n\tbackground: White;}\n.bar{color: Blue;}"
    }

    def "you should be able to extend a pseudo-class lazily"() {
        when:
        def css = GrooCSS.process {
            odd { backgroundColor '#eee' }
            li % even { extend(odd) }
        }
        then:
        "$css" == ':nth-child(odd),li:nth-child(even){background-color: #eee;}'
    }

}