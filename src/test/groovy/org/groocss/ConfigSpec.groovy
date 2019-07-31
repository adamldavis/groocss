package org.groocss

import org.groocss.proc.PlaceholderProcessor
import spock.lang.Specification
import spock.lang.Unroll

class ConfigSpec extends Specification {


    def "Config map constructor works" () {
        expect:
        new Config([compress: true]).compress
    }


    def "should be able to use Config builder "() {
        when:
        def css = GrooCSS.process(Config.builder().addMs(false).addOpera(false).compress(true).build()) {
            sg '.a', {left 0}
            sg '.b', {left 0}
        }
        then:
        "$css" == ".a{left: 0;}.b{left: 0;}"
    }

    def "should be able to use withConfig closure "() {
        when:
        def css = GrooCSS.withConfig { noExts().compress().utf8() }.process {
            sg '.a', {boxShadow('0 0 5px')}
            sg '.b', {boxShadow('0 0 5px')}
        }
        then:
        "$css" == "@charset \"UTF-8\";.a{box-shadow: 0 0 5px;}.b{box-shadow: 0 0 5px;}"
    }

    def "should optionally prettyPrint styles"() {
        when:
        def css = GrooCSS.withConfig { prettyPrint().noExts() }.process {
            sg '.a', {
                color('black')
                background('white')
                transition('500ms')
            }
        }
        then:
        "$css" ==
                '''.a {
    color: black;
    background: white;
    transition: 500ms;
}'''
    }

    def "should optionally prettyPrint colors"() {
        when:
        def css = GrooCSS.withConfig { prettyPrint().noExts() }.process {
            def sea = c('5512ab')
            sg '.sea', {
                color(sea.darker())
                background(sea.brighter())
            }
        }
        then:
        "$css" == ".sea {\n    color: #3b0c77;\n    background: #7919f4;\n}"
    }

    def "should optionally prettyPrint keyframes"() {
        when:
        def css = GrooCSS.withConfig { prettyPrint().noExts() }.process {
            keyframes('bounce') {
                frame(40) {
                    transform 'translateY(-30px)'
                }
                frame([0,20,50,80,100]) {
                    transform 'translateY(0)'
                }
            }
        }
        then:
        "$css" == """@keyframes bounce {
    40% {
        transform: translateY(-30px);
    }
    0%, 20%, 50%, 80%, 100% {
        transform: translateY(0);
    }
}"""
    }

    @Unroll
    def "should be able to configure using certain element names as classes: #css"() {
        expect:
        assert "${GrooCSS.withConfig { useAsClasses(['main', 'link']) }.process closure}" == css
        where:
        css | closure
        "div p.main{text-decoration: none;}" | { div p.main { textDecoration 'none' } }
        "div p a.link{text-decoration: none;}" | { div p ^ a.link { textDecoration 'none' } }
        "div .test a.link{text-decoration: none;}" | { div get_().test ^a.link { textDecoration 'none' } }
    }

    @Unroll
    def "should be able to convert underscores to dashes in classes: #css"() {
        expect:
        assert "${GrooCSS.withConfig { convertUnderline() }.process closure}" == css
        where:
        css | closure
        "div p.main-content{text-decoration: none;}"        | { div p.main_content { textDecoration 'none' } }
        "div p.not-your-dads-css{text-decoration: none;}"   | { div p.not_your_dads_css { textDecoration 'none' } }
        ".date-time{color: #123;}"      | { def dt = get_().date_time; sg(dt) {color '#123'} }
        "div.date-time{color: #123;}"   | { def dt = div.date_time; sg(dt) {color '#123'} }
    }

    def "should work with withProcessorClasses with PlaceholderProcessor"() {
        when:
        def css = GrooCSS.process(new Config().withProcessorClasses([PlaceholderProcessor])) {

            input**placeholder { color red }
        }
        then:
        "$css" == "input::placeholder{color: Red;}\ninput::-webkit-input-placeholder{color: Red;}"
    }

    def "should work with Properties"() {
        when:
        def props = new Properties()
        props.setProperty('compress', 'true')
        props.setProperty('convertUnderline', 'true')
        def css = GrooCSS.withProperties(props).process {
            p.test_me { color red }
        }
        then:
        "$css" == "p.test-me{color: Red;}"
    }

    def "should work with Properties and processors"() {
        when:
        def props = new Properties()
        props.setProperty('processors', 'org.groocss.proc.PlaceholderProcessor')
        def css = GrooCSS.process(new Config(props)) {

            input**placeholder { color red }
        }
        then:
        "$css" == "input::placeholder{color: Red;}\ninput::-webkit-input-placeholder{color: Red;}"
    }

    def "should work with Properties and false"() {
        when:
        def props = new Properties()
        props.setProperty('addWebkit', 'false')
        props.setProperty('addMoz', 'false')
        props.setProperty('convertUnderline', 'false')
        def css = GrooCSS.withProperties(props).process {
            kf('test') {
                from { width 10.em } to { width 20.em }
            }
        }
        then:
        "$css" == '@keyframes test {\nfrom{width: 10em;}\nto{width: 20em;}\n}'
    }

    def "should work with variables"() {
        when:
        def css = GrooCSS.withConfig { withVariables([testWidth: '20em']).noExts() }.process {
            kf('test') {
                from { width 10.em } to { width testWidth }
            }
        }
        then:
        "$css" == '@keyframes test {\nfrom{width: 10em;}\nto{width: 20em;}\n}'
    }

    def "should work with Properties and variables"() {
        when:
        def props = new Properties()
        props.setProperty('addWebkit', 'false')
        props.setProperty('variable.testWidth', '20em')
        def css = GrooCSS.withProperties(props).process {
            kf('test') {
                from { width 10.em } to { width testWidth }
            }
        }
        then:
        "$css" == '@keyframes test {\nfrom{width: 10em;}\nto{width: 20em;}\n}'
    }

}