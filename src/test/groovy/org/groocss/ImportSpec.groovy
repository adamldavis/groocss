package org.groocss

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by adavis on 8/10/17.
 */
class ImportSpec extends Specification {
    
    @Shared
    File otherCss
    
    def setup() {
        otherCss = new File(File.createTempDir(),"other.css")
    }

    @Unroll
    def "import should just import it: #name"() {
        given:
        otherCss.text = "a {color blue}"
        expect:
        def css = GrooCSS.process closure
        'a{color: Blue;}' == "$css".trim()
        where:
        name            | closure
        'File name'     | { importFile otherCss.absoluteFile.toString() }
        'File'          | { importFile otherCss.absoluteFile }
        'String'        | { importString otherCss.text }
        'InputStream'   | { importStream otherCss.newInputStream() }
    }

    def "import should be put wherever its put"() {
        given:
        otherCss.text = "a {color blue}"
        expect:
        def css = GrooCSS.process {
            importFile otherCss.absoluteFile
            header { fontSize '15pt' }
        }
        'a{color: Blue;}\nheader{font-size: 15pt;}' == "$css".trim()
    }

    def "import should allow parameters"() {
        given:
        otherCss.text = "a {color linkColor}"
        expect:
        def css = GrooCSS.process {
            importFile otherCss.absoluteFile, linkColor: '#456789'
        }
        'a{color: #456789;}' == "$css".trim()
    }

    def "import should CSS before and after and keep original Config"() {
        given:
        otherCss.text = "a {color linkColor}"
        expect:
        def css = GrooCSS.process(new Config(compress: true)) {
            table { padding 5.px }
            importFile otherCss.absoluteFile, linkColor: '#456789'
            div { padding 2.px margin 1.px }
        }
        'table{padding: 5px;}a{color: #456789;}div{padding: 2px;margin: 1px;}' == "$css".trim()
    }

    @Unroll
    def "import should allow #type parameter"() {
        given:
        otherCss.text = "a {color linkColor}"
        expect:
        def css = GrooCSS.process {
            importFile otherCss.absoluteFile, linkColor: value, param2: 123
        }
        'a{color: #123456;}' == "$css".trim()
        where:
        type        | value
        'String'    | '#123456'
        'Integer'   | 0x123456
        'Color'     | new Color(0x123456)
    }
}
