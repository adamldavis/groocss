package org.groocss

import spock.lang.Specification

class VariableSpec extends Specification {

    def "should allow variables to be passed and resolved within DSL"() {
        expect:
        GrooCSS.convert(new Config().withVariables(kolor: '#123'), "table { color kolor }").toString() ==
                "table{color: #123;}"
        GrooCSS.withConfig { withVariables([kolor: '#123']) }.convert("table { color kolor }").toString() ==
                "table{color: #123;}"
    }

    def "should allow variables to be passed to withVariable and resolved within DSL"() {
        expect:
        GrooCSS.convert(new Config().withVariable('kolor', '#123'), "table { color kolor }").toString() ==
                "table{color: #123;}"
    }

    def "should allow variables to be passed to withVariables2 and resolved within DSL"() {
        expect:
        GrooCSS.convert(new Config().withVariables('kolor', '#123', 'w', 200.px), "table { color kolor width w}")
                .toString() == "table{color: #123;\n\twidth: 200px;}"
    }


    def "should allow variables to be passed to convert(Config, File, File) and resolved within DSL"() {
        given:
        def input = File.createTempFile("input", ".css.groovy")
        def output = File.createTempFile("output", ".css")
        input.text = "table { color kolor }"
        when:
        GrooCSS.convert(Config.builder().variables([kolor: '#123']).build(), input, output)
        then:
        "table{color: #123;}" == output.text
        output.delete()
        input.delete()
    }

    def "should allow variables to be passed to convertWithoutBase and resolved within DSL"() {
        given:
        def input = File.createTempFile("input", ".css.groovy")
        def output = File.createTempFile("output", ".css")
        input.text = "''.groocss { table { color kolor } }"
        when:
        GrooCSS.convertWithoutBase(input, output, "UTF-8", [kolor: '#123'])
        then:
        "table{color: #123;}" == output.text
        output.delete()
        input.delete()
    }


}
