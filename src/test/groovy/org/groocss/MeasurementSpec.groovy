package org.groocss

import spock.lang.Specification

/**
 * Spock tests for Measurement class.
 */
class MeasurementSpec extends Specification {

    def "equals identity"() {
        expect:
        m1 == m1
        where:
        m1 = new Measurement(1, 'px')
    }

    def "equals with values"() {
        expect:
        m1 == m2
        where:
        m1 | m2
        new Measurement(1, 'px') | new Measurement(1, 'px')
        new Measurement(2, 'em') | new Measurement(2, 'em')
        new Measurement(3.14, 'rad') | new Measurement(3.14, 'rad')
    }

    def "not equals with values"() {
        expect:
        m1 != m2
        where:
        m1 | m2
        new Measurement(1, 'px') | new Measurement(2, 'px')
        new Measurement(2, 'em') | new Measurement(3, 'em')
        new Measurement(3.14, 'px') | new Measurement(3.1415, 'px')
    }

    def "not equals with units"() {
        expect:
        m1 != m2
        where:
        m1 | m2
        new Measurement(1, 'pt') | new Measurement(1, 'px')
        new Measurement(2, '%') | new Measurement(2, 'em')
        new Measurement(3.14, 'mm') | new Measurement(3.14, 'cm')
    }

    def "equals works with conversions"() {
        expect:
        new Measurement(1000, 'ms') == new Measurement(1, 's')
        new Measurement(6, 'pc') == new Measurement(1, 'in')
        new Measurement(72, 'pt') == new Measurement(1, 'in')
        new Measurement(25.4, 'mm') == new Measurement(1, 'in')
    }

    def "isPixel works regardless of value"() {
        expect:
        m1.isPixel()
        m2.isPixel()
        where:
        m1 | m2
        new Measurement(1, 'px') | new Measurement(111, 'px')
        new Measurement(2, 'px') | new Measurement(222, 'px')
        new Measurement(3, 'px') | new Measurement(333, 'px')
    }

    def "isPercent works"() {
        expect:
        new Measurement(100, '%').percent
    }

    def "isRelative works"() {
        expect:
        new Measurement(100, 'em').relative
        new Measurement(100, 'vh').relative
        new Measurement(100, 'vw').relative
    }

    def "isTime works"() {
        expect:
        new Measurement(100, 'ms').time
        new Measurement(100, 's').time
    }

    def "isDistance works"() {
        expect:
        new Measurement(100, 'pt').distance
        new Measurement(100, 'cm').distance
        new Measurement(100, 'in').distance
        new Measurement(100, 'mm').distance
    }

    def "isTrig works"() {
        expect:
        new Measurement(3.14, 'rad').trig
        new Measurement(180, 'deg').trig
    }

    def "isZero works"() {
        expect:
        new Measurement(0, 'rad').zero
        new Measurement(0, 'px').zero
        new Measurement(0.0, 'em').zero
        new Measurement(0.000, 'in').zero
    }

}
