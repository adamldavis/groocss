package org.groocss

import spock.lang.Specification

/**
 * Created by adavis on 8/9/17.
 */
class RawSpec extends Specification {

    def "raw should just include it"() {
        given:
        def css = GrooCSS.process {
            raw '::webkit-blaw { dostuff }'
        }
        expect:
        '::webkit-blaw { dostuff }' == "$css"
    }

    def "raw should print in order"() {
        given:
        def css = GrooCSS.process {
            raw 'raw1'
            raw 'raw2'
        }
        expect:
        'raw1\nraw2' == "$css"
    }
}
