package org.groocss

import spock.lang.Specification

/** As part of 1.0 it should allow for a simple statically compiled syntax to enable IDE code completion. */
class StaticCompileSpec extends Specification {

    def "static css with sg"() {
        when:
        def css = GrooCSS.process {
            sg('.a') { color whiteSmoke.mix(blue) }
            sg('.b') { color tint(olive) fontSize 2.cm }
            'html body a[id]:hover:active'.sg {
                backgroundColor '440044'.color
                extend '.a'
            }
        }.css
        then:
        css.groups.size() == 3
    }

    def "static css with \$"() {
        when:
        def css = GrooCSS.process {
            $('.a') { color whiteSmoke.mix(blue) }
            $('.b') { color tint(olive) fontSize 2.cm }
            'html body a[id]:hover:active'.$ {
                backgroundColor '440044'.color
                extend '.a'
            }
        }.css
        then:
        css.groups.size() == 3
    }

}

