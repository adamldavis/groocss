package org.groocss

import org.junit.After
import org.junit.Before
import spock.lang.Specification

class NumberExtensionSpec extends  Specification {


    GrooCSS gcss

    @Before
    def initThreadLocal1() {
        gcss = new GrooCSS(new Config(addWebkit: false)) // need to do this to set the ThreadLocal value
    }

    @After
    def nullifyThreadLocal() {
        GrooCSS.threadLocalInstance.set(null) // so we don't pollute
    }

    def "should create color using .color"() {
        expect:
        0x123456.color instanceof Color
    }
    def "should create color using .toColor()"() {
        expect:
        0x123456.toColor() instanceof Color
    }

    def "should create keyframes"() {
        expect:
        gcss.keyframes('showIt') {
            10 % { color 'white' }
        }
        gcss.toString().contains "@keyframes showIt {\n10%{color: white;}\n}"
    }

    def "should create measurements"() {
        expect:
        //sizes
        1.px
        0.001.m
        10.cm
        100.mm
        10.pt
        100.rem
        10.em
        11.1.in
        //times
        10.s
        0.5.s
        100.ms
        //trigs
        10.rad
        180.deg
    }

    def "should create percentage using percent"() {
        expect:
        10.percent == new Measurement(10, '%')
    }

    def "should create percentage using % _"() {
        given:
        def a = new Underscore()
        expect:
        10 % a == new Measurement(10, '%')
        ''.groocss { body { width 100%get_() } }.toString() == 'body{width: 100%;}'
    }

}
