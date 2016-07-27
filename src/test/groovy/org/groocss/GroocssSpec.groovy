package org.groocss

import spock.lang.Specification

/**
 * Created by adavis on 7/25/16.
 */
class GroocssSpec extends Specification {

    def should_create_a_css() {
        when:
        def css = GrooCSS.runBlock {
            sg('.class') {}
        }
        then:
        css != null
    }

    def should_create_rules() {
        when:
        def css = GrooCSS.runBlock {
            sel('.a') {}
            sel('.b') {}
            css('.c') {}
        }.css
        then:
        css.groups.size() == 3
    }


    def should_set_styles() {
        when:
        def css = GrooCSS.runBlock {
            sg('.a') {
                color('black')
                background('white')
                transition('500ms')
            }
        }.css
        then:
        css.groups.size() == 1
        "$css" == ".a{color: black;\n\tbackground: white;\n\ttransition: 500ms;}"
    }

}
