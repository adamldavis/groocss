package org.groocss

import spock.lang.Specification

/**
 * Created by adavis on 8/10/17.
 */
class CommentSpec extends Specification {

    def "comment should include comment in output"() {
        given:
        def css = GrooCSS.process {
            comment 'I am Groocss!'
        }
        expect:
        '/* I am Groocss! */' == "$css"
    }

    def "comment should print in order"() {
        given:
        def css = GrooCSS.process {
            comment 'comment1'
            a { fontSize 18.px }
            comment 'comment2'
            aside { fontSize 15.px }
        }
        expect:
        "$css" == '''
        /* comment1 */
        a{font-size: 18px;}
        /* comment2 */
        aside{font-size: 15px;}'''.stripIndent().trim()
    }
}
