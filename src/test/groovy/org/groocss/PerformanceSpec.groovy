package org.groocss

import spock.lang.Specification

class PerformanceSpec extends Specification {

    def "test converting a file 100 times" () {
        when:
        long start = System.currentTimeMillis()
        def output = new File('build/test3.css')
        def input = new File('src/test/groovy/test3.css.groovy')
        println input.absolutePath
        100.times { GrooCSS.convertFile(new Config().noExts().compress(), input, output) }
        def css = output.text
        def time = System.currentTimeMillis() - start
        println "took $time ms"
        then:
        css.toString() == 'body{font-size: 2em;color: Black;}article{padding: 2em;}#thing{font-size: 200%;}' +
                '@keyframes test {from{color: Black;}to{color: Red;}}'
    }

}
