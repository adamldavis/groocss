package org.groocss


import org.junit.BeforeClass
import spock.lang.Specification

class StringExtensionSpec extends Specification {

    def gcss

    @BeforeClass
    def initThreadLocal1() {
        gcss = new GrooCSS() // need to do this to set the ThreadLocal value
    }

    def "should create GrooCSS dsl from string"() {
        expect:
        'my css'.groocss { section { color red } } instanceof GrooCSS
    }

    def "should create GrooCSS dsl from string with config"() {
        expect:
        'main.css'.groocss(new Config(compress: true)) { body { color red } } instanceof GrooCSS
    }

    def "should create color using .color"() {
        expect:
        '123456'.color instanceof Color
    }

    def "should create color using .toColor()"() {
        expect:
        '123456'.toColor() instanceof Color
    }

    def "should create SG using .sg {} syntax"() {
        expect:
        def s = 'div.test a:hover'.sg { color 'white' }
        s instanceof StyleGroup
        s.selector == 'div.test a:hover'
    }

    def "should create SG using .\$ {} syntax"() {
        expect:
        def s = 'div.test a:hover'.$ { color 'white' }
        s instanceof StyleGroup
        s.selector == 'div.test a:hover'
    }

    def "should create SG using .id {} syntax"() {
        expect:
        def s = 'test'.id { color whiteSmoke }
        s instanceof StyleGroup
        s.selector == '#test'
    }

    def "should create KeyFrames using .keyframes {} syntax"() {
        expect:
        def s = 'test'.keyframes { 10% { color white } }
        s instanceof KeyFrames
        s.name == 'test'
    }

    def "should create Media using .media {} syntax"() {
        expect:
        def s = 'test'.media { body { fontSize 15.px } }
        s instanceof MediaCSS
        s.mediaRule == 'test'
    }
}
